package ma.dentalpro.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class PatientTest {

    private static Patient patient(Long id, String nom, String prenom) {
        return Patient.builder().id(id).nom(nom).prenom(prenom).build();
    }

    @Test
    void equalsEtHashCodeReposentSurLIdentifiant() {
        var avant = patient(1L, "Benali", "Omar");
        var apresModification = patient(1L, "Benali", "Omar Karim");

        assertEquals(avant, apresModification);
        assertEquals(avant.hashCode(), apresModification.hashCode());

        var ensemble = new HashSet<Patient>();
        ensemble.add(avant);
        assertTrue(ensemble.contains(apresModification));
    }

    @Test
    void deuxPatientsSansIdentifiantNeSontPasEgaux() {
        assertNotEquals(patient(null, "Benali", "Omar"), patient(null, "Benali", "Omar"));
    }

    @Test
    void compareToTrieParNomPuisPrenomSansTenirCompteDeLaCasse() {
        assertTrue(patient(1L, "alaoui", "Ayman").compareTo(patient(2L, "Benali", "Omar")) < 0);
        assertTrue(patient(1L, "Benali", "Omar").compareTo(patient(2L, "benali", "Amine")) > 0);
    }

    @Test
    void ageEtEnfantSontCalculesDepuisLaDateDeNaissance() {
        var enfant = Patient.builder().dateNaissance(LocalDate.now().minusYears(8)).build();
        var adulte = Patient.builder().dateNaissance(LocalDate.now().minusYears(30).minusDays(1)).build();
        var limite = Patient.builder().dateNaissance(LocalDate.now().minusYears(Patient.AGE_LIMITE_ENFANT)).build();

        assertEquals(8, enfant.getAge());
        assertTrue(enfant.isEnfant());
        assertEquals(30, adulte.getAge());
        assertFalse(adulte.isEnfant());
        assertFalse(limite.isEnfant());
    }

    @Test
    void sansDateDeNaissanceLAgeEstInconnu() {
        var p = new Patient();
        assertNull(p.getAge());
        assertFalse(p.isEnfant());
    }
}
