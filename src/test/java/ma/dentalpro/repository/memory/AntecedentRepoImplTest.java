package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import ma.dentalpro.domain.Antecedent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires du repository en mémoire pour les antécédents médicaux.
 */
class AntecedentRepoImplTest {

    private final AntecedentRepoImpl repo = new AntecedentRepoImpl();

    @BeforeEach
    void initialiser() {
        VirtualDataBase.reset();
    }

    @Test
    void findAllRetourneLesAntecedentsDuJeuInitial() {
        List<Antecedent> liste = repo.findAll();
        assertEquals(9, liste.size());
    }

    @Test
    void findByIdRetourneLAntecedentCorrespondant() {
        Antecedent a = repo.findById(1L);
        assertNotNull(a);
        assertEquals("Diabète", a.getLibelle());
        assertEquals("MALADIE_CHRONIQUE", a.getCategorie());
    }

    @Test
    void findByIdInexistantRetourneNull() {
        assertNull(repo.findById(999L));
        assertNull(repo.findById(null));
    }

    @Test
    void findByLibelleLikeRechercheSansCasse() {
        List<Antecedent> allergies = repo.findByLibelleLike("allergie");
        assertEquals(2, allergies.size()); // pénicilline et latex
    }

    @Test
    void findByCategorieRetourneLesBonsElements() {
        List<Antecedent> chroniques = repo.findByCategorie("MALADIE_CHRONIQUE");
        assertEquals(4, chroniques.size()); // Diabète, HTA, Asthme, Cardiopathie
    }

    @Test
    void saveAjouteUnNouvelAntecedentAvecNouvelId() {
        Antecedent nouveau = Antecedent.builder()
                .libelle("Hémophilie")
                .categorie("MALADIE_CHRONIQUE")
                .actif(true)
                .build();

        Antecedent sauvegarde = repo.save(nouveau);
        assertNotNull(sauvegarde.getId());
        assertEquals(10L, sauvegarde.getId());
        assertEquals(10, repo.findAll().size());
        assertEquals("Hémophilie", repo.findById(10L).getLibelle());
    }

    @Test
    void updateModifieUnAntecedentExistant() {
        Antecedent a = repo.findById(1L);
        a.setLibelle("Diabète de type 1");
        assertTrue(repo.update(a));
        assertEquals("Diabète de type 1", repo.findById(1L).getLibelle());
    }

    @Test
    void deleteByIdSupprimeLAntecedent() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertEquals(8, repo.findAll().size());
        assertFalse(repo.deleteById(1L));
    }
}
