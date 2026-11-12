-- ════════════════════════════════════════════════════════════════════════════════════════════
-- DentalPro (DentalSoft V1.0) — Schéma MySQL complet (20 tables)
--
-- Exécution :  mysql -u root -p < src/main/resources/db/schema.sql
--              puis           mysql -u root -p < src/main/resources/db/seed.sql
--
-- ATTENTION : ce script SUPPRIME puis recrée la base dentalSoftBD (toutes les données sont perdues).
--
-- Conventions (dossier de spécification, chapitre 2) :
--   • InnoDB + utf8mb4 ; identifiants BIGINT ; montants DECIMAL(12,2) (RG-08) ;
--   • pk_ clé primaire, fk_ clé étrangère, uq_ unicité, chk_ contrainte de domaine ;
--   • aucune suppression en cascade sur les données métier : patients, consultations, factures
--     et paiements ne sont jamais supprimés physiquement (archivage ou annulation à la place) ;
--   • les règles entre plusieurs lignes (chevauchement de RDV, reste à payer, au plus une
--     facture validée…) sont contrôlées dans les services, sous transaction et verrou.
-- Requiert MySQL 8.0.16 ou plus récent (contraintes CHECK appliquées).
-- ════════════════════════════════════════════════════════════════════════════════════════════

DROP DATABASE IF EXISTS dentalSoftBD;
CREATE DATABASE dentalSoftBD
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE dentalSoftBD;

-- ────────────────────────────────────────────────────────────────────────────────────────────
-- ADMINISTRATION : cabinet, comptes et rôles
-- ────────────────────────────────────────────────────────────────────────────────────────────

-- Configuration du cabinet unique (BF-10, CA-07) : une seule ligne autorisée (id = 1).
CREATE TABLE cabinet (
    id          BIGINT       NOT NULL,
    nom         VARCHAR(120) NOT NULL,
    adresse     VARCHAR(255) NULL,
    telephone   VARCHAR(30)  NULL,
    email       VARCHAR(150) NULL,
    logo_path   VARCHAR(255) NULL,
    CONSTRAINT pk_cabinet PRIMARY KEY (id),
    CONSTRAINT chk_cabinet_unique CHECK (id = 1)
) ENGINE = InnoDB;

-- Compte de connexion (BF-04, BF-05). password_hash : "algorithme$iterations$sel$empreinte".
CREATE TABLE utilisateur (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    login          VARCHAR(80)  NOT NULL,
    password_hash  VARCHAR(512) NOT NULL,
    nom            VARCHAR(100) NOT NULL,
    prenom         VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NULL,
    telephone      VARCHAR(30)  NULL,
    adresse        VARCHAR(255) NULL,
    specialite     VARCHAR(100) NULL,
    actif          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_utilisateur PRIMARY KEY (id),
    CONSTRAINT uq_utilisateur_login UNIQUE (login)
) ENGINE = InnoDB;

-- Référentiel des rôles.
CREATE TABLE role (
    code     VARCHAR(20) NOT NULL,
    libelle  VARCHAR(50) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (code),
    CONSTRAINT chk_role_code CHECK (code IN ('ADMIN', 'MEDECIN', 'SECRETAIRE'))
) ENGINE = InnoDB;

-- Association compte ↔ rôle : un compte peut avoir plusieurs rôles (BF-06).
CREATE TABLE utilisateur_role (
    utilisateur_id  BIGINT      NOT NULL,
    role_code       VARCHAR(20) NOT NULL,
    CONSTRAINT pk_utilisateur_role PRIMARY KEY (utilisateur_id, role_code),
    CONSTRAINT fk_utilisateur_role_utilisateur FOREIGN KEY (utilisateur_id)
        REFERENCES utilisateur (id) ON DELETE CASCADE,
    CONSTRAINT fk_utilisateur_role_role FOREIGN KEY (role_code)
        REFERENCES role (code)
) ENGINE = InnoDB;

-- ────────────────────────────────────────────────────────────────────────────────────────────
-- ACCUEIL ET AGENDA : patients, dossiers, rendez-vous, indisponibilités
-- ────────────────────────────────────────────────────────────────────────────────────────────

-- Fiche administrative (BF-11 à BF-13). Archivage au lieu de suppression (RG-04).
-- CIN et passeport sont facultatifs mais uniques lorsqu'ils sont renseignés.
CREATE TABLE patient (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nom             VARCHAR(100) NOT NULL,
    prenom          VARCHAR(100) NOT NULL,
    date_naissance  DATE         NOT NULL,
    sexe            VARCHAR(20)  NULL,
    cin             VARCHAR(30)  NULL,
    passeport       VARCHAR(30)  NULL,
    email           VARCHAR(150) NULL,
    telephone       VARCHAR(30)  NULL,
    adresse         VARCHAR(255) NULL,
    assurance       VARCHAR(80)  NULL,
    archive         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_patient PRIMARY KEY (id),
    CONSTRAINT uq_patient_cin UNIQUE (cin),
    CONSTRAINT uq_patient_passeport UNIQUE (passeport),
    CONSTRAINT chk_patient_sexe CHECK (sexe IS NULL OR sexe IN ('HOMME', 'FEMME'))
) ENGINE = InnoDB;

-- Index de recherche (CA-09 : nom, téléphone ; BNF-05 : < 2 s sur 1000 patients).
CREATE INDEX idx_patient_nom_prenom ON patient (nom, prenom);
CREATE INDEX idx_patient_telephone ON patient (telephone);

-- Dossier médical unique par patient, créé dans la même transaction (RG-01, CA-11).
-- medecin_id : médecin référent, facultatif (la secrétaire peut créer le patient).
CREATE TABLE dossier_medical (
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    patient_id     BIGINT   NOT NULL,
    date_creation  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    medecin_id     BIGINT   NULL,
    CONSTRAINT pk_dossier_medical PRIMARY KEY (id),
    CONSTRAINT uq_dossier_medical_patient UNIQUE (patient_id),
    CONSTRAINT fk_dossier_medical_patient FOREIGN KEY (patient_id) REFERENCES patient (id),
    CONSTRAINT fk_dossier_medical_medecin FOREIGN KEY (medecin_id) REFERENCES utilisateur (id)
) ENGINE = InnoDB;

-- Rendez-vous (BF-17, BF-18, RG-05). Le non-chevauchement des RDV actifs d'un même médecin
-- (CA-18) est vérifié par le service sous verrou (SELECT … FOR UPDATE).
-- Cycle de vie : PLANIFIE → CONFIRME → ARRIVE → EN_COURS → TERMINE ; ou ANNULE / ABSENT.
CREATE TABLE rdv (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    dossier_id  BIGINT       NOT NULL,
    medecin_id  BIGINT       NOT NULL,
    debut       DATETIME     NOT NULL,
    fin         DATETIME     NOT NULL,
    motif       VARCHAR(255) NULL,
    statut      VARCHAR(20)  NOT NULL DEFAULT 'PLANIFIE',
    arrivee_at  DATETIME     NULL,
    CONSTRAINT pk_rdv PRIMARY KEY (id),
    CONSTRAINT fk_rdv_dossier FOREIGN KEY (dossier_id) REFERENCES dossier_medical (id),
    CONSTRAINT fk_rdv_medecin FOREIGN KEY (medecin_id) REFERENCES utilisateur (id),
    CONSTRAINT chk_rdv_periode CHECK (fin > debut),
    CONSTRAINT chk_rdv_statut CHECK (statut IN
        ('PLANIFIE', 'CONFIRME', 'ARRIVE', 'EN_COURS', 'TERMINE', 'ANNULE', 'ABSENT'))
) ENGINE = InnoDB;

-- Agenda d'un médecin et contrôle de chevauchement (CA-15, CA-18).
CREATE INDEX idx_rdv_medecin_periode ON rdv (medecin_id, debut, fin);
-- File d'attente du jour triée sur l'heure d'arrivée (BF-19, CA-19).
CREATE INDEX idx_rdv_statut_arrivee ON rdv (statut, arrivee_at);

-- Périodes d'indisponibilité du médecin (BF-16, CA-16).
CREATE TABLE indisponibilite (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    medecin_id  BIGINT       NOT NULL,
    debut       DATETIME     NOT NULL,
    fin         DATETIME     NOT NULL,
    motif       VARCHAR(150) NULL,
    CONSTRAINT pk_indisponibilite PRIMARY KEY (id),
    CONSTRAINT fk_indisponibilite_medecin FOREIGN KEY (medecin_id) REFERENCES utilisateur (id),
    CONSTRAINT chk_indisponibilite_periode CHECK (fin > debut)
) ENGINE = InnoDB;

CREATE INDEX idx_indisponibilite_medecin_periode ON indisponibilite (medecin_id, debut, fin);

-- ────────────────────────────────────────────────────────────────────────────────────────────
-- CLINIQUE : antécédents, consultations, actes, interventions, ordonnances, certificats
-- ────────────────────────────────────────────────────────────────────────────────────────────

-- Catalogue des antécédents (liste pédagogique ; aucune décision clinique automatique).
CREATE TABLE antecedent (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    libelle    VARCHAR(150) NOT NULL,
    categorie  VARCHAR(60)  NOT NULL,
    actif      BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_antecedent PRIMARY KEY (id),
    CONSTRAINT uq_antecedent_libelle UNIQUE (libelle)
) ENGINE = InnoDB;

-- Antécédent associé à un dossier, avec risque et commentaire propres au patient (BF-13, CA-13).
CREATE TABLE dossier_antecedent (
    dossier_id     BIGINT      NOT NULL,
    antecedent_id  BIGINT      NOT NULL,
    niveau_risque  VARCHAR(20) NOT NULL,
    commentaire    TEXT        NULL,
    actif          BOOLEAN     NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_dossier_antecedent PRIMARY KEY (dossier_id, antecedent_id),
    CONSTRAINT fk_dossier_antecedent_dossier FOREIGN KEY (dossier_id) REFERENCES dossier_medical (id),
    CONSTRAINT fk_dossier_antecedent_antecedent FOREIGN KEY (antecedent_id) REFERENCES antecedent (id),
    CONSTRAINT chk_dossier_antecedent_risque CHECK (niveau_risque IN ('FAIBLE', 'MOYEN', 'ELEVE'))
) ENGINE = InnoDB;

-- Rencontre clinique (BF-20, BF-21, RG-06). Un RDV produit au plus une consultation (CA-20).
-- observation : donnée clinique réservée au médecin (jamais retournée à la secrétaire).
CREATE TABLE consultation (
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    dossier_id         BIGINT      NOT NULL,
    medecin_id         BIGINT      NOT NULL,
    rdv_id             BIGINT      NULL,
    date_consultation  DATETIME    NOT NULL,
    observation        TEXT        NULL,
    statut             VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    CONSTRAINT pk_consultation PRIMARY KEY (id),
    CONSTRAINT uq_consultation_rdv UNIQUE (rdv_id),
    CONSTRAINT fk_consultation_dossier FOREIGN KEY (dossier_id) REFERENCES dossier_medical (id),
    CONSTRAINT fk_consultation_medecin FOREIGN KEY (medecin_id) REFERENCES utilisateur (id),
    CONSTRAINT fk_consultation_rdv FOREIGN KEY (rdv_id) REFERENCES rdv (id),
    CONSTRAINT chk_consultation_statut CHECK (statut IN ('BROUILLON', 'VALIDEE'))
) ENGINE = InnoDB;

CREATE INDEX idx_consultation_date ON consultation (date_consultation);

-- Catalogue des actes dentaires et tarifs de référence (BF-22, CA-22).
CREATE TABLE acte (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    libelle    VARCHAR(150)  NOT NULL,
    categorie  VARCHAR(60)   NULL,
    prix_base  DECIMAL(12,2) NOT NULL,
    actif      BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_acte PRIMARY KEY (id),
    CONSTRAINT uq_acte_libelle UNIQUE (libelle),
    CONSTRAINT chk_acte_prix CHECK (prix_base > 0)
) ENGINE = InnoDB;

-- Acte effectué pendant une consultation (BF-23, BF-24, RG-07).
-- Le libellé et le prix sont copiés au moment de l'enregistrement : un changement du
-- catalogue ne modifie pas l'historique (CA-22, CA-23). dent NULL = acte global (CA-24).
CREATE TABLE intervention (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    consultation_id        BIGINT        NOT NULL,
    acte_id                BIGINT        NOT NULL,
    libelle_acte_snapshot  VARCHAR(150)  NOT NULL,
    dent                   VARCHAR(10)   NULL,
    quantite               INT           NOT NULL DEFAULT 1,
    prix_unitaire          DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_intervention PRIMARY KEY (id),
    CONSTRAINT fk_intervention_consultation FOREIGN KEY (consultation_id) REFERENCES consultation (id),
    CONSTRAINT fk_intervention_acte FOREIGN KEY (acte_id) REFERENCES acte (id),
    CONSTRAINT chk_intervention_quantite CHECK (quantite > 0),
    CONSTRAINT chk_intervention_prix CHECK (prix_unitaire >= 0)
) ENGINE = InnoDB;

-- Référentiel des médicaments (BF-25, CA-25) : un médicament inactif n'est plus prescriptible.
CREATE TABLE medicament (
    id     BIGINT       NOT NULL AUTO_INCREMENT,
    nom    VARCHAR(150) NOT NULL,
    forme  VARCHAR(80)  NULL,
    actif  BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_medicament PRIMARY KEY (id),
    CONSTRAINT uq_medicament_nom_forme UNIQUE (nom, forme)
) ENGINE = InnoDB;

-- Ordonnance (BF-26, RG-11). date_emission est renseignée à l'émission.
CREATE TABLE ordonnance (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    consultation_id  BIGINT      NOT NULL,
    date_emission    DATETIME    NULL,
    statut           VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    CONSTRAINT pk_ordonnance PRIMARY KEY (id),
    CONSTRAINT fk_ordonnance_consultation FOREIGN KEY (consultation_id) REFERENCES consultation (id),
    CONSTRAINT chk_ordonnance_statut CHECK (statut IN ('BROUILLON', 'EMISE')),
    CONSTRAINT chk_ordonnance_emission CHECK (statut = 'BROUILLON' OR date_emission IS NOT NULL)
) ENGINE = InnoDB;

-- Ligne d'ordonnance. Le nom du médicament est conservé tel qu'à l'émission.
CREATE TABLE prescription (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    ordonnance_id            BIGINT       NOT NULL,
    medicament_id            BIGINT       NOT NULL,
    nom_medicament_snapshot  VARCHAR(150) NOT NULL,
    quantite                 INT          NOT NULL,
    frequence                VARCHAR(150) NOT NULL,
    duree_jours              INT          NOT NULL,
    CONSTRAINT pk_prescription PRIMARY KEY (id),
    CONSTRAINT fk_prescription_ordonnance FOREIGN KEY (ordonnance_id) REFERENCES ordonnance (id),
    CONSTRAINT fk_prescription_medicament FOREIGN KEY (medicament_id) REFERENCES medicament (id),
    CONSTRAINT chk_prescription_quantite CHECK (quantite > 0),
    CONSTRAINT chk_prescription_duree CHECK (duree_jours > 0)
) ENGINE = InnoDB;

-- Certificat médical (BF-27, CA-27) : date_fin >= date_debut.
CREATE TABLE certificat (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    consultation_id  BIGINT      NOT NULL,
    date_debut       DATE        NOT NULL,
    date_fin         DATE        NOT NULL,
    motif            TEXT        NULL,
    date_emission    DATETIME    NULL,
    statut           VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    CONSTRAINT pk_certificat PRIMARY KEY (id),
    CONSTRAINT fk_certificat_consultation FOREIGN KEY (consultation_id) REFERENCES consultation (id),
    CONSTRAINT chk_certificat_periode CHECK (date_fin >= date_debut),
    CONSTRAINT chk_certificat_statut CHECK (statut IN ('BROUILLON', 'EMIS')),
    CONSTRAINT chk_certificat_emission CHECK (statut = 'BROUILLON' OR date_emission IS NOT NULL)
) ENGINE = InnoDB;

-- ────────────────────────────────────────────────────────────────────────────────────────────
-- CAISSE ET PILOTAGE : factures, paiements, charges
-- ────────────────────────────────────────────────────────────────────────────────────────────

-- Facture (BF-28, BF-29, RG-09). Une consultation produit au plus une facture.
-- total : calculé par le service à partir des interventions, jamais saisi ; figé à la validation.
CREATE TABLE facture (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    consultation_id   BIGINT        NOT NULL,
    numero            VARCHAR(40)   NOT NULL,
    date_facture      DATETIME      NOT NULL,
    total             DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    statut            VARCHAR(20)   NOT NULL DEFAULT 'BROUILLON',
    motif_annulation  VARCHAR(255)  NULL,
    CONSTRAINT pk_facture PRIMARY KEY (id),
    CONSTRAINT uq_facture_numero UNIQUE (numero),
    CONSTRAINT uq_facture_consultation UNIQUE (consultation_id),
    CONSTRAINT fk_facture_consultation FOREIGN KEY (consultation_id) REFERENCES consultation (id),
    CONSTRAINT chk_facture_total CHECK (total >= 0),
    CONSTRAINT chk_facture_total_valide CHECK (statut <> 'VALIDEE' OR total > 0),
    CONSTRAINT chk_facture_statut CHECK (statut IN ('BROUILLON', 'VALIDEE', 'ANNULEE')),
    CONSTRAINT chk_facture_annulation CHECK (statut <> 'ANNULEE' OR motif_annulation IS NOT NULL)
) ENGINE = InnoDB;

CREATE INDEX idx_facture_date ON facture (date_facture);

-- Encaissement (BF-30, RG-10). reference unique (UUID de la soumission) : un double clic ou
-- un nouvel essai de la même soumission ne crée pas de second paiement (CA-30).
-- Le non-dépassement du reste à payer est contrôlé par le service sous verrou de la facture.
CREATE TABLE paiement (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    facture_id     BIGINT        NOT NULL,
    auteur_id      BIGINT        NOT NULL,
    reference      CHAR(36)      NOT NULL,
    date_paiement  DATETIME      NOT NULL,
    montant        DECIMAL(12,2) NOT NULL,
    mode           VARCHAR(20)   NOT NULL,
    CONSTRAINT pk_paiement PRIMARY KEY (id),
    CONSTRAINT uq_paiement_reference UNIQUE (reference),
    CONSTRAINT fk_paiement_facture FOREIGN KEY (facture_id) REFERENCES facture (id),
    CONSTRAINT fk_paiement_auteur FOREIGN KEY (auteur_id) REFERENCES utilisateur (id),
    CONSTRAINT chk_paiement_montant CHECK (montant > 0),
    CONSTRAINT chk_paiement_mode CHECK (mode IN ('ESPECES', 'CARTE', 'CHEQUE', 'VIREMENT', 'AUTRE'))
) ENGINE = InnoDB;

-- Recettes de la période (RG-12, BNF-05 : tableau de bord < 3 s).
CREATE INDEX idx_paiement_date ON paiement (date_paiement);

-- Dépense du cabinet (BF-32, CA-32) : diminue le solde sans toucher aux factures.
CREATE TABLE charge (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    auteur_id    BIGINT        NOT NULL,
    titre        VARCHAR(150)  NOT NULL,
    description  VARCHAR(255)  NULL,
    montant      DECIMAL(12,2) NOT NULL,
    date_charge  DATETIME      NOT NULL,
    CONSTRAINT pk_charge PRIMARY KEY (id),
    CONSTRAINT fk_charge_auteur FOREIGN KEY (auteur_id) REFERENCES utilisateur (id),
    CONSTRAINT chk_charge_montant CHECK (montant > 0)
) ENGINE = InnoDB;

CREATE INDEX idx_charge_date ON charge (date_charge);
