package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import ma.dentalpro.domain.Acte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActeRepoImplTest {

    private final ActeRepoImpl repo = new ActeRepoImpl();

    // Avant chaque test, on remet les données du seed : les tests ne se gênent pas entre eux.
    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    private static Acte nouveau(Long id, String libelle, String prix) {
        return Acte.builder().id(id).libelle(libelle).categorie("TEST").prixBase(new BigDecimal(prix)).build();
    }

    @Test
    void findAllRenvoieLesOnzeActesDuSeed() {
        assertEquals(11, repo.findAll().size());
    }

    @Test
    void findByIdRetrouveUnActe() {
        assertEquals("Détartrage", repo.findById(2L).getLibelle());
        assertNull(repo.findById(999L));
    }

    @Test
    void saveAttribueUnNouvelIdentifiantEtConserveLActe() {
        Acte sauve = repo.save(nouveau(null, "Pose de facette", "1800.00"));

        assertEquals(12L, sauve.getId());
        assertEquals("Pose de facette", repo.findById(12L).getLibelle());
        assertEquals(12, repo.findAll().size());
    }

    @Test
    void updateModifieUnActeExistant() {
        assertTrue(repo.update(nouveau(1L, "Consultation de contrôle", "250.00")));
        assertEquals("Consultation de contrôle", repo.findById(1L).getLibelle());
    }

    @Test
    void updateRefuseUnActeInexistant() {
        assertFalse(repo.update(nouveau(999L, "Inconnu", "10.00")));
    }

    @Test
    void deleteByIdSupprimeLActe() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertFalse(repo.deleteById(1L)); // déjà supprimé
    }

    @Test
    void findByLibelleLikeChercheSansTenirCompteDeLaCasseEtTrie() {
        List<Acte> resultat = repo.findByLibelleLike("EXTRACTION");

        assertEquals(2, resultat.size());
        assertEquals("Extraction dent de sagesse", resultat.get(0).getLibelle()); // d avant s
        assertEquals("Extraction simple", resultat.get(1).getLibelle());
    }
}
