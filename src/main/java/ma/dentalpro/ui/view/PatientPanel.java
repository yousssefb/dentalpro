package ma.dentalpro.ui.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;
import ma.dentalpro.shared.RepositoryException;
import ma.dentalpro.ui.controllers.api.PatientController;
import ma.dentalpro.ui.model.PatientTableModel;

/**
 * Écran « Patients » (la « Vue » de MVC).
 *
 * <p>Il contient :</p>
 * <ul>
 *   <li>en haut : une zone de recherche par nom ;</li>
 *   <li>au centre : le tableau des patients ;</li>
 *   <li>en bas : les boutons Ajouter et Modifier.</li>
 * </ul>
 *
 * <p>C'est un JPanel (et pas une JFrame) pour que la fenêtre principale de l'application
 * puisse l'afficher dans sa zone centrale plus tard.</p>
 *
 * <p>Important : l'écran ne parle qu'au contrôleur. Il n'appelle jamais le service
 * ni le repository directement.</p>
 */
public class PatientPanel extends JPanel implements ActionListener {

    // Le contrôleur : seul point de contact entre l'écran et le reste de l'application.
    private PatientController controller;

    // Le modèle du tableau (voir PatientTableModel) et le tableau lui-même.
    private PatientTableModel tableModel;
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
     * @param controller le contrôleur des patients (récupéré depuis ApplicationContext)
     */
    public PatientPanel(PatientController controller) {
        this.controller = controller;

        // BorderLayout découpe le panneau en zones : NORTH (haut), CENTER (centre), SOUTH (bas)...
        setLayout(new BorderLayout());

        // ---------- 1) Zone de recherche (en haut) ----------
        JPanel panelRecherche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtRecherche = new JTextField(20);
        btnRechercher = new JButton("Rechercher");
        btnToutAfficher = new JButton("Tout afficher");
        panelRecherche.add(new JLabel("Nom ou prénom :"));
        panelRecherche.add(txtRecherche);
        panelRecherche.add(btnRechercher);
        panelRecherche.add(btnToutAfficher);
        add(panelRecherche, BorderLayout.NORTH);

        // ---------- 2) Tableau des patients (au centre) ----------
        tableModel = new PatientTableModel();
        table = new JTable(tableModel);
        // On ne peut sélectionner qu'une seule ligne à la fois.
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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
        // Appuyer sur Entrée dans la zone de recherche lance aussi la recherche.
        txtRecherche.addActionListener(this);

        // Au démarrage, on affiche tous les patients.
        chargerTousLesPatients();
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
            chargerTousLesPatients();
        } else if (e.getSource() == btnAjouter) {
            ajouterPatient();
        } else if (e.getSource() == btnModifier) {
            modifierPatient();
        }
    }

    // =====================================================================================
    // Actions de l'écran
    // =====================================================================================

    /** Charge tous les patients dans le tableau. */
    private void chargerTousLesPatients() {
        try {
            List<Patient> patients = controller.findAll();
            tableModel.setPatients(patients);
        } catch (RepositoryException ex) {
            // Erreur de base de données : on prévient l'utilisateur au lieu de planter.
            afficherErreur(ex.getSummary());
        }
    }

    /** Cherche les patients dont le nom ou le prénom contient le texte saisi. */
    private void rechercher() {
        String motCle = txtRecherche.getText().trim();
        try {
            List<Patient> resultat = controller.findByNomLike(motCle);
            tableModel.setPatients(resultat);
            if (resultat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun patient trouvé pour « " + motCle + " ».");
            }
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
    }

    /** Ouvre le formulaire vide puis enregistre le nouveau patient. */
    private void ajouterPatient() {
        Patient nouveau = new Patient();

        // Si l'utilisateur clique sur « Annuler », on s'arrête là.
        boolean valide = afficherFormulaire(nouveau, "Ajouter un patient");
        if (!valide) {
            return;
        }

        try {
            controller.save(nouveau);
            JOptionPane.showMessageDialog(this, "Patient ajouté avec succès.");
            chargerTousLesPatients();
        } catch (IllegalArgumentException ex) {
            // Message envoyé par le service (exemple : « Le nom du patient est obligatoire. »).
            afficherErreur(ex.getMessage());
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
    }

    /** Ouvre le formulaire rempli avec le patient sélectionné puis enregistre les modifications. */
    private void modifierPatient() {
        int ligne = table.getSelectedRow();
        // getSelectedRow() renvoie -1 si aucune ligne n'est sélectionnée.
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord sélectionner un patient dans le tableau.");
            return;
        }

        Patient patient = tableModel.getPatientAt(ligne);

        boolean valide = afficherFormulaire(patient, "Modifier un patient");
        if (!valide) {
            // L'utilisateur a annulé : on recharge pour retrouver les valeurs d'origine.
            chargerTousLesPatients();
            return;
        }

        try {
            boolean modifie = controller.update(patient);
            if (modifie) {
                JOptionPane.showMessageDialog(this, "Patient modifié avec succès.");
            } else {
                afficherErreur("Le patient n'a pas pu être modifié.");
            }
        } catch (IllegalArgumentException ex) {
            afficherErreur(ex.getMessage());
        } catch (RepositoryException ex) {
            afficherErreur(ex.getSummary());
        }
        chargerTousLesPatients();
    }

    // =====================================================================================
    // Formulaire d'ajout / de modification
    // =====================================================================================

    /**
     * Affiche un petit formulaire dans une boîte de dialogue JOptionPane.
     * Les champs sont pré-remplis avec les valeurs du patient (vides pour un nouveau patient).
     * Si l'utilisateur valide, les valeurs saisies sont copiées dans l'objet patient.
     *
     * @param patient le patient à remplir (nouveau ou existant)
     * @param titre   le titre de la fenêtre
     * @return true si l'utilisateur a cliqué sur OK avec une date correcte, false s'il a annulé
     */
    private boolean afficherFormulaire(Patient patient, String titre) {
        // 1) Création des champs, pré-remplis avec les valeurs actuelles du patient.
        JTextField txtNom = new JTextField(texte(patient.getNom()), 20);
        JTextField txtPrenom = new JTextField(texte(patient.getPrenom()), 20);
        JTextField txtDate = new JTextField(20);
        if (patient.getDateNaissance() != null) {
            txtDate.setText(patient.getDateNaissance().toString()); // format AAAA-MM-JJ
        }
        // Liste déroulante du sexe : la première valeur vide veut dire « non renseigné ».
        JComboBox<String> comboSexe = new JComboBox<>(new String[] {"", "HOMME", "FEMME"});
        if (patient.getSexe() != null) {
            comboSexe.setSelectedItem(patient.getSexe().name());
        }
        JTextField txtTelephone = new JTextField(texte(patient.getTelephone()), 20);
        JTextField txtCin = new JTextField(texte(patient.getCin()), 20);
        JTextField txtEmail = new JTextField(texte(patient.getEmail()), 20);
        JTextField txtAdresse = new JTextField(texte(patient.getAdresse()), 20);

        // 2) Mise en page : GridLayout(0, 2) = 2 colonnes (libellé | champ), autant de lignes que nécessaire.
        JPanel formulaire = new JPanel(new GridLayout(0, 2, 5, 5));
        formulaire.add(new JLabel("Nom * :"));
        formulaire.add(txtNom);
        formulaire.add(new JLabel("Prénom * :"));
        formulaire.add(txtPrenom);
        formulaire.add(new JLabel("Date de naissance (AAAA-MM-JJ) :"));
        formulaire.add(txtDate);
        formulaire.add(new JLabel("Sexe :"));
        formulaire.add(comboSexe);
        formulaire.add(new JLabel("Téléphone :"));
        formulaire.add(txtTelephone);
        formulaire.add(new JLabel("CIN :"));
        formulaire.add(txtCin);
        formulaire.add(new JLabel("E-mail :"));
        formulaire.add(txtEmail);
        formulaire.add(new JLabel("Adresse :"));
        formulaire.add(txtAdresse);

        // 3) Affichage de la boîte de dialogue avec les boutons OK et Annuler.
        int choix = JOptionPane.showConfirmDialog(this, formulaire, titre,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choix != JOptionPane.OK_OPTION) {
            return false;
        }

        // 4) Lecture de la date : si le format est faux, on affiche une erreur.
        LocalDate dateNaissance = null;
        String dateTexte = txtDate.getText().trim();
        if (!dateTexte.isEmpty()) {
            try {
                dateNaissance = LocalDate.parse(dateTexte);
            } catch (DateTimeParseException ex) {
                afficherErreur("Date invalide. Exemple de format attendu : 2005-03-14");
                return false;
            }
        }

        // 5) Copie des valeurs saisies dans l'objet patient.
        //    (La vérification du nom et du prénom est faite par le service, pas ici.)
        patient.setNom(txtNom.getText().trim());
        patient.setPrenom(txtPrenom.getText().trim());
        patient.setDateNaissance(dateNaissance);
        String sexeChoisi = (String) comboSexe.getSelectedItem();
        if (sexeChoisi == null || sexeChoisi.isEmpty()) {
            patient.setSexe(null);
        } else {
            patient.setSexe(Sexe.valueOf(sexeChoisi)); // transforme "HOMME" en Sexe.HOMME
        }
        patient.setTelephone(videVersNull(txtTelephone.getText()));
        patient.setCin(videVersNull(txtCin.getText()));
        patient.setEmail(videVersNull(txtEmail.getText()));
        patient.setAdresse(videVersNull(txtAdresse.getText()));
        return true;
    }

    // =====================================================================================
    // Petites méthodes utilitaires
    // =====================================================================================

    /** Affiche un message d'erreur dans une boîte de dialogue. */
    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /** Évite d'afficher le mot « null » dans un champ : renvoie "" si la valeur est absente. */
    private String texte(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur;
    }

    /**
     * Un champ laissé vide est enregistré comme null (valeur absente) dans la base.
     * C'est important pour le CIN : la base accepte plusieurs CIN vides (null),
     * mais refuse deux fois le même texte, même vide "".
     */
    private String videVersNull(String valeur) {
        String propre = valeur.trim();
        if (propre.isEmpty()) {
            return null;
        }
        return propre;
    }
}
