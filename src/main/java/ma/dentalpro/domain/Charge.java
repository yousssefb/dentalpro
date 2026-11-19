package ma.dentalpro.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dépense du cabinet (table charge).
 *
 * <p>Exemple : loyer, électricité, fournitures dentaires.
 * Une charge diminue le solde du cabinet, sans toucher aux factures patients.</p>
 *
 * <p>Lombok (@Data, @Builder…) génère automatiquement les getters, les setters
 * et les constructeurs : on n'a pas besoin de les écrire à la main.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Charge {

    /** Identifiant unique (généré par MySQL ou par le repository en mémoire). */
    private Long id;

    /**
     * Identifiant de l'utilisateur qui a saisi la charge.
     * Pour la semaine 1, on met un auteur de démo dans l'écran (pas encore de connexion).
     */
    private Long auteurId;

    /** Titre court obligatoire, par exemple « Loyer du cabinet ». */
    private String titre;

    /** Détail facultatif, par exemple « Loyer mensuel mars ». */
    private String description;

    /**
     * Montant en MAD.
     * On utilise BigDecimal (et pas double) pour l'argent : les virgules flottantes
     * peuvent donner des erreurs d'arrondi (0.1 + 0.2 ≠ 0.3).
     */
    private BigDecimal montant;

    /** Date et heure de la dépense. */
    private LocalDateTime dateCharge;

    /**
     * Deux charges sont égales si elles ont le même identifiant.
     * C'est la règle habituelle pour une « entité » en base de données.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Charge)) {
            return false;
        }
        Charge autre = (Charge) o;
        return id != null && id.equals(autre.id);
    }

    /** Doit rester cohérent avec equals : basé uniquement sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
