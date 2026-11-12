# dentalpro

**DentalPro (DentalSoft V1.0)** : application de bureau de gestion d'un cabinet dentaire (cabinet *YOU SMILE*).
Projet du module Java avancé — EMSI Rabat, encadré par Pr. O. El Midaoui (26 octobre → 27 décembre 2026).

Java Swing · JDBC · MySQL · Maven · Lombok — sans Spring, Hibernate ni autre framework/ORM.

## Versions utilisées

| Outil | Version |
|---|---|
| JDK | 27 (Oracle JDK 27.0.1) — imposé par `maven-enforcer-plugin` |
| Maven | 3.9.x (celui fourni avec IntelliJ IDEA suffit) |
| MySQL Server | 8.4 LTS (8.0 ou plus récent accepté) |
| Pilote JDBC | mysql-connector-j 8.4.0 |
| Lombok | 1.18.48 |
| Tests | JUnit 5.13 |

## Installation

1. **Créer la base de données** (MySQL doit être démarré) :
   ```bash
   mysql -u root -p < src/main/resources/db/schema.sql
   ```
2. **Configurer la connexion** : copier `src/main/resources/db.properties.example` en
   `src/main/resources/db.properties`, puis renseigner l'utilisateur et le mot de passe MySQL.
   Ce fichier est ignoré par Git : aucun mot de passe n'est versionné.
   Les variables d'environnement `DENTALPRO_DB_URL`, `DENTALPRO_DB_USER` et `DENTALPRO_DB_PASSWORD`
   sont prioritaires sur le fichier si elles sont définies.
3. **Ouvrir le projet dans IntelliJ** : *File → Open* → dossier contenant `pom.xml`,
   puis *File → Project Structure → SDK* = JDK 27.

## Lancement

- Depuis IntelliJ : exécuter `ma.dentalpro.App`.
- En ligne de commande :
  ```bash
  mvn compile exec:java
  ```

## Tests et construction

```bash
mvn clean verify
```

Compile le projet, exécute les tests JUnit et produit `target/dentalpro-1.0-SNAPSHOT.jar`.

## Architecture

Architecture logique en couches : chaque couche ne dépend que de l'interface de la couche inférieure.
Le choix des implémentations se fait dans `application.properties` (lu par `ApplicationContext`),
sans modifier le code.

```
ui (contrôleurs, vues Swing) → service (règles métier, droits) → repository (DAO JDBC, SQL) → MySQL
```

| Package | Rôle |
|---|---|
| `ma.dentalpro` | Point d'entrée `App` |
| `ma.dentalpro.config` | `ApplicationContext` (fabrique + singleton), `JdbcConnectionFactory` |
| `ma.dentalpro.domain` | Entités métier (`Patient`, …) |
| `ma.dentalpro.repository.api` | Interfaces de persistance (`CrudRepo`, `PatientRepo`, …) |
| `ma.dentalpro.repository.mysql` | Implémentations JDBC/MySQL — seul endroit contenant du SQL |
| `ma.dentalpro.repository.memory` | Implémentations en mémoire (tests sans base) |
| `ma.dentalpro.repository.jdbc` | Utilitaires JDBC et `RowMappers` |
| `ma.dentalpro.service` | Interfaces et implémentations des services |
| `ma.dentalpro.ui.controllers` | Contrôleurs d'interface |
| `ma.dentalpro.shared` | Exceptions et utilitaires communs |

## Planning des sprints

| Sprint | Période | Contenu |
|---|---|---|
| S0 | 26 oct → 1 nov | Cadrage, maquettes, décisions |
| S1 | 2 → 15 nov | Connexion, rôles, patients, dossier médical, profil, recherche |
| S2 | 16 → 29 nov | Agenda, RDV, file d'attente, antécédents, consultations, interventions |
| S3 | 30 nov → 13 déc | Factures, paiements, situation financière, documents, charges, tableau de bord |
| S4 | 14 → 20 déc | Corrections, tests, documentation |
| Clôture | 21 → 27 déc | Gel de version, présentation, vidéo, dépôt |

## Équipe

| Membre | Module |
|---|---|
| M1 — *à compléter* | Session, rôles, patients, dossier, profil |
| M2 — *à compléter* | Agenda, rendez-vous, indisponibilités, file d'attente |
| M3 — *à compléter* | Consultations, interventions, antécédents |
| M4 — *à compléter* | Facturation, paiements, situation financière, caisse |
| M5 — *à compléter* | Référentiels (actes, médicaments), ordonnances, certificats |
| M6 — *à compléter* | Charges, tableau de bord, reproductibilité |

Données de démonstration : exclusivement fictives.
