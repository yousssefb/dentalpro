package ma.dentalpro.domain;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Acte médical appliqué lors d'une consultation (table intervention).
 * Exemples : Détartrage, Extraction dent 48, Radio rétro-alvéolaire...
 *
 * <p>Une consultation peut comporter plusieurs interventions. Chaque intervention
 * garde une « photo » du libellé de l'acte au moment des soins (libelleActeSnapshot)
 * pour que les factures et dossiers passés restent exacts même si le catalogue change plus tard.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Intervention {

    // Identifiant unique de l'intervention.
    private Long id;

    // Consultation à laquelle est rattachée cette intervention (clé étrangère).
    private Long consultationId;

    // Acte de référence issu du catalogue (clé étrangère).
    private Long acteId;

    // Copie conforme du libellé de l'acte au moment des soins.
    private String libelleActeSnapshot;

    // Numérotation FDI de la dent traitée (ex. "48", "36"), ou null pour un acte général (détartrage).
    private String dent;

    // Quantité d'actes réalisés (au minimum 1).
    @Builder.Default
    private Integer quantite = 1;

    // Prix unitaire facturé en MAD (BigDecimal pour éviter les imprécisions d'arrondi).
    private BigDecimal prixUnitaire;

    /**
     * Calcule le montant total de cette intervention (prix unitaire x quantité).
     *
     * @return le montant total sous forme de BigDecimal
     */
    public BigDecimal getTotal() {
        if (prixUnitaire == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    /** Deux interventions sont égales si elles possèdent le même identifiant. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Intervention)) {
            return false;
        }
        Intervention autre = (Intervention) o;
        return id != null && id.equals(autre.id);
    }

    /** Cohérent avec equals. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
