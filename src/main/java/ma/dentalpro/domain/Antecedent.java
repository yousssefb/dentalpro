package ma.dentalpro.domain;

import java.util.Comparator;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fiche d'un antécédent médical ou facteur de risque (table antecedent).
 * Exemples : Diabète, Allergie à la pénicilline, Asthme, Tabagisme...
 *
 * <p>Ces antécédents forment un catalogue de référence que l'on peut ensuite
 * associer au dossier médical d'un patient avec un niveau de risque.</p>
 *
 * <p>Lombok génère automatiquement les getters, setters et constructeurs.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Antecedent implements Comparable<Antecedent> {

    // Identifiant unique dans la base de données (clé primaire).
    private Long id;

    // Nom de l'antécédent (ex : "Allergie à la pénicilline").
    private String libelle;

    // Catégorie (ex : "ALLERGIE", "MALADIE_CHRONIQUE", "HABITUDE").
    private String categorie;

    // Vrai si l'antécédent est actif (proposé lors de la saisie).
    @Builder.Default
    private boolean actif = true;

    /** Deux antécédents sont considérés identiques s'ils ont le même identifiant. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Antecedent)) {
            return false;
        }
        Antecedent autre = (Antecedent) o;
        return id != null && id.equals(autre.id);
    }

    /** Cohérent avec equals : basé uniquement sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /** Tri naturel : ordre alphabétique par libellé (insensible aux majuscules/minuscules). */
    @Override
    public int compareTo(Antecedent autre) {
        if (this.libelle == null && autre.libelle == null) {
            return 0;
        }
        if (this.libelle == null) {
            return 1;
        }
        if (autre.libelle == null) {
            return -1;
        }
        return this.libelle.compareToIgnoreCase(autre.libelle);
    }
}
