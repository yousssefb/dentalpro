package ma.dentalpro.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import org.junit.jupiter.api.Test;

class PatientTest {

    @Test
    void equalsEtHashCodeReposentSurLIdentifiant() {
        var avant = new Patient(1L, "Omar", 22);
        var apresModification = new Patient(1L, "Omar Alaoui", 23);

        assertEquals(avant, apresModification);
        assertEquals(avant.hashCode(), apresModification.hashCode());

        var ensemble = new HashSet<Patient>();
        ensemble.add(avant);
        assertTrue(ensemble.contains(apresModification));
    }

    @Test
    void deuxPatientsSansIdentifiantNeSontPasEgaux() {
        assertNotEquals(new Patient(null, "Omar", 22), new Patient(null, "Omar", 22));
    }

    @Test
    void compareToTrieParNomSansTenirCompteDeLaCasse() {
        assertTrue(new Patient(1L, "amine", 18).compareTo(new Patient(2L, "Btihal", 27)) < 0);
        assertTrue(new Patient(1L, "Omar", 22).compareTo(new Patient(2L, "fadoua", 20)) > 0);
    }
}
