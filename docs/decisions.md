# Décisions et hypothèses à valider

Le dossier de spécification laisse plusieurs points « à valider » (PV). En attendant la validation
de Pr. El Midaoui, le groupe a retenu les choix suivants. Chaque choix peut être modifié sans
remettre en cause l'architecture.

| # | Sujet | Choix retenu | Où |
|---|---|---|---|
| D-01 | Valeurs du sexe (PV-04) | `HOMME`, `FEMME`, facultatif | `patient.sexe`, enum `Sexe` |
| D-02 | Mesure « Enfant » | Patient de moins de 12 ans (âge calculé depuis la date de naissance) | `Patient.AGE_LIMITE_ENFANT` |
| D-03 | CIN / passeport / e-mail | Facultatifs ; CIN et passeport uniques s'ils sont renseignés | `patient` |
| D-04 | Médecin du dossier | `dossier_medical.medecin_id` facultatif (la secrétaire crée le patient et son dossier) | `dossier_medical` |
| D-05 | Rôles | `ADMIN`, `MEDECIN`, `SECRETAIRE` ; un compte peut cumuler plusieurs rôles | `role`, `utilisateur_role` |
| D-06 | Mots de passe (BNF-02) | PBKDF2WithHmacSHA256, 600 000 itérations, sel aléatoire 16 octets, format `algorithme$iterations$sel$empreinte` (Base64) — disponible dans le JDK, sans bibliothèque externe | `utilisateur.password_hash` |
| D-07 | Cycle de vie d'un RDV | `PLANIFIE → CONFIRME → ARRIVE → EN_COURS → TERMINE`, ou `ANNULE` / `ABSENT` | `rdv.statut` |
| D-08 | États des documents | Consultation `BROUILLON`/`VALIDEE` ; ordonnance `BROUILLON`/`EMISE` ; certificat `BROUILLON`/`EMIS` ; facture `BROUILLON`/`VALIDEE`/`ANNULEE` | colonnes `statut` |
| D-09 | Facture par consultation (PV-06) | Au plus une facture par consultation, y compris annulée (contrainte `UNIQUE`) | `facture.consultation_id` |
| D-10 | Numéro de facture | `FAC-AAAA-NNNN`, attribué par le service | `facture.numero` |
| D-11 | Modes de paiement | `ESPECES`, `CARTE`, `CHEQUE`, `VIREMENT`, `AUTRE` | `paiement.mode` |
| D-12 | Référence de paiement | UUID générée à l'ouverture du formulaire : un double clic ou un nouvel essai ne crée pas de doublon | `paiement.reference` |
| D-13 | Niveaux de risque d'un antécédent | `FAIBLE`, `MOYEN`, `ELEVE` | `dossier_antecedent.niveau_risque` |
| D-14 | Dent | Numérotation FDI (11 à 48, 51 à 85) ou zone libre courte ; `NULL` pour un acte global | `intervention.dent` |
| D-15 | Cabinet unique | Une seule ligne (`id = 1`) imposée par contrainte | `cabinet` |
| D-16 | Suppression physique | Interdite pour patients, consultations, factures et paiements (archivage / annulation) ; les clés étrangères bloquent aussi la suppression d'un patient qui a un dossier | services + FK |
