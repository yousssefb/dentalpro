package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import ma.dentalpro.domain.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientRepoImplTest {

    private final PatientRepoImpl repo = new PatientRepoImpl();

    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    private static Patient nouveau(Long id, String nom, String prenom) {
        return Patient.builder().id(id).nom(nom).prenom(prenom).dateNaissance(LocalDate.of(1990, 1, 1)).build();
    }

    @Test
    void saveAttribueUnNouvelIdentifiantEtConserveLePatient() {
        var sauve = repo.save(nouveau(null, "Nadir", "Anis"));

        assertEquals(11L, sauve.getId());
        assertEquals("Anis", repo.findById(11L).getPrenom());
        assertEquals(11, repo.findAll().size());
    }

    @Test
    void updateModifieUnPatientExistant() {
        assertTrue(repo.update(nouveau(1L, "Berrada", "Rayane")));
        assertEquals("Rayane", repo.findById(1L).getPrenom());
    }

    @Test
    void updateRefuseUnPatientInexistant() {
        assertFalse(repo.update(nouveau(999L, "Inconnu", "X")));
    }

    @Test
    void deleteByIdSupprimeLePatient() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertFalse(repo.deleteById(1L));
    }

    @Test
    void findByNomLikeChercheDansNomEtPrenomSansTenirCompteDeLaCasse() {
        var resultat = repo.findByNomLike("AMIN");

        assertEquals(2, resultat.size());
        assertEquals("Chraibi", resultat.get(0).getNom());   // Amina
        assertEquals("Tazi", resultat.get(1).getNom());      // Amine
        assertEquals(2, repo.findByNomLike("ben").size());   // Benali, Bennani
    }
}
