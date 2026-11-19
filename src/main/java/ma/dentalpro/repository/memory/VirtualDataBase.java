package ma.dentalpro.repository.memory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;

/**
 * Base de données simulée en mémoire, utile pour les tests sans MySQL.
 *
 * <p>Les listes sont créées une seule fois : les ajouts, modifications et suppressions
 * restent visibles jusqu'à l'arrêt de l'application. Données fictives.</p>
 */
public final class VirtualDataBase {

    private static final List<Patient> PATIENTS = new ArrayList<>(initialPatients());
    private static final List<Charge> CHARGES = new ArrayList<>(initialCharges());

    private VirtualDataBase() {}

    public static List<Patient> patients() {
        return PATIENTS;
    }

    public static List<Charge> charges() {
        return CHARGES;
    }

    /** Remet les données initiales (utilisé par les tests). */
    public static synchronized void reset() {
        PATIENTS.clear();
        PATIENTS.addAll(initialPatients());
        CHARGES.clear();
        CHARGES.addAll(initialCharges());
    }

    private static List<Patient> initialPatients() {
        List<Patient> liste = new ArrayList<>();
        liste.add(patient(1L, "Berrada", "Fadoua", "2005-03-14", Sexe.FEMME));
        liste.add(patient(2L, "Benali", "Omar", "2003-07-02", Sexe.HOMME));
        liste.add(patient(3L, "Idrissi", "Souhail", "2002-11-21", Sexe.HOMME));
        liste.add(patient(4L, "Tazi", "Amine", "2007-01-09", Sexe.HOMME));
        liste.add(patient(5L, "Lahlou", "Tarek", "2009-05-30", Sexe.HOMME));
        liste.add(patient(6L, "Chraibi", "Amina", "2006-02-17", Sexe.FEMME));
        liste.add(patient(7L, "Bennani", "Tanae", "1996-09-12", Sexe.FEMME));
        liste.add(patient(8L, "Alaoui", "Ayman", "2000-04-25", Sexe.HOMME));
        liste.add(patient(9L, "Fassi", "Btihal", "1999-12-03", Sexe.FEMME));
        liste.add(patient(10L, "Ouazzani", "Rania", "2018-06-15", Sexe.FEMME));
        return liste;
    }

    /** Quelques charges fictives pour tester l'écran sans MySQL. */
    private static List<Charge> initialCharges() {
        List<Charge> liste = new ArrayList<>();
        liste.add(charge(1L, 2L, "Loyer du cabinet", "Loyer mensuel", "5000.00",
                LocalDateTime.now().minusDays(20)));
        liste.add(charge(2L, 2L, "Fournitures dentaires", "Composites, gants, compresses", "1200.00",
                LocalDateTime.now().minusDays(6)));
        liste.add(charge(3L, 2L, "Électricité", "Facture mensuelle (montant fictif)", "650.00",
                LocalDateTime.now().minusDays(2)));
        return liste;
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

    private static Charge charge(Long id, Long auteurId, String titre, String description,
                                 String montant, LocalDateTime dateCharge) {
        return Charge.builder()
                .id(id)
                .auteurId(auteurId)
                .titre(titre)
                .description(description)
                .montant(new BigDecimal(montant))
                .dateCharge(dateCharge)
                .build();
    }
}
