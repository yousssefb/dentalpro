package ma.dentalpro.ui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Patient;
import org.junit.jupiter.api.Test;

/** Vérifie que le modèle du tableau affiche bien les patients qu'on lui donne. */
class PatientTableModelTest {

    @Test
    void leTableauAfficheUneLigneParPatient() {
        Patient p1 = new Patient();
        p1.setId(1L);
        p1.setNom("Benali");
        p1.setPrenom("Omar");
        p1.setTelephone("0600000002");

        Patient p2 = new Patient();
        p2.setId(2L);
        p2.setNom("Tazi");
        p2.setPrenom("Amine");

        List<Patient> patients = new ArrayList<>();
        patients.add(p1);
        patients.add(p2);

        PatientTableModel model = new PatientTableModel();
        model.setPatients(patients);

        assertEquals(2, model.getRowCount());
        assertEquals(7, model.getColumnCount());
        assertEquals("Nom", model.getColumnName(1));
        assertEquals("Benali", model.getValueAt(0, 1));
        assertEquals("0600000002", model.getValueAt(0, 5));
        assertEquals("Amine", model.getValueAt(1, 2));
        // getPatientAt renvoie l'objet complet (utilisé par le bouton « Modifier »).
        assertEquals(p2, model.getPatientAt(1));
    }
}
