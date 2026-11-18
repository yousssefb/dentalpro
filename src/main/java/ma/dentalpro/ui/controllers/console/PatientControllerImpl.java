package ma.dentalpro.ui.controllers.console;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.service.api.PatientService;
import ma.dentalpro.ui.controllers.api.PatientController;

@Data
@AllArgsConstructor
public class PatientControllerImpl implements PatientController {

    private final PatientService service;

    @Override
    public void getAllPatients() {
        List<Patient> patients = service.getPatientsSortedByName();
        for (Patient patient : patients) {
            String ligne = "- " + patient.getNomComplet() + ", " + patient.getAge() + " ans";
            if (patient.isEnfant()) {
                ligne = ligne + "  [enfant]";
            }
            if (patient.isArchive()) {
                ligne = ligne + "  [archivé]";
            }
            System.out.println(ligne);
        }
    }

    @Override
    public Patient save(Patient patient) {
        return service.save(patient);
    }

    @Override
    public boolean update(Patient patient) {
        return service.update(patient);
    }

    @Override
    public boolean delete(Patient patient) {
        return service.delete(patient);
    }

    @Override
    public boolean delete(Long idPatient) {
        return service.deleteById(idPatient);
    }

    @Override
    public List<Patient> findByNomLike(String nom) {
        return service.findByNomLike(nom);
    }
}
