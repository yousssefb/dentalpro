package ma.dentalpro;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ma.dentalpro.config.ApplicationContext;
import ma.dentalpro.ui.controllers.api.PatientController;
import ma.dentalpro.ui.view.PatientPanel;

/**
 * Lanceur provisoire de l'écran « Patients » (semaine 1, membre M1).
 *
 * <p>Il permet de tester l'écran seul, en attendant la fenêtre principale (M6).
 * La base utilisée (MySQL ou mémoire) dépend de la ligne dao.patientDao
 * dans application.properties.</p>
 */
public class AppSwingPatients {

    public static void main(String[] args) {
        // 1) On récupère le contrôleur depuis ApplicationContext (jamais de "new PatientControllerImpl()").
        PatientController controller;
        try {
            controller = ApplicationContext.getInstance().getBean(PatientController.class);
        } catch (IllegalStateException e) {
            // Exemple : MySQL arrêté ou db.properties absent.
            JOptionPane.showMessageDialog(null, "Démarrage impossible : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2) On crée la fenêtre et on y place l'écran des patients.
        JFrame fenetre = new JFrame("DentalSoft – Patients");
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fermer la fenêtre arrête le programme
        fenetre.add(new PatientPanel(controller));
        fenetre.setSize(900, 500);
        fenetre.setLocationRelativeTo(null); // centre la fenêtre à l'écran
        fenetre.setVisible(true);
    }
}
