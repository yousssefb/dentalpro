package ma.dentalpro.repository;

import ma.dentalpro.domain.Rdv;
import ma.dentalpro.domain.StatutRdv;
import ma.dentalpro.repository.memory.RdvRepoMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RdvRepoTest {
    private RdvRepoMemory repository;

    @BeforeEach
    void initialiser() {
        repository = new RdvRepoMemory();
    }
    private Rdv creerRdv() {
        return new Rdv(
                1L,
                2L,
                LocalDateTime.of(2026, 11, 20, 10, 0),
                LocalDateTime.of(2026, 11, 20, 10, 30),
                "Consultation",
                StatutRdv.PLANIFIE
        );
    }
    @Test
    void testAjouterRdv() {
        Rdv rdv = creerRdv();

        Rdv resultat = repository.save(rdv);

        assertNotNull(resultat.getId());
        assertEquals(1L, resultat.getId());
        assertEquals(1, repository.findAll().size());
    }
    @Test
    void testRechercherRdv() {
        Rdv rdv = repository.save(creerRdv());

        Rdv resultat = repository.findById(rdv.getId());

        assertNotNull(resultat);
        assertEquals("Consultation", resultat.getMotif());
    }
    @Test
    void testModifierRdv() {
        Rdv rdv = repository.save(creerRdv());

        rdv.setMotif("Détartrage");

        boolean resultat = repository.update(rdv);

        assertTrue(resultat);
        assertEquals("Détartrage",
                repository.findById(rdv.getId()).getMotif());
    }
    @Test
    void testSupprimerRdv() {
        Rdv rdv = repository.save(creerRdv());

        boolean resultat = repository.deleteById(rdv.getId());

        assertTrue(resultat);
        assertNull(repository.findById(rdv.getId()));
    }
    @Test
    void testRechercherRdvInexistant() {
        Rdv resultat = repository.findById(100L);

        assertNull(resultat);
    }
}
