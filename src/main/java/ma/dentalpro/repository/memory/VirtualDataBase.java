package ma.dentalpro.repository.memory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.domain.Medicament;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;

/**
 * Base de données simulée en mémoire, utile pour les tests sans MySQL.
 *
 * <p>Les listes sont créées une seule fois : les ajouts, modifications et suppressions
 * restent visibles jusqu'à l'arrêt de l'application. Données fictives, identiques à seed.sql.</p>
 */
public final class VirtualDataBase {

    private static final List<Patient> PATIENTS = new ArrayList<>(initialPatients());
    private static final List<Acte> ACTES = new ArrayList<>(initialActes());
    private static final List<Medicament> MEDICAMENTS = new ArrayList<>(initialMedicaments());

    private VirtualDataBase() {}

    public static List<Patient> patients() {
        return PATIENTS;
    }

    /** Liste unique des actes (catalogue). */
    public static List<Acte> actes() {
        return ACTES;
    }

    /** Liste unique des médicaments (référentiel). */
    public static List<Medicament> medicaments() {
        return MEDICAMENTS;
    }

    /** Remet les données initiales (utilisé par les tests). */
    public static synchronized void reset() {
        PATIENTS.clear();
        PATIENTS.addAll(initialPatients());
        ACTES.clear();
        ACTES.addAll(initialActes());
        MEDICAMENTS.clear();
        MEDICAMENTS.addAll(initialMedicaments());
    }

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
        // Téléphone fictif sur 10 chiffres : 0600000001, 0600000002, ..., 0600000010.
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

    /** Les 11 actes du seed.sql (prix en MAD). */
    private static List<Acte> initialActes() {
        return List.of(
                acte(1L, "Consultation", "GENERAL", "200.00", true),
                acte(2L, "Détartrage", "PREVENTION", "400.00", true),
                acte(3L, "Extraction simple", "CHIRURGIE", "450.00", true),
                acte(4L, "Soin de carie (composite)", "SOINS", "500.00", true),
                acte(5L, "Traitement de canal", "ENDODONTIE", "1200.00", true),
                acte(6L, "Radiographie panoramique", "IMAGERIE", "300.00", true),
                acte(7L, "Radiographie rétro-alvéolaire", "IMAGERIE", "150.00", true),
                acte(8L, "Couronne céramique", "PROTHESE", "3500.00", true),
                acte(9L, "Blanchiment", "ESTHETIQUE", "2500.00", true),
                acte(10L, "Extraction dent de sagesse", "CHIRURGIE", "900.00", true),
                acte(11L, "Application de fluor", "PREVENTION", "150.00", false)
        );
    }

    private static Acte acte(Long id, String libelle, String categorie, String prix, boolean actif) {
        return Acte.builder()
                .id(id).libelle(libelle).categorie(categorie)
                .prixBase(new BigDecimal(prix))
                .actif(actif)
                .build();
    }

    /** Les 8 médicaments du seed.sql. */
    private static List<Medicament> initialMedicaments() {
        return List.of(
                medicament(1L, "Amoxicilline 1 g", "Comprimé", true),
                medicament(2L, "Paracétamol 1 g", "Comprimé", true),
                medicament(3L, "Ibuprofène 400 mg", "Comprimé", true),
                medicament(4L, "Chlorhexidine 0,12 %", "Bain de bouche", true),
                medicament(5L, "Métronidazole 500 mg", "Comprimé", true),
                medicament(6L, "Spiramycine 3 MUI", "Comprimé", true),
                medicament(7L, "Gel gingival apaisant", "Gel", true),
                medicament(8L, "Ancien antalgique", "Sirop", false)
        );
    }

    private static Medicament medicament(Long id, String nom, String forme, boolean actif) {
        return Medicament.builder().id(id).nom(nom).forme(forme).actif(actif).build();
    }
}
