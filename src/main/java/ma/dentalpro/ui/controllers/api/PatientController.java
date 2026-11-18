package ma.dentalpro.ui.controllers.api;

import java.util.List;
import ma.dentalpro.domain.Patient;

/**
 * Contrat des opérations exposées par un contrôleur de patients.
 */
public interface PatientController {

    void getAllPatients();

    /**
     * Retourne la liste des patients triés par nom (A → Z).
     * Utilisée par l'écran Swing pour remplir le tableau.
     */
    List<Patient> findAll();

    Patient save(Patient patient);

    boolean update(Patient patient);

    boolean delete(Patient patient);

    boolean delete(Long idPatient);

    List<Patient> findByNomLike(String nom);
}
