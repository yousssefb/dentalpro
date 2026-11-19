package ma.dentalpro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ma.dentalpro.domain.Medicament;
import ma.dentalpro.repository.memory.MedicamentRepoImpl;
import ma.dentalpro.repository.memory.VirtualDataBase;
import ma.dentalpro.service.impl.MedicamentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests du service des médicaments, avec le repository en mémoire (sans MySQL). */
class MedicamentServiceImplTest {

    private final MedicamentServiceImpl service = new MedicamentServiceImpl(new MedicamentRepoImpl());

    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    private static Medicament medicament(String nom, String forme) {
        return Medicament.builder().nom(nom).forme(forme).build();
    }

    @Test
    void crudCompletSurUnMedicament() {
        // Create
        Medicament sauve = service.save(medicament("Articaïne 4 %", "Injectable"));
        assertNotNull(sauve.getId());

        // Read
        assertEquals("Articaïne 4 %", service.findById(sauve.getId()).getNom());
        assertEquals(1, service.findByNomLike("articaïne").size());

        // Update
        sauve.setForme("Ampoule");
        assertTrue(service.update(sauve));
        assertEquals("Ampoule", service.findById(sauve.getId()).getForme());

        // Delete
        assertTrue(service.deleteById(sauve.getId()));
        assertNull(service.findById(sauve.getId()));
    }

    @Test
    void saveRefuseUnNomVide() {
        verifierRefus(medicament("  ", "Gel"));
    }

    @Test
    void saveRefuseUnDoublonNomEtForme() {
        verifierRefus(medicament("paracétamol 1 g", "COMPRIMÉ"));
    }

    @Test
    void saveAccepteLeMemeNomAvecUneAutreForme() {
        Medicament sirop = service.save(medicament("Paracétamol 1 g", "Sirop"));
        assertNotNull(sirop.getId());
    }

    @Test
    void updateAutoriseDeGarderSonPropreNom() {
        Medicament amoxicilline = service.findById(1L);
        amoxicilline.setActif(false); // on désactive seulement

        assertTrue(service.update(amoxicilline));
        assertEquals(false, service.findById(1L).isActif());
    }

    /** Vérifie que save refuse cet objet en lançant une IllegalArgumentException. */
    private void verifierRefus(Medicament medicament) {
        try {
            service.save(medicament);
            fail("Une IllegalArgumentException était attendue");
        } catch (IllegalArgumentException e) {
            // c'est le comportement attendu : le service a refusé les données
        }
    }
}
