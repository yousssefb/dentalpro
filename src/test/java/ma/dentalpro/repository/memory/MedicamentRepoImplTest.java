package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import ma.dentalpro.domain.Medicament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MedicamentRepoImplTest {

    private final MedicamentRepoImpl repo = new MedicamentRepoImpl();

    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    private static Medicament nouveau(Long id, String nom, String forme) {
        return Medicament.builder().id(id).nom(nom).forme(forme).build();
    }

    @Test
    void findAllRenvoieLesHuitMedicamentsDuSeed() {
        assertEquals(8, repo.findAll().size());
    }

    @Test
    void findByIdRetrouveUnMedicament() {
        assertEquals("Paracétamol 1 g", repo.findById(2L).getNom());
        assertNull(repo.findById(999L));
    }

    @Test
    void saveAttribueUnNouvelIdentifiantEtConserveLeMedicament() {
        Medicament sauve = repo.save(nouveau(null, "Articaïne 4 %", "Injectable"));

        assertEquals(9L, sauve.getId());
        assertEquals("Articaïne 4 %", repo.findById(9L).getNom());
        assertTrue(sauve.isActif()); // un nouveau médicament est actif par défaut
    }

    @Test
    void updateModifieUnMedicamentExistant() {
        assertTrue(repo.update(nouveau(1L, "Amoxicilline 500 mg", "Gélule")));
        assertEquals("Gélule", repo.findById(1L).getForme());
    }

    @Test
    void updateRefuseUnMedicamentInexistant() {
        assertFalse(repo.update(nouveau(999L, "Inconnu", null)));
    }

    @Test
    void deleteByIdSupprimeLeMedicament() {
        assertTrue(repo.deleteById(1L));
        assertNull(repo.findById(1L));
        assertFalse(repo.deleteById(1L));
    }

    @Test
    void findByNomLikeChercheSansTenirCompteDeLaCasse() {
        List<Medicament> resultat = repo.findByNomLike("IBU");

        assertEquals(1, resultat.size());
        assertEquals("Ibuprofène 400 mg", resultat.get(0).getNom());
    }
}
