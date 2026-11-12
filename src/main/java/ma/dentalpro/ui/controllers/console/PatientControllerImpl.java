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
        var result = service.getPatientsSortedByName();
        result.forEach(patient -> System.out.printf("- %-22s %3s ans%s%s%n",
                patient.getNomComplet(),
                patient.getAge(),
                patient.isEnfant() ? "  [enfant]" : "",
                patient.isArchive() ? "  [archivé]" : ""));
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
