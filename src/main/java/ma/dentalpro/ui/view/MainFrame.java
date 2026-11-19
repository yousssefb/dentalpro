package ma.dentalpro.ui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ma.dentalpro.ui.controllers.api.ChargeController;

/**
 * Fenêtre principale de DentalSoft (responsabilité M6).
 *
 * <p>Elle contient :</p>
 * <ul>
 *   <li>une barre de menu en haut ;</li>
 *   <li>une zone centrale qui change selon le menu choisi (CardLayout).</li>
 * </ul>
 *
 * <p>Les autres membres (M1, M2…) pourront plus tard brancher leurs JPanel
 * (Patients, Agenda…) dans cette fenêtre, au lieu de lancer chacun sa propre JFrame.</p>
 */
public class MainFrame extends JFrame implements ActionListener {

    // Noms des « cartes » affichées au centre (utilisés par CardLayout).
    private static final String CARTE_ACCUEIL = "accueil";
    private static final String CARTE_CHARGES = "charges";

    private JPanel zoneCentrale;
    private CardLayout cartes;

    private JMenuItem itemAccueil;
    private JMenuItem itemCharges;
    private JMenuItem itemAPropos;
    private JMenuItem itemQuitter;

    /**
     * Construit la fenêtre principale.
     *
     * @param chargeController contrôleur des charges (pour l'écran Charges)
     */
    public MainFrame(ChargeController chargeController) {
        super("DentalSoft — YOU SMILE");

        // Fermer la fenêtre arrête le programme.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 550);
        setLocationRelativeTo(null); // centre la fenêtre à l'écran

        // ---------- Zone centrale avec CardLayout ----------
        // CardLayout empile plusieurs panneaux : un seul est visible à la fois.
        cartes = new CardLayout();
        zoneCentrale = new JPanel(cartes);
        zoneCentrale.add(new AccueilPanel(), CARTE_ACCUEIL);
        zoneCentrale.add(new ChargePanel(chargeController), CARTE_CHARGES);
        add(zoneCentrale, BorderLayout.CENTER);

        // ---------- Barre de menu ----------
        setJMenuBar(creerMenu());

        // On commence sur l'accueil.
        cartes.show(zoneCentrale, CARTE_ACCUEIL);
    }

    /** Crée la barre de menu (Fichier, Modules, Aide). */
    private JMenuBar creerMenu() {
        JMenuBar barre = new JMenuBar();

        // Menu Fichier
        JMenu menuFichier = new JMenu("Fichier");
        itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(this);
        menuFichier.add(itemQuitter);

        // Menu Modules : les écrans de l'application
        JMenu menuModules = new JMenu("Modules");
        itemAccueil = new JMenuItem("Accueil");
        itemCharges = new JMenuItem("Charges");
        itemAccueil.addActionListener(this);
        itemCharges.addActionListener(this);
        menuModules.add(itemAccueil);
        menuModules.add(itemCharges);
        // Les autres modules (Patients, Agenda…) seront ajoutés ici plus tard.

        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        itemAPropos = new JMenuItem("À propos");
        itemAPropos.addActionListener(this);
        menuAide.add(itemAPropos);

        barre.add(menuFichier);
        barre.add(menuModules);
        barre.add(menuAide);
        return barre;
    }

    /** Réagit aux clics du menu. */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == itemAccueil) {
            cartes.show(zoneCentrale, CARTE_ACCUEIL);
        } else if (e.getSource() == itemCharges) {
            cartes.show(zoneCentrale, CARTE_CHARGES);
        } else if (e.getSource() == itemAPropos) {
            JOptionPane.showMessageDialog(this,
                    "DentalSoft V1.0\nCabinet YOU SMILE\nFenêtre principale — M6 semaine 1",
                    "À propos",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == itemQuitter) {
            // dispose() ferme la fenêtre ; avec EXIT_ON_CLOSE le programme s'arrête.
            dispose();
        }
    }
}
