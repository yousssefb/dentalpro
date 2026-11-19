package ma.dentalpro.ui.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import ma.dentalpro.domain.Charge;

/**
 * Modèle de données du tableau des charges (le « M » de MVC).
 *
 * <p>Un JTable ne connaît pas nos objets Charge : il demande seulement
 * « combien de lignes ? », « combien de colonnes ? » et « quelle valeur dans la case ? ».
 * Cette classe répond à ces questions à partir d'une liste de charges.</p>
 */
public class ChargeTableModel extends AbstractTableModel {

    // Titres des colonnes affichées en haut du tableau.
    private static final String[] COLONNES = {
            "N°", "Titre", "Description", "Montant (MAD)", "Date"
    };

    // La liste des charges actuellement affichées dans le tableau.
    private List<Charge> charges = new ArrayList<>();

    /**
     * Remplace les charges affichées par une nouvelle liste, puis prévient le tableau
     * qu'il doit se redessiner.
     */
    public void setCharges(List<Charge> nouvellesCharges) {
        // On copie la liste pour ne pas dépendre de la liste d'origine.
        this.charges = new ArrayList<>(nouvellesCharges);
        // Cette méthode (héritée de AbstractTableModel) rafraîchit l'affichage du JTable.
        fireTableDataChanged();
    }

    /** Retourne la charge affichée à la ligne demandée (utile pour « Modifier »). */
    public Charge getChargeAt(int ligne) {
        return charges.get(ligne);
    }

    @Override
    public int getRowCount() {
        // Une ligne par charge.
        return charges.size();
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
        Charge c = charges.get(ligne);

        // Le switch choisit la valeur à afficher selon le numéro de la colonne.
        switch (colonne) {
            case 0:
                return c.getId();
            case 1:
                return c.getTitre();
            case 2:
                return c.getDescription();
            case 3:
                return c.getMontant();
            case 4:
                return c.getDateCharge();
            default:
                return "";
        }
    }
}
