package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import ma.dentalpro.domain.Intervention;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires du repository en mémoire pour les interventions.
 */
class InterventionRepoImplTest {

    private final InterventionRepoImpl repo = new InterventionRepoImpl();

    @BeforeEach
    void initialiser() {
        VirtualDataBase.reset();
    }

    @Test
    void findAllRetourneLesInterventionsDuSeed() {
        List<Intervention> liste = repo.findAll();
        assertEquals(6, liste.size());
    }

    @Test
    void findByIdRetourneLIntervention() {
        Intervention i = repo.findById(1L);
        assertNotNull(i);
        assertEquals("Détartrage", i.getLibelleActeSnapshot());
        assertEquals(new BigDecimal("400.00"), i.getPrixUnitaire());
    }

    @Test
    void findByConsultationIdRetourneLesInterventionsDeLaConsultation() {
        List<Intervention> deConsultation1 = repo.findByConsultationId(1L);
        assertEquals(2, deConsultation1.size()); // Détartrage + Extraction 48
    }

    @Test
    void saveAjouteUneInterventionAvecNouvelId() {
        Intervention nouvelle = Intervention.builder()
                .consultationId(4L)
                .acteId(2L)
                .libelleActeSnapshot("Détartrage")
                .quantite(1)
                .prixUnitaire(new BigDecimal("400.00"))
                .build();

        Intervention sauvee = repo.save(nouvelle);
        assertNotNull(sauvee.getId());
        assertEquals(7L, sauvee.getId());
        assertEquals(7, repo.findAll().size());
    }

    @Test
    void updateModifieQuantiteEtPrix() {
        Intervention i = repo.findById(1L);
        i.setQuantite(2);
        assertTrue(repo.update(i));

        Intervention relue = repo.findById(1L);
        assertEquals(2, relue.getQuantite());
        assertEquals(new BigDecimal("800.00"), relue.getTotal());
    }

    @Test
    void deleteByIdSupprimeLIntervention() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertEquals(5, repo.findAll().size());
        assertFalse(repo.deleteById(1L));
    }
}
