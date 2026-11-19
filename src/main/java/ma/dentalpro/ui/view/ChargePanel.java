package ma.dentalpro.ui.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.shared.RepositoryException;
import ma.dentalpro.ui.controllers.api.ChargeController;
import ma.dentalpro.ui.model.ChargeTableModel;

/**
 * Écran « Charges » (la « Vue » de MVC) — semaine 1, membre M6.
 *
 * <p>Il contient :</p>
 * <ul>
 *   <li>en haut : une zone de recherche par titre ;</li>
 *   <li>au centre : le tableau des charges ;</li>
 *   <li>en bas : les boutons Ajouter et Modifier.</li>
 * </ul>
 *
 * <p>C'est un JPanel (pas une JFrame) pour pouvoir être placé dans la fenêtre principale.</p>
 *
 * <p>Important : l'écran ne parle qu'au contrôleur. Il n'appelle jamais le service
 * ni le repository directement.</p>
 */
public class ChargePanel extends JPanel implements ActionListener {

    /**
     * Auteur provisoire pour la semaine 1 (Dr Benjelloun, id = 2 dans seed.sql).
     * Plus tard, on prendra l'utilisateur connecté.
     */
    private static final Long AUTEUR_DEMO_ID = 2L;

    // Le contrôleur : seul point de contact entre l'écran et le reste de l'application.
    private ChargeController controller;

    // Le modèle du tableau et le tableau lui-même.
    private ChargeTableModel tableModel;
    private JTable table;

    // Composants de la zone de recherche.
    private JTextField txtRecherche;
    private JButton btnRechercher;
    private JButton btnToutAfficher;

    // Boutons du bas.
    private JButton btnAjouter;
    private JButton btnModifier;

    /**
     * Construit l'écran.
     *
     * @param controller le contrôleur des charges (récupéré depuis ApplicationContext)
     */
    public ChargePanel(ChargeController controller) {
        this.controller = controller;

        // BorderLayout découpe le panneau en zones : NORTH (haut), CENTER (centre), SOUTH (bas)...
        setLayout(new BorderLayout());

        // ---------- 1) Zone de recherche (en haut) ----------
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtRecherche = new JTextField(20);
        btnRechercher = new JButton("Rechercher");
        btnToutAfficher = new JButton("Tout afficher");
        panelRecherche.add(new JLabel("Titre :"));
        panelRecherche.add(txtRecherche);
        panelRecherche.add(btnRechercher);
        panelRecherche.add(btnToutAfficher);
        add(panelRecherche, BorderLayout.NORTH);

        // ---------- 2) Tableau des charges (au centre) ----------
        tableModel = new ChargeTableModel();
        table = new JTable(tableModel);
        // On ne peut sélectionner qu'une seule ligne à la fois.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Le JScrollPane ajoute une barre de défilement si la liste est longue.
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- 3) Boutons d'action (en bas) ----------
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        add(panelBoutons, BorderLayout.SOUTH);

        // ---------- 4) Écouteurs des boutons ----------
        // "this" veut dire : c'est CETTE classe qui réagit aux clics (méthode actionPerformed).
        btnRechercher.addActionListener(this);
        btnToutAfficher.addActionListener(this);
        btnAjouter.addActionListener(this);
        btnModifier.addActionListener(this);
        txtRecherche.addActionListener(this);

        // Au démarrage, on affiche toutes les charges.
        chargerToutesLesCharges();
    }

    /**
     * Appelée automatiquement par Swing à chaque clic sur un bouton.
     * On regarde quel bouton a été cliqué avec e.getSource().
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRechercher || e.getSource() == txtRecherche) {
            rechercher();
        } else if (e.getSource() == btnToutAfficher) {
            txtRecherche.setText("");
            chargerToutesLesCharges();
        } else if (e.getSource() == btnAjouter) {
            ajouterCharge();
        } else if (e.getSource() == btnModifier) {
            modifierCharge();
        }
    }

    // =====================================================================================
    // Actions de l'écran
    // =====================================================================================

    /** Charge toutes les charges dans le tableau. */
    private void chargerToutesLesCharges() {
        try {
            List<Charge> charges = controller.findAll();
            tableModel.setCharges(charges);
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
    }

    /** Cherche les charges dont le titre contient le texte saisi. */
    private void rechercher() {
        String motCle = txtRecherche.getText().trim();
        try {
            List<Charge> resultat = controller.findByTitreLike(motCle);
            tableModel.setCharges(resultat);
            if (resultat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucune charge trouvée pour « " + motCle + " ».");
            }
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
    }

    /** Ouvre le formulaire vide puis enregistre la nouvelle charge. */
    private void ajouterCharge() {
        Charge nouvelle = new Charge();
        // Auteur de démo (semaine 1) : plus tard = utilisateur connecté.
        nouvelle.setAuteurId(AUTEUR_DEMO_ID);
        // Date du moment où on crée la charge.
        nouvelle.setDateCharge(LocalDateTime.now());

        boolean valide = afficherFormulaire(nouvelle, "Ajouter une charge");
        if (!valide) {
            return;
        }

        try {
            controller.save(nouvelle);
            JOptionPane.showMessageDialog(this, "Charge ajoutée avec succès.");
            chargerToutesLesCharges();
        } catch (IllegalArgumentException ex) {
            // Message envoyé par le service (exemple : « Le titre est obligatoire. »).
            afficherErreur(ex.getMessage());
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
    }

    /** Ouvre le formulaire rempli avec la charge sélectionnée puis enregistre. */
    private void modifierCharge() {
        int ligne = table.getSelectedRow();
        // getSelectedRow() renvoie -1 si aucune ligne n'est sélectionnée.
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord sélectionner une charge dans le tableau.");
            return;
        }

        Charge charge = tableModel.getChargeAt(ligne);

        boolean valide = afficherFormulaire(charge, "Modifier une charge");
        if (!valide) {
            chargerToutesLesCharges();
            return;
        }

        try {
            boolean modifie = controller.update(charge);
            if (modifie) {
                JOptionPane.showMessageDialog(this, "Charge modifiée avec succès.");
            } else {
                afficherErreur("La charge n'a pas pu être modifiée.");
            }
        } catch (IllegalArgumentException ex) {
            afficherErreur(ex.getMessage());
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
        chargerToutesLesCharges();
    }

    // =====================================================================================
    // Formulaire d'ajout / de modification
    // =====================================================================================

    /**
     * Affiche un petit formulaire dans une boîte de dialogue JOptionPane.
     *
     * @param charge la charge à remplir (nouvelle ou existante)
     * @param titre  le titre de la fenêtre
     * @return true si l'utilisateur a cliqué sur OK avec un montant correct, false sinon
     */
    private boolean afficherFormulaire(Charge charge, String titre) {
        // 1) Création des champs, pré-remplis avec les valeurs actuelles.
        JTextField txtTitre = new JTextField(texte(charge.getTitre()), 20);
        JTextField txtDescription = new JTextField(texte(charge.getDescription()), 20);
        JTextField txtMontant = new JTextField(20);
        if (charge.getMontant() != null) {
            txtMontant.setText(charge.getMontant().toPlainString());
        }

        // 2) Mise en page : GridLayout(0, 2) = 2 colonnes (libellé | champ).
        JPanel formulaire = new JPanel(new GridLayout(0, 2, 5, 5));
        formulaire.add(new JLabel("Titre * :"));
        formulaire.add(txtTitre);
        formulaire.add(new JLabel("Description :"));
        formulaire.add(txtDescription);
        formulaire.add(new JLabel("Montant (MAD) * :"));
        formulaire.add(txtMontant);

        // 3) Affichage de la boîte de dialogue avec les boutons OK et Annuler.
        int choix = JOptionPane.showConfirmDialog(this, formulaire, titre,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choix != JOptionPane.OK_OPTION) {
            return false;
        }

        // 4) Lecture du montant : si le format est faux, on affiche une erreur.
        BigDecimal montant = null;
        String montantTexte = txtMontant.getText().trim();
        if (!montantTexte.isEmpty()) {
            try {
                // new BigDecimal("1200.50") convertit le texte en nombre décimal exact.
                montant = new BigDecimal(montantTexte.replace(',', '.'));
            } catch (NumberFormatException ex) {
                afficherErreur("Montant invalide. Exemple attendu : 1200.50");
                return false;
            }
        }

        // 5) Copie des valeurs saisies dans l'objet charge.
        //    (La vérification titre/montant est faite par le service, pas ici.)
        charge.setTitre(txtTitre.getText().trim());
        charge.setDescription(videVersNull(txtDescription.getText()));
        charge.setMontant(montant);
        if (charge.getAuteurId() == null) {
            charge.setAuteurId(AUTEUR_DEMO_ID);
        }
        if (charge.getDateCharge() == null) {
            charge.setDateCharge(LocalDateTime.now());
        }
        return true;
    }

    // =====================================================================================
    // Petites méthodes utilitaires
    // =====================================================================================

    /** Affiche un message d'erreur dans une boîte de dialogue. */
    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /** Évite d'afficher le mot « null » dans un champ. */
    private String texte(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur;
    }

    /** Un champ laissé vide est enregistré comme null (valeur absente). */
    private String videVersNull(String valeur) {
        String propre = valeur.trim();
        if (propre.isEmpty()) {
            return null;
        }
        return propre;
    }
}
