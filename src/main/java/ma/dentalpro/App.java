package ma.dentalpro;

import java.util.List;
import ma.dentalpro.config.ApplicationContext;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.shared.RepositoryException;
import ma.dentalpro.ui.controllers.api.PatientController;

/**
 * Point d'entrée provisoire (console) : vérifie que la chaîne
 * Controller → Service → Repository → MySQL fonctionne sur les données de seed.sql.
 * Il sera remplacé par la fenêtre de connexion Swing.
 */
public class App {

    public static void main(String[] args) {
        PatientController controller;
        try {
            controller = ApplicationContext.getInstance().getBean(PatientController.class);
        } catch (IllegalStateException e) {
            System.err.println("[DÉMARRAGE IMPOSSIBLE] " + e.getMessage());
            return;
        }

        try {
            System.out.println("\nPatients (triés par nom) :");
            controller.getAllPatients();

            System.out.println("\nRecherche « ben » :");
            List<Patient> resultat = controller.findByNomLike("ben");
            for (Patient p : resultat) {
                System.out.println("- #" + p.getId() + " " + p.getNomComplet() + ", tél. " + p.getTelephone());
            }
        } catch (RepositoryException e) {
            System.err.println(e.getSummary());
        }
    }
}
