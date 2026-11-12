package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ma.dentalpro.domain.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientRepoImplTest {

    private final PatientRepoImpl repo = new PatientRepoImpl();

    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    @Test
    void saveAttribueUnNouvelIdentifiantEtConserveLePatient() {
        var sauve = repo.save(new Patient(null, "Anis", 34));

        assertEquals(11L, sauve.getIdPatient());
        assertEquals("Anis", repo.findById(11L).getNom());
        assertEquals(10, repo.findAll().size());
    }

    @Test
    void updateModifieUnPatientExistant() {
        assertTrue(repo.update(new Patient(1L, "Rayane", 23)));
        assertEquals("Rayane", repo.findById(1L).getNom());
    }

    @Test
    void updateRefuseUnPatientInexistant() {
        assertFalse(repo.update(new Patient(999L, "Inconnu", 40)));
    }

    @Test
    void deleteByIdSupprimeLePatient() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertFalse(repo.deleteById(1L));
    }

    @Test
    void findByNomLikeEstInsensibleALaCasse() {
        var resultat = repo.findByNomLike("AMIN");

        assertEquals(2, resultat.size());
        assertEquals("Amina", resultat.get(0).getNom());
        assertEquals("Amine", resultat.get(1).getNom());
    }
}
