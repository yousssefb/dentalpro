package ma.dentalpro.ui.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Écran d'accueil simple de la fenêtre principale (M6, semaine 1).
 *
 * <p>Il sert de page de démarrage : le menu permettra ensuite d'ouvrir
 * Charges, Patients, Agenda… au fur et à mesure que les autres membres livrent leurs écrans.</p>
 */
public class AccueilPanel extends JPanel {

    public AccueilPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titre = new JLabel("DentalSoft — Cabinet YOU SMILE", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titre, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(0, 1, 8, 8));
        centre.add(new JLabel("Bienvenue dans l'application de gestion du cabinet dentaire.", SwingConstants.CENTER));
        centre.add(new JLabel("Utilisez le menu en haut pour ouvrir un écran.", SwingConstants.CENTER));
        centre.add(new JLabel("Semaine 1 (M6) : fenêtre principale + module Charges.", SwingConstants.CENTER));
        add(centre, BorderLayout.CENTER);
    }
}
