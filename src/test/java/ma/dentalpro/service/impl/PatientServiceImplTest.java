package ma.dentalpro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ma.dentalpro.domain.Patient;
import ma.dentalpro.repository.memory.PatientRepoImpl;
import ma.dentalpro.repository.memory.VirtualDataBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientServiceImplTest {

    private final PatientServiceImpl service = new PatientServiceImpl(new PatientRepoImpl());

    @BeforeEach
    void reinitialiser() {
        VirtualDataBase.reset();
    }

    @Test
    void getPatientsSortedByNameTrieDeAaZ() {
        var noms = service.getPatientsSortedByName().stream().map(Patient::getNom).toList();

        assertEquals("Alaoui", noms.getFirst());
        assertEquals("Tazi", noms.getLast());
        assertEquals(noms.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList(), noms);
    }

    @Test
    void saveRefuseUnPatientSansNom() {
        Patient sansNom = new Patient();
        sansNom.setPrenom("Omar");

        // assertThrows vérifie que l'appel lance bien une IllegalArgumentException.
        IllegalArgumentException erreur = assertThrows(IllegalArgumentException.class, () -> service.save(sansNom));
        assertEquals("Le nom du patient est obligatoire.", erreur.getMessage());
    }

    @Test
    void updateRefuseUnPatientSansPrenom() {
        Patient sansPrenom = new Patient();
        sansPrenom.setId(1L);
        sansPrenom.setNom("Berrada");
        sansPrenom.setPrenom("   "); // que des espaces = vide

        assertThrows(IllegalArgumentException.class, () -> service.update(sansPrenom));
    }

    @Test
    void saveAccepteUnPatientAvecNomEtPrenom() {
        Patient nouveau = new Patient();
        nouveau.setNom("Nadir");
        nouveau.setPrenom("Anis");

        Patient sauve = service.save(nouveau);

        assertNotNull(sauve.getId());
    }
}
