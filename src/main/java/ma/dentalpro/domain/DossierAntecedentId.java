package ma.dentalpro.domain;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clé composite de la table dossier_antecedent :
 * le couple (dossier_id, antecedent_id).
 *
 * <p>En base de données, la table dossier_antecedent n'a pas un "id" auto-incrémenté :
 * sa clé primaire est la combinaison du numéro de dossier et du numéro d'antécédent.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DossierAntecedentId implements Serializable {

    private Long dossierId;
    private Long antecedentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DossierAntecedentId)) {
            return false;
        }
        DossierAntecedentId autre = (DossierAntecedentId) o;
        return Objects.equals(dossierId, autre.dossierId) &&
               Objects.equals(antecedentId, autre.antecedentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dossierId, antecedentId);
    }
}
