package ma.dentalpro.repository.memory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;

/**
 * Base de données simulée en mémoire, utile pour les tests sans MySQL.
 *
 * <p>La liste est créée une seule fois : les ajouts, modifications et suppressions
 * restent visibles jusqu'à l'arrêt de l'application. Données fictives.</p>
 */
public final class VirtualDataBase {

    private static final List<Patient> PATIENTS = new ArrayList<>(initialPatients());

    private VirtualDataBase() {}

    public static List<Patient> patients() {
        return PATIENTS;
    }

    /** Remet les données initiales (utilisé par les tests). */
    public static synchronized void reset() {
        PATIENTS.clear();
        PATIENTS.addAll(initialPatients());
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
        return Patient.builder()
                .id(id).nom(nom).prenom(prenom)
                .dateNaissance(LocalDate.parse(naissance))
                .sexe(sexe)
                .telephone("06000000" + (id < 10 ? "0" + id : id))
                .build();
    }
}
