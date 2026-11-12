package ma.dentalpro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
