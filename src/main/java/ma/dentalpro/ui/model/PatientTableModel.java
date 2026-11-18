package ma.dentalpro.ui.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import ma.dentalpro.domain.Patient;

/**
 * Modèle de données du tableau des patients (le « M » de MVC).
 *
 * <p>Un JTable ne connaît pas nos objets Patient : il demande seulement
 * « combien de lignes ? », « combien de colonnes ? » et « quelle valeur dans la case (ligne, colonne) ? ».
 * Cette classe répond à ces questions à partir d'une liste de patients.</p>
 */
public class PatientTableModel extends AbstractTableModel {

    // Titres des colonnes affichées en haut du tableau.
    private static final String[] COLONNES = {
            "N°", "Nom", "Prénom", "Date de naissance", "Âge", "Téléphone", "CIN"
    };

    // La liste des patients actuellement affichés dans le tableau.
    private List<Patient> patients = new ArrayList<>();

    /**
     * Remplace les patients affichés par une nouvelle liste, puis prévient le tableau
     * qu'il doit se redessiner.
     */
    public void setPatients(List<Patient> nouveauxPatients) {
        // On copie la liste pour ne pas dépendre de la liste d'origine.
        this.patients = new ArrayList<>(nouveauxPatients);
        // Cette méthode (héritée de AbstractTableModel) rafraîchit l'affichage du JTable.
        fireTableDataChanged();
    }

    /** Retourne le patient affiché à la ligne demandée (utile pour le bouton « Modifier »). */
    public Patient getPatientAt(int ligne) {
        return patients.get(ligne);
    }

    @Override
    public int getRowCount() {
        // Une ligne par patient.
        return patients.size();
    }

    @Override
    public int getColumnCount() {
        return COLONNES.length;
    }

    @Override
    public String getColumnName(int colonne) {
        return COLONNES[colonne];
    }

    @Override
    public Object getValueAt(int ligne, int colonne) {
        Patient p = patients.get(ligne);

        // Le switch choisit la valeur à afficher selon le numéro de la colonne.
        switch (colonne) {
            case 0:
                return p.getId();
            case 1:
                return p.getNom();
            case 2:
                return p.getPrenom();
            case 3:
                return p.getDateNaissance();
            case 4:
                return p.getAge();
            case 5:
                return p.getTelephone();
            case 6:
                return p.getCin();
            default:
                return "";
        }
    }
}
