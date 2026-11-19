package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.StatutConsultation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires du repository en mémoire pour les consultations.
 */
class ConsultationRepoImplTest {

    private final ConsultationRepoImpl repo = new ConsultationRepoImpl();

    @BeforeEach
    void initialiser() {
        VirtualDataBase.reset();
    }

    @Test
    void findAllRetourneLesConsultationsDuSeed() {
        List<Consultation> consultations = repo.findAll();
        assertEquals(4, consultations.size());
    }

    @Test
    void findByIdRetourneLaBonneConsultation() {
        Consultation c = repo.findById(1L);
        assertNotNull(c);
        assertEquals(1L, c.getDossierId());
        assertEquals(2L, c.getMedecinId());
        assertEquals(StatutConsultation.VALIDEE, c.getStatut());
    }

    @Test
    void findByDossierIdRetourneLesConsultationsDuPatient() {
        List<Consultation> duDossier1 = repo.findByDossierId(1L);
        assertEquals(1, duDossier1.size());
        assertEquals(1L, duDossier1.get(0).getId());
    }

    @Test
    void findByRdvIdTrouveLaConsultationAssociee() {
        Consultation c = repo.findByRdvId(1L);
        assertNotNull(c);
        assertEquals(1L, c.getId());
    }

    @Test
    void findByStatutFiltreCorrectement() {
        List<Consultation> validees = repo.findByStatut(StatutConsultation.VALIDEE);
        assertEquals(3, validees.size());

        List<Consultation> brouillons = repo.findByStatut(StatutConsultation.BROUILLON);
        assertEquals(1, brouillons.size());
    }

    @Test
    void saveAttribueUnIdEtConserveLaConsultation() {
        Consultation nouvelle = Consultation.builder()
                .dossierId(2L)
                .medecinId(2L)
                .dateConsultation(LocalDateTime.now())
                .observation("Examen annuel")
                .statut(StatutConsultation.BROUILLON)
                .build();

        Consultation sauvee = repo.save(nouvelle);
        assertNotNull(sauvee.getId());
        assertEquals(5L, sauvee.getId());
        assertEquals(5, repo.findAll().size());
    }

    @Test
    void updateModifieLeStatutEtLObservation() {
        Consultation c = repo.findById(4L); // consultation 4 est BROUILLON
        assertEquals(StatutConsultation.BROUILLON, c.getStatut());

        c.setStatut(StatutConsultation.VALIDEE);
        c.setObservation("Contrôle terminé, RAS.");
        assertTrue(repo.update(c));

        Consultation relue = repo.findById(4L);
        assertEquals(StatutConsultation.VALIDEE, relue.getStatut());
        assertEquals("Contrôle terminé, RAS.", relue.getObservation());
    }

    @Test
    void deleteByIdSupprimeLaConsultation() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertEquals(3, repo.findAll().size());
        assertFalse(repo.deleteById(1L));
    }
}
