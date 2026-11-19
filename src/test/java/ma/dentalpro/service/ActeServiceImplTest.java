package ma.dentalpro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.repository.memory.ActeRepoImpl;
import ma.dentalpro.repository.memory.VirtualDataBase;
import ma.dentalpro.service.impl.ActeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests du service des actes, avec le repository en mémoire (sans MySQL). */
class ActeServiceImplTest {

    private final ActeServiceImpl service = new ActeServiceImpl(new ActeRepoImpl());

    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    private static Acte acte(String libelle, String prix) {
        return Acte.builder().libelle(libelle).categorie("TEST").prixBase(prix == null ? null : new BigDecimal(prix)).build();
    }

    @Test
    void crudCompletSurUnActe() {
        // Create
        Acte sauve = service.save(acte("Pose de facette", "1800.00"));
        assertNotNull(sauve.getId());

        // Read
        assertEquals("Pose de facette", service.findById(sauve.getId()).getLibelle());
        assertEquals(1, service.findByLibelleLike("facette").size());

        // Update
        sauve.setLibelle("Pose de facette céramique");
        assertTrue(service.update(sauve));
        assertEquals("Pose de facette céramique", service.findById(sauve.getId()).getLibelle());

        // Delete
        assertTrue(service.deleteById(sauve.getId()));
        assertNull(service.findById(sauve.getId()));
    }

    @Test
    void saveRefuseUnLibelleVide() {
        verifierRefus(acte("   ", "100.00"));
    }

    @Test
    void saveRefuseUnPrixNulOuNegatif() {
        verifierRefus(acte("Test A", "0.00"));
        verifierRefus(acte("Test B", "-5.00"));
        verifierRefus(acte("Test C", null));
    }

    @Test
    void saveRefuseUnLibelleDejaUtiliseMemeEnMajuscules() {
        verifierRefus(acte("DÉTARTRAGE", "100.00"));
    }

    @Test
    void updateAutoriseDeGarderSonPropreLibelle() {
        Acte detartrage = service.findById(2L);
        detartrage.setPrixBase(new BigDecimal("450.00")); // seul le prix change

        assertTrue(service.update(detartrage));
        assertEquals(0, new BigDecimal("450.00").compareTo(service.findById(2L).getPrixBase()));
    }

    /** Vérifie que save refuse cet objet en lançant une IllegalArgumentException. */
    private void verifierRefus(Acte acte) {
        try {
            service.save(acte);
            fail("Une IllegalArgumentException était attendue");
        } catch (IllegalArgumentException e) {
            // c'est le comportement attendu : le service a refusé les données
        }
    }
}
