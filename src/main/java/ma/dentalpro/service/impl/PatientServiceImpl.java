package ma.dentalpro.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.repository.api.PatientRepo;
import ma.dentalpro.service.api.PatientService;

@Data @AllArgsConstructor
public class PatientServiceImpl implements PatientService {
    // Couplage faible avec repository
    private final PatientRepo repo;

    @Override
    public List<Patient> getPatientsSortedByName() {
        // On copie la liste reçue du repository pour pouvoir la trier sans risque.
        List<Patient> patients = new ArrayList<>(repo.findAll());
        // Tri croissant A → Z selon Patient.compareTo (nom puis prénom, insensible à la casse).
        Collections.sort(patients);
        return patients;
    }

    @Override
    public Patient save(Patient patient) {
        return repo.save(patient);
    }

    @Override
    public boolean update(Patient patient) {
        return repo.update(patient);
    }

    @Override
    public boolean delete(Patient patient) {
        return repo.delete(patient);
    }
    @Override
    public boolean deleteById(Long idPatient) {
        return repo.deleteById(idPatient);
    }

    @Override
    public List<Patient> findByNomLike(String nom) {
        return repo.findByNomLike(nom);
    }

}
