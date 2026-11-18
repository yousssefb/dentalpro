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
        // Règle métier : on vérifie le patient AVANT de l'envoyer au repository.
        verifierPatient(patient);
        return repo.save(patient);
    }

    @Override
    public boolean update(Patient patient) {
        // Même vérification pour une modification.
        verifierPatient(patient);
        return repo.update(patient);
    }

    /**
     * Vérifie les champs obligatoires d'un patient : le nom et le prénom.
     * Si une règle n'est pas respectée, on lance une IllegalArgumentException
     * avec un message en français que l'écran pourra afficher à l'utilisateur.
     */
    private void verifierPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Le patient ne peut pas être vide.");
        }
        // isBlank() est vrai pour "" ou "   " (texte vide ou seulement des espaces).
        if (patient.getNom() == null || patient.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du patient est obligatoire.");
        }
        if (patient.getPrenom() == null || patient.getPrenom().isBlank()) {
            throw new IllegalArgumentException("Le prénom du patient est obligatoire.");
        }
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
