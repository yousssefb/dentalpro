package ma.dentalpro.repository.memory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Antecedent;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.Intervention;
import ma.dentalpro.domain.NiveauRisque;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;
import ma.dentalpro.domain.StatutConsultation;

/**
 * Base de données simulée en mémoire, utile pour les tests sans serveur MySQL.
 *
 * <p>Les listes sont créées une seule fois : les ajouts, modifications et suppressions
 * restent visibles jusqu'à l'arrêt de l'application ou l'appel à reset(). Données fictives.</p>
 */
public final class VirtualDataBase {

    private static final List<Patient> PATIENTS = new ArrayList<>(initialPatients());
    private static final List<Antecedent> ANTECEDENTS = new ArrayList<>(initialAntecedents());
    private static final List<DossierAntecedent> DOSSIER_ANTECEDENTS = new ArrayList<>(initialDossierAntecedents());
    private static final List<Consultation> CONSULTATIONS = new ArrayList<>(initialConsultations());
    private static final List<Intervention> INTERVENTIONS = new ArrayList<>(initialInterventions());

    private VirtualDataBase() {}

    public static List<Patient> patients() {
        return PATIENTS;
    }

    public static List<Antecedent> antecedents() {
        return ANTECEDENTS;
    }

    public static List<DossierAntecedent> dossierAntecedents() {
        return DOSSIER_ANTECEDENTS;
    }

    public static List<Consultation> consultations() {
        return CONSULTATIONS;
    }

    public static List<Intervention> interventions() {
        return INTERVENTIONS;
    }

    /** Remet les données initiales (utilisé par les tests pour repartir d'un état propre). */
    public static synchronized void reset() {
        PATIENTS.clear();
        PATIENTS.addAll(initialPatients());

        ANTECEDENTS.clear();
        ANTECEDENTS.addAll(initialAntecedents());

        DOSSIER_ANTECEDENTS.clear();
        DOSSIER_ANTECEDENTS.addAll(initialDossierAntecedents());

        CONSULTATIONS.clear();
        CONSULTATIONS.addAll(initialConsultations());

        INTERVENTIONS.clear();
        INTERVENTIONS.addAll(initialInterventions());
    }

    // =====================================================================================
    // Données initiales : Patients
    // =====================================================================================

    private static List<Patient> initialPatients() {
        return List.of(
                patient(1L, "Berrada", "Fadoua", "2005-03-14", Sexe.FEMME),
                patient(2L, "Benali", "Omar", "2003-07-02", Sexe.HOMME),
                patient(3L, "Idrissi", "Souhail", "2002-11-21", Sexe.HOMME),
                patient(4L, "Tazi", "Amine", "2007-01-09", Sexe.HOMME),
                patient(5L, "Lahlou", "Tarek", "2009-05-30", Sexe.HOMME),
                patient(6L, "Chraibi", "Amina", "2006-02-17", Sexe.FEMME),
                patient(7L, "Bennani", "Tanae", "1996-09-12", Sexe.FEMME),
                patient(8L, "Alaoui", "Ayman", "2000-04-25", Sexe.HOMME),
                patient(9L, "Fassi", "Btihal", "1999-12-03", Sexe.FEMME),
                patient(10L, "Ouazzani", "Rania", "2018-06-15", Sexe.FEMME)
        );
    }

    private static Patient patient(Long id, String nom, String prenom, String naissance, Sexe sexe) {
        String telephone = "06000000" + id;
        if (id < 10) {
            telephone = "060000000" + id;
        }
        return Patient.builder()
                .id(id).nom(nom).prenom(prenom)
                .dateNaissance(LocalDate.parse(naissance))
                .sexe(sexe)
                .telephone(telephone)
                .build();
    }

    // =====================================================================================
    // Données initiales : Antécédents (alignés avec seed.sql)
    // =====================================================================================

    private static List<Antecedent> initialAntecedents() {
        return List.of(
                new Antecedent(1L, "Diabète", "MALADIE_CHRONIQUE", true),
                new Antecedent(2L, "Hypertension artérielle", "MALADIE_CHRONIQUE", true),
                new Antecedent(3L, "Allergie à la pénicilline", "ALLERGIE", true),
                new Antecedent(4L, "Allergie au latex", "ALLERGIE", true),
                new Antecedent(5L, "Asthme", "MALADIE_CHRONIQUE", true),
                new Antecedent(6L, "Cardiopathie", "MALADIE_CHRONIQUE", true),
                new Antecedent(7L, "Traitement anticoagulant", "TRAITEMENT", true),
                new Antecedent(8L, "Grossesse", "ETAT_PHYSIOLOGIQUE", true),
                new Antecedent(9L, "Tabagisme", "HABITUDE", true)
        );
    }

    // =====================================================================================
    // Données initiales : Dossier-Antécédents (alignés avec seed.sql)
    // =====================================================================================

    private static List<DossierAntecedent> initialDossierAntecedents() {
        return List.of(
                new DossierAntecedent(1L, 3L, NiveauRisque.ELEVE, "Réaction cutanée sévère en 2019 : éviter toute pénicilline.", true),
                new DossierAntecedent(7L, 3L, NiveauRisque.MOYEN, "Éruption légère signalée par la patiente, non confirmée.", true),
                new DossierAntecedent(2L, 1L, NiveauRisque.MOYEN, "Diabète de type 2 équilibré sous traitement oral.", true),
                new DossierAntecedent(7L, 2L, NiveauRisque.MOYEN, null, true),
                new DossierAntecedent(8L, 9L, NiveauRisque.FAIBLE, "Environ 10 cigarettes par jour.", true)
        );
    }

    // =====================================================================================
    // Données initiales : Consultations (alignées avec seed.sql)
    // =====================================================================================

    private static List<Consultation> initialConsultations() {
        LocalDate aujourdhui = LocalDate.now();
        return List.of(
                new Consultation(1L, 1L, 2L, 1L, aujourdhui.minusDays(14).atTime(9, 5),
                        "Tartre abondant. Dent 48 extraite sans complication.", StatutConsultation.VALIDEE),
                new Consultation(2L, 2L, 2L, 2L, aujourdhui.minusDays(7).atTime(10, 5),
                        "Carie profonde sur 46, radiographie réalisée. Traitement à prévoir.", StatutConsultation.VALIDEE),
                new Consultation(3L, 3L, 2L, 5L, aujourdhui.atTime(9, 5),
                        "Carie occlusale sur 36, composite posé.", StatutConsultation.VALIDEE),
                new Consultation(4L, 4L, 2L, 6L, aujourdhui.atTime(10, 0),
                        "Contrôle en cours.", StatutConsultation.BROUILLON)
        );
    }

    // =====================================================================================
    // Données initiales : Interventions (alignées avec seed.sql)
    // =====================================================================================

    private static List<Intervention> initialInterventions() {
        return List.of(
                new Intervention(1L, 1L, 2L, "Détartrage", null, 1, new BigDecimal("400.00")),
                new Intervention(2L, 1L, 3L, "Extraction simple", "48", 1, new BigDecimal("450.00")),
                new Intervention(3L, 2L, 1L, "Consultation", null, 1, new BigDecimal("200.00")),
                new Intervention(4L, 2L, 7L, "Radiographie rétro-alvéolaire", "46", 1, new BigDecimal("150.00")),
                new Intervention(5L, 3L, 4L, "Soin de carie (composite)", "36", 1, new BigDecimal("500.00")),
                new Intervention(6L, 4L, 1L, "Consultation", null, 1, new BigDecimal("200.00"))
        );
    }
}
