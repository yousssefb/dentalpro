package ma.dentalpro.domain;

import java.util.Comparator;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Médicament du référentiel (table medicament).
 *
 * <p>Un médicament inactif n'est plus proposé pour les ordonnances, mais il reste dans la base
 * pour garder l'historique des anciennes ordonnances.</p>
 */
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Medicament implements Comparable<Medicament> {

    private Long id;
    private String nom;
    private String forme;   // exemple : Comprimé, Gel, Bain de bouche

    // Par défaut, un nouveau médicament est actif.
    @Builder.Default
    private boolean actif = true;

    /** Deux médicaments sont égaux s'ils ont le même identifiant. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Medicament)) {
            return false;
        }
        Medicament autre = (Medicament) o;
        return id != null && id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /** Ordre naturel : par nom, sans tenir compte des majuscules (un nom null passe en dernier). */
    @Override
    public int compareTo(Medicament autre) {
        return Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER).compare(this.nom, autre.nom);
    }
}
