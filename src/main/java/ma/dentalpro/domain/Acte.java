package ma.dentalpro.domain;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Acte dentaire du catalogue (table acte) : détartrage, extraction, couronne...
 *
 * <p>Le prix de base est un tarif de référence. Un acte n'est pas supprimé quand il a déjà
 * été utilisé : on le désactive (actif = false) pour ne pas casser l'historique.</p>
 *
 * <p>Lombok génère les getters, les setters et les constructeurs grâce aux annotations.</p>
 */
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Acte implements Comparable<Acte> {

    private Long id;
    private String libelle;
    private String categorie;

    // BigDecimal est le bon type pour de l'argent (pas de double : il fait des erreurs d'arrondi).
    private BigDecimal prixBase;

    // Par défaut, un nouvel acte est actif (sans cette annotation, Lombok mettrait false).
    @Builder.Default
    private boolean actif = true;

    /** Deux actes sont égaux s'ils ont le même identifiant. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Acte)) {
            return false;
        }
        Acte autre = (Acte) o;
        return id != null && id.equals(autre.id);
    }

    /** Cohérent avec equals : basé uniquement sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /** Ordre naturel : par libellé, sans tenir compte des majuscules (un libellé null passe en dernier). */
    @Override
    public int compareTo(Acte autre) {
        return Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER).compare(this.libelle, autre.libelle);
    }
}
