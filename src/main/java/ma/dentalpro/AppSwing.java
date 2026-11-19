package ma.dentalpro;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import ma.dentalpro.config.ApplicationContext;
import ma.dentalpro.ui.controllers.api.ChargeController;
import ma.dentalpro.ui.view.MainFrame;

/**
 * Lanceur Swing de l'application (fenêtre principale) — M6 semaine 1.
 *
 * <p>À lancer depuis IntelliJ : exécuter {@code ma.dentalpro.AppSwing}.</p>
 *
 * <p>La base utilisée (MySQL ou mémoire) dépend des lignes dao.* dans
 * {@code application.properties}.</p>
 */
public class AppSwing {

    public static void main(String[] args) {
        // SwingUtilities.invokeLater place le code de l'interface sur le « thread EDT »
        // (Event Dispatch Thread) : c'est la règle recommandée pour Swing.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                demarrer();
            }
        });
    }

    /** Crée le contexte puis ouvre la fenêtre principale. */
    private static void demarrer() {
        ChargeController chargeController;
        try {
            // On récupère le contrôleur depuis ApplicationContext (jamais de "new" ici).
            chargeController = ApplicationContext.getInstance().getBean(ChargeController.class);
        } catch (IllegalStateException e) {
            // Exemple : MySQL arrêté ou db.properties absent.
            JOptionPane.showMessageDialog(null, "Démarrage impossible : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MainFrame fenetre = new MainFrame(chargeController);
        fenetre.setVisible(true);
    }
}
