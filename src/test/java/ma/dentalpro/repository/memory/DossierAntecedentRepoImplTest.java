package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.DossierAntecedentId;
import ma.dentalpro.domain.NiveauRisque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires du repository en mémoire pour dossier_antecedent.
 */
class DossierAntecedentRepoImplTest {

    private final DossierAntecedentRepoImpl repo = new DossierAntecedentRepoImpl();

    @BeforeEach
    void initialiser() {
        VirtualDataBase.reset();
    }

    @Test
    void findAllRetourneLesDonneesDuSeed() {
        List<DossierAntecedent> liste = repo.findAll();
        assertEquals(5, liste.size());
    }

    @Test
    void findByDossierIdRetourneLesAntecedentsDuPatient() {
        List<DossierAntecedent> duDossier7 = repo.findByDossierId(7L);
        assertEquals(2, duDossier7.size()); // antécédents 3 (pénicilline) et 2 (HTA)
    }

    @Test
    void findByDossierIdEtAntecedentIdTrouveLAssociation() {
        DossierAntecedent da = repo.findByDossierIdAndAntecedentId(1L, 3L);
        assertNotNull(da);
        assertEquals(NiveauRisque.ELEVE, da.getNiveauRisque());
        assertTrue(da.getCommentaire().contains("pénicilline"));
    }

    @Test
    void findByIdAvecCleCompositeFonctionne() {
        DossierAntecedentId id = new DossierAntecedentId(1L, 3L);
        DossierAntecedent da = repo.findById(id);
        assertNotNull(da);
        assertEquals(1L, da.getDossierId());
        assertEquals(3L, da.getAntecedentId());
    }

    @Test
    void saveAjouteUneNouvelleAssociation() {
        DossierAntecedent nouveau = DossierAntecedent.builder()
                .dossierId(3L)
                .antecedentId(5L) // Asthme
                .niveauRisque(NiveauRisque.MOYEN)
                .commentaire("Crises occasionnelles")
                .actif(true)
                .build();

        repo.save(nouveau);
        assertEquals(6, repo.findAll().size());
        assertNotNull(repo.findByDossierIdAndAntecedentId(3L, 5L));
    }

    @Test
    void updateModifieLeNiveauDeRisqueEtCommentaire() {
        DossierAntecedent da = repo.findByDossierIdAndAntecedentId(1L, 3L);
        da.setNiveauRisque(NiveauRisque.MOYEN);
        da.setCommentaire("Nouveau test allergologique");

        assertTrue(repo.update(da));
        DossierAntecedent modifie = repo.findByDossierIdAndAntecedentId(1L, 3L);
        assertEquals(NiveauRisque.MOYEN, modifie.getNiveauRisque());
    }

    @Test
    void deleteByDossierIdAndAntecedentIdSupprimeLAssociation() {
        assertTrue(repo.deleteByDossierIdAndAntecedentId(1L, 3L));
        assertNull(repo.findByDossierIdAndAntecedentId(1L, 3L));
        assertEquals(4, repo.findAll().size());
    }
}
