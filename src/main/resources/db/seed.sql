-- ════════════════════════════════════════════════════════════════════════════════════════════
-- DentalPro (DentalSoft V1.0) — Données initiales de démonstration
--
-- Exécution (après schema.sql) :  mysql -u root -p < src/main/resources/db/seed.sql
--
-- TOUTES LES DONNÉES SONT FICTIVES (RG-13, BNF-02) : noms, CIN (préfixe ZZ), téléphones
-- (06000000xx) et e-mails (@example.com) n'appartiennent à aucune personne réelle.
--
-- Les dates sont relatives au jour d'exécution (@aujourdhui) : le tableau de bord, l'agenda et
-- la file d'attente du jour ont donc toujours des données, quel que soit le jour de la démo.
--
-- Comptes de démonstration (mots de passe de test uniquement, stockés sous forme d'empreinte
-- PBKDF2WithHmacSHA256, 600 000 itérations, sel aléatoire de 16 octets) :
--   login               mot de passe       rôle          état
--   admin               Admin#2026         ADMIN         actif
--   dr.benjelloun       Medecin#2026       MEDECIN       actif
--   dr.amrani           Medecin#2026       MEDECIN       actif
--   s.kadiri            Secretaire#2026    SECRETAIRE    actif
--   ancien.secretaire   Secretaire#2026    SECRETAIRE    désactivé (CA-05 : connexion refusée)
--
-- Scénarios de référence préparés :
--   • Facture FAC-2026-0001 = 400,00 + 450,00 = 850,00 MAD (CA-28) ; un paiement de 300,00 MAD
--     est déjà enregistré → reste 550,00 MAD (CA-31) ; payer 550,00 la solde (CA-30).
--   • Consultation n°3 validée sans facture : sert à démontrer la génération d'une facture.
--   • File d'attente du jour : 2 patients ARRIVE, triés par heure d'arrivée (CA-19).
--   • Même antécédent (allergie pénicilline) sur deux dossiers, commentaires distincts (CA-13).
--   • Patient archivé (Kettani Salma) : consultable, pas de nouveau RDV (CA-10).
-- ════════════════════════════════════════════════════════════════════════════════════════════

USE dentalSoftBD;

SET @aujourdhui = CURDATE();

START TRANSACTION;

-- ─── Cabinet ──────────────────────────────────────────────────────────────────────────────────
INSERT INTO cabinet (id, nom, adresse, telephone, email, logo_path) VALUES
    (1, 'Cabinet Dentaire YOU SMILE', '12, avenue Fictive, Agdal, Rabat', '0537000000',
     'contact@yousmile.example.com', NULL);

-- ─── Rôles ────────────────────────────────────────────────────────────────────────────────────
INSERT INTO role (code, libelle) VALUES
    ('ADMIN',      'Administrateur'),
    ('MEDECIN',    'Médecin'),
    ('SECRETAIRE', 'Secrétaire');

-- ─── Comptes utilisateurs ─────────────────────────────────────────────────────────────────────
INSERT INTO utilisateur (id, login, password_hash, nom, prenom, email, telephone, adresse, specialite, actif) VALUES
    (1, 'admin',
     'PBKDF2WithHmacSHA256$600000$AkS3iehtGLKrmenSyNMdKQ==$S1hOGol6zMwa6wthUgOFpWX0ZciNWUQ0bnBugmoizHE=',
     'Système', 'Admin', 'admin@yousmile.example.com', NULL, NULL, NULL, TRUE),
    (2, 'dr.benjelloun',
     'PBKDF2WithHmacSHA256$600000$kOKORUmmP3GGoKZVDTAiWA==$JyaZKMpBE44ysa/u9oL4x3Oj+MrrPHHTx5fXhbaBqD4=',
     'Benjelloun', 'Karim', 'k.benjelloun@yousmile.example.com', '0600000101',
     '12, avenue Fictive, Agdal, Rabat', 'Chirurgie dentaire', TRUE),
    (3, 'dr.amrani',
     'PBKDF2WithHmacSHA256$600000$X2KHpEqiAccFE/jiv7F8YQ==$AwQIHD8hxlgD88UwYz/4gF0O10PESc8I3MCZTb06dLA=',
     'Amrani', 'Nadia', 'n.amrani@yousmile.example.com', '0600000102',
     '12, avenue Fictive, Agdal, Rabat', 'Orthodontie', TRUE),
    (4, 's.kadiri',
     'PBKDF2WithHmacSHA256$600000$9oNTmN7e+Tucg9Cesg/tpw==$a7IRl0elklKhmZqrgv1BCRdbEYQig1HjXSc/nrEyZfU=',
     'Kadiri', 'Salma', 's.kadiri@yousmile.example.com', '0600000103', NULL, NULL, TRUE),
    (5, 'ancien.secretaire',
     'PBKDF2WithHmacSHA256$600000$v1WWgPMup6/HtUebDbPUyQ==$yPzOLjdyxX6oLYEnUTlRplG59ifls+KlYIeHwgm+a8s=',
     'Mansouri', 'Hind', NULL, NULL, NULL, NULL, FALSE);

INSERT INTO utilisateur_role (utilisateur_id, role_code) VALUES
    (1, 'ADMIN'),
    (2, 'MEDECIN'),
    (3, 'MEDECIN'),
    (4, 'SECRETAIRE'),
    (5, 'SECRETAIRE');

-- ─── Patients (fiches administratives) ────────────────────────────────────────────────────────
INSERT INTO patient (id, nom, prenom, date_naissance, sexe, cin, passeport, email, telephone, adresse, assurance, archive) VALUES
    (1,  'Berrada',   'Fadoua',  '2005-03-14', 'FEMME', 'ZZ100001', NULL, 'fadoua.berrada@example.com', '0600000001', 'Agdal, Rabat',     'CNSS',  FALSE),
    (2,  'Benali',    'Omar',    '2003-07-02', 'HOMME', 'ZZ100002', NULL, NULL,                         '0600000002', 'Hay Riad, Rabat',  'CNOPS', FALSE),
    (3,  'Idrissi',   'Souhail', '2002-11-21', 'HOMME', 'ZZ100003', NULL, 'souhail.idrissi@example.com','0600000003', 'Hassan, Rabat',    NULL,    FALSE),
    (4,  'Tazi',      'Amine',   '2007-01-09', 'HOMME', NULL,       NULL, NULL,                         '0600000004', 'Témara',           'CNSS',  FALSE),
    (5,  'Lahlou',    'Tarek',   '2009-05-30', 'HOMME', NULL,       NULL, NULL,                         '0600000005', 'Salé',             'Assurance privée', FALSE),
    (6,  'Chraibi',   'Amina',   '2006-02-17', 'FEMME', 'ZZ100006', NULL, 'amina.chraibi@example.com',  '0600000006', 'Océan, Rabat',     'CNOPS', FALSE),
    (7,  'Bennani',   'Tanae',   '1996-09-12', 'FEMME', 'ZZ100007', NULL, NULL,                         '0600000007', 'Souissi, Rabat',   'Mutuelle privée', FALSE),
    (8,  'Alaoui',    'Ayman',   '2000-04-25', 'HOMME', 'ZZ100008', NULL, 'ayman.alaoui@example.com',   '0600000008', 'Kénitra',          NULL,    FALSE),
    (9,  'Fassi',     'Btihal',  '1999-12-03', 'FEMME', 'ZZ100009', NULL, NULL,                         '0600000009', 'Agdal, Rabat',     'CNSS',  FALSE),
    (10, 'Ouazzani',  'Rania',   '2018-06-15', 'FEMME', NULL,       NULL, NULL,                         '0600000010', 'Hay Riad, Rabat',  'CNOPS', FALSE),
    (11, 'Moussaoui', 'Karim',   '1985-10-08', 'HOMME', NULL, 'ZP0000011', 'karim.moussaoui@example.com','0600000011', NULL,              NULL,    FALSE),
    (12, 'Kettani',   'Salma',   '1990-08-19', 'FEMME', 'ZZ100012', NULL, NULL,                         '0600000012', 'Agdal, Rabat',     'CNSS',  TRUE);

-- Un dossier médical par patient (RG-01).
INSERT INTO dossier_medical (id, patient_id, date_creation, medecin_id) VALUES
    (1,  1,  TIMESTAMP(@aujourdhui - INTERVAL 60 DAY, '09:00:00'), 2),
    (2,  2,  TIMESTAMP(@aujourdhui - INTERVAL 55 DAY, '09:00:00'), 2),
    (3,  3,  TIMESTAMP(@aujourdhui - INTERVAL 50 DAY, '09:00:00'), 2),
    (4,  4,  TIMESTAMP(@aujourdhui - INTERVAL 45 DAY, '09:00:00'), 2),
    (5,  5,  TIMESTAMP(@aujourdhui - INTERVAL 40 DAY, '09:00:00'), 3),
    (6,  6,  TIMESTAMP(@aujourdhui - INTERVAL 35 DAY, '09:00:00'), 3),
    (7,  7,  TIMESTAMP(@aujourdhui - INTERVAL 30 DAY, '09:00:00'), 2),
    (8,  8,  TIMESTAMP(@aujourdhui - INTERVAL 25 DAY, '09:00:00'), 2),
    (9,  9,  TIMESTAMP(@aujourdhui - INTERVAL 20 DAY, '09:00:00'), 2),
    (10, 10, TIMESTAMP(@aujourdhui - INTERVAL 15 DAY, '09:00:00'), 2),
    (11, 11, TIMESTAMP(@aujourdhui - INTERVAL 10 DAY, '09:00:00'), 2),
    (12, 12, TIMESTAMP(@aujourdhui - INTERVAL 90 DAY, '09:00:00'), 2);

-- ─── Référentiels ─────────────────────────────────────────────────────────────────────────────
INSERT INTO antecedent (id, libelle, categorie, actif) VALUES
    (1, 'Diabète',                   'MALADIE_CHRONIQUE',  TRUE),
    (2, 'Hypertension artérielle',   'MALADIE_CHRONIQUE',  TRUE),
    (3, 'Allergie à la pénicilline', 'ALLERGIE',           TRUE),
    (4, 'Allergie au latex',         'ALLERGIE',           TRUE),
    (5, 'Asthme',                    'MALADIE_CHRONIQUE',  TRUE),
    (6, 'Cardiopathie',              'MALADIE_CHRONIQUE',  TRUE),
    (7, 'Traitement anticoagulant',  'TRAITEMENT',         TRUE),
    (8, 'Grossesse',                 'ETAT_PHYSIOLOGIQUE', TRUE),
    (9, 'Tabagisme',                 'HABITUDE',           TRUE);

INSERT INTO dossier_antecedent (dossier_id, antecedent_id, niveau_risque, commentaire, actif) VALUES
    (1, 3, 'ELEVE',  'Réaction cutanée sévère en 2019 : éviter toute pénicilline.', TRUE),
    (7, 3, 'MOYEN',  'Éruption légère signalée par la patiente, non confirmée.',     TRUE),
    (2, 1, 'MOYEN',  'Diabète de type 2 équilibré sous traitement oral.',           TRUE),
    (7, 2, 'MOYEN',  NULL,                                                           TRUE),
    (8, 9, 'FAIBLE', 'Environ 10 cigarettes par jour.',                             TRUE);

-- Tarifs de référence fictifs en MAD.
INSERT INTO acte (id, libelle, categorie, prix_base, actif) VALUES
    (1,  'Consultation',                    'GENERAL',     200.00,  TRUE),
    (2,  'Détartrage',                      'PREVENTION',  400.00,  TRUE),
    (3,  'Extraction simple',               'CHIRURGIE',   450.00,  TRUE),
    (4,  'Soin de carie (composite)',       'SOINS',       500.00,  TRUE),
    (5,  'Traitement de canal',             'ENDODONTIE',  1200.00, TRUE),
    (6,  'Radiographie panoramique',        'IMAGERIE',    300.00,  TRUE),
    (7,  'Radiographie rétro-alvéolaire',   'IMAGERIE',    150.00,  TRUE),
    (8,  'Couronne céramique',              'PROTHESE',    3500.00, TRUE),
    (9,  'Blanchiment',                     'ESTHETIQUE',  2500.00, TRUE),
    (10, 'Extraction dent de sagesse',      'CHIRURGIE',   900.00,  TRUE),
    (11, 'Application de fluor',            'PREVENTION',  150.00,  FALSE);

INSERT INTO medicament (id, nom, forme, actif) VALUES
    (1, 'Amoxicilline 1 g',       'Comprimé',       TRUE),
    (2, 'Paracétamol 1 g',        'Comprimé',       TRUE),
    (3, 'Ibuprofène 400 mg',      'Comprimé',       TRUE),
    (4, 'Chlorhexidine 0,12 %',   'Bain de bouche', TRUE),
    (5, 'Métronidazole 500 mg',   'Comprimé',       TRUE),
    (6, 'Spiramycine 3 MUI',      'Comprimé',       TRUE),
    (7, 'Gel gingival apaisant',  'Gel',            TRUE),
    (8, 'Ancien antalgique',      'Sirop',          FALSE);

-- ─── Rendez-vous ──────────────────────────────────────────────────────────────────────────────
INSERT INTO rdv (id, dossier_id, medecin_id, debut, fin, motif, statut, arrivee_at) VALUES
    -- Passés
    (1,  1,  2, TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '09:00:00'), TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '09:30:00'),
         'Détartrage et douleur dent de sagesse', 'TERMINE',  TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '08:50:00')),
    (2,  2,  2, TIMESTAMP(@aujourdhui - INTERVAL 7 DAY,  '10:00:00'), TIMESTAMP(@aujourdhui - INTERVAL 7 DAY,  '10:30:00'),
         'Douleur molaire',                       'TERMINE',  TIMESTAMP(@aujourdhui - INTERVAL 7 DAY,  '09:55:00')),
    (3,  6,  3, TIMESTAMP(@aujourdhui - INTERVAL 5 DAY,  '11:00:00'), TIMESTAMP(@aujourdhui - INTERVAL 5 DAY,  '11:30:00'),
         'Contrôle orthodontique',                'ABSENT',   NULL),
    (4,  9,  2, TIMESTAMP(@aujourdhui - INTERVAL 3 DAY,  '14:00:00'), TIMESTAMP(@aujourdhui - INTERVAL 3 DAY,  '14:30:00'),
         'Consultation',                          'ANNULE',   NULL),
    -- Aujourd'hui
    (5,  3,  2, TIMESTAMP(@aujourdhui, '09:00:00'), TIMESTAMP(@aujourdhui, '09:45:00'),
         'Carie dent 36',                         'TERMINE',  TIMESTAMP(@aujourdhui, '08:55:00')),
    (6,  4,  2, TIMESTAMP(@aujourdhui, '10:00:00'), TIMESTAMP(@aujourdhui, '10:30:00'),
         'Contrôle',                              'EN_COURS', TIMESTAMP(@aujourdhui, '09:50:00')),
    (7,  7,  2, TIMESTAMP(@aujourdhui, '10:30:00'), TIMESTAMP(@aujourdhui, '11:00:00'),
         'Détartrage',                            'ARRIVE',   TIMESTAMP(@aujourdhui, '10:05:00')),
    (8,  10, 2, TIMESTAMP(@aujourdhui, '11:00:00'), TIMESTAMP(@aujourdhui, '11:30:00'),
         'Première visite enfant',                'ARRIVE',   TIMESTAMP(@aujourdhui, '10:20:00')),
    (9,  8,  2, TIMESTAMP(@aujourdhui, '15:00:00'), TIMESTAMP(@aujourdhui, '15:30:00'),
         'Douleur gencive',                       'CONFIRME', NULL),
    (10, 5,  3, TIMESTAMP(@aujourdhui, '16:00:00'), TIMESTAMP(@aujourdhui, '16:30:00'),
         'Bilan orthodontique',                   'PLANIFIE', NULL),
    -- À venir
    (11, 1,  2, TIMESTAMP(@aujourdhui + INTERVAL 3 DAY,  '09:00:00'), TIMESTAMP(@aujourdhui + INTERVAL 3 DAY,  '09:30:00'),
         'Contrôle post-extraction',              'PLANIFIE', NULL),
    (12, 11, 2, TIMESTAMP(@aujourdhui + INTERVAL 7 DAY,  '14:00:00'), TIMESTAMP(@aujourdhui + INTERVAL 7 DAY,  '14:45:00'),
         'Pose couronne',                         'PLANIFIE', NULL);

INSERT INTO indisponibilite (id, medecin_id, debut, fin, motif) VALUES
    (1, 2, TIMESTAMP(@aujourdhui + INTERVAL 10 DAY, '08:00:00'), TIMESTAMP(@aujourdhui + INTERVAL 11 DAY, '18:00:00'), 'Congrès dentaire'),
    (2, 3, TIMESTAMP(@aujourdhui + INTERVAL 5 DAY,  '08:00:00'), TIMESTAMP(@aujourdhui + INTERVAL 5 DAY,  '18:00:00'), 'Formation');

-- ─── Consultations et interventions ───────────────────────────────────────────────────────────
INSERT INTO consultation (id, dossier_id, medecin_id, rdv_id, date_consultation, observation, statut) VALUES
    (1, 1, 2, 1, TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '09:05:00'),
        'Tartre abondant. Dent 48 extraite sans complication.', 'VALIDEE'),
    (2, 2, 2, 2, TIMESTAMP(@aujourdhui - INTERVAL 7 DAY,  '10:05:00'),
        'Carie profonde sur 46, radiographie réalisée. Traitement à prévoir.', 'VALIDEE'),
    (3, 3, 2, 5, TIMESTAMP(@aujourdhui, '09:05:00'),
        'Carie occlusale sur 36, composite posé.', 'VALIDEE'),
    (4, 4, 2, 6, TIMESTAMP(@aujourdhui, '10:00:00'),
        'Contrôle en cours.', 'BROUILLON');

INSERT INTO intervention (id, consultation_id, acte_id, libelle_acte_snapshot, dent, quantite, prix_unitaire) VALUES
    (1, 1, 2, 'Détartrage',                    NULL, 1, 400.00),
    (2, 1, 3, 'Extraction simple',             '48', 1, 450.00),
    (3, 2, 1, 'Consultation',                  NULL, 1, 200.00),
    (4, 2, 7, 'Radiographie rétro-alvéolaire', '46', 1, 150.00),
    (5, 3, 4, 'Soin de carie (composite)',     '36', 1, 500.00),
    (6, 4, 1, 'Consultation',                  NULL, 1, 200.00);

-- ─── Documents cliniques ──────────────────────────────────────────────────────────────────────
INSERT INTO ordonnance (id, consultation_id, date_emission, statut) VALUES
    (1, 1, TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '09:40:00'), 'EMISE'),
    (2, 3, NULL,                                                   'BROUILLON');

INSERT INTO prescription (id, ordonnance_id, medicament_id, nom_medicament_snapshot, quantite, frequence, duree_jours) VALUES
    (1, 1, 2, 'Paracétamol 1 g',      1, '1 comprimé toutes les 8 heures en cas de douleur', 3),
    (2, 1, 4, 'Chlorhexidine 0,12 %', 1, '1 bain de bouche matin et soir après brossage',    7),
    (3, 2, 3, 'Ibuprofène 400 mg',    1, '1 comprimé matin et soir pendant les repas',       3);

INSERT INTO certificat (id, consultation_id, date_debut, date_fin, motif, date_emission, statut) VALUES
    (1, 1, @aujourdhui - INTERVAL 14 DAY, @aujourdhui - INTERVAL 13 DAY,
        'Repos suite à une extraction dentaire.', TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '09:45:00'), 'EMIS');

-- ─── Facturation et caisse ────────────────────────────────────────────────────────────────────
-- Totaux = somme des interventions (quantite × prix_unitaire), figés à la validation.
INSERT INTO facture (id, consultation_id, numero, date_facture, total, statut, motif_annulation) VALUES
    (1, 1, 'FAC-2026-0001', TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '10:00:00'), 850.00, 'VALIDEE', NULL),
    (2, 2, 'FAC-2026-0002', TIMESTAMP(@aujourdhui - INTERVAL 7 DAY,  '11:00:00'), 350.00, 'VALIDEE', NULL);

INSERT INTO paiement (id, facture_id, auteur_id, reference, date_paiement, montant, mode) VALUES
    (1, 1, 4, '6f1c2d3e-0001-4a5b-8c9d-000000000001', TIMESTAMP(@aujourdhui - INTERVAL 14 DAY, '10:05:00'), 300.00, 'ESPECES'),
    (2, 2, 4, '6f1c2d3e-0002-4a5b-8c9d-000000000002', TIMESTAMP(@aujourdhui - INTERVAL 7 DAY,  '11:05:00'), 350.00, 'CARTE');

INSERT INTO charge (id, auteur_id, titre, description, montant, date_charge) VALUES
    (1, 2, 'Loyer du cabinet',      'Loyer mensuel',                         5000.00, TIMESTAMP(DATE_FORMAT(@aujourdhui, '%Y-%m-01'), '09:00:00')),
    (2, 2, 'Fournitures dentaires', 'Composites, gants, compresses',         1200.00, TIMESTAMP(@aujourdhui - INTERVAL 6 DAY, '12:00:00')),
    (3, 2, 'Électricité',           'Facture mensuelle (montant fictif)',     650.00, TIMESTAMP(@aujourdhui - INTERVAL 2 DAY, '12:00:00'));

COMMIT;
