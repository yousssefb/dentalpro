package ma.dentalpro.repository.memory;

import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Patient;

/**
 * Base de données simulée en mémoire, utile pour les tests sans MySQL.
 *
 * <p>La liste est créée une seule fois : les ajouts, modifications et suppressions
 * restent visibles jusqu'à l'arrêt de l'application.</p>
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
                new Patient(1L, "Fadoua", 20),
                new Patient(2L, "Omar", 22),
                new Patient(4L, "Souhail", 23),
                new Patient(5L, "Amine", 18),
                new Patient(6L, "Tarek", 17),
                new Patient(7L, "Amina", 20),
                new Patient(8L, "Tanae", 30),
                new Patient(9L, "Ayman", 26),
                new Patient(10L, "Btihal", 27)
        );
    }
}
