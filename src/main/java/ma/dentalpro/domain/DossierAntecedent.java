package ma.dentalpro.domain;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Antécédent rattaché au dossier médical d'un patient spécifique (table dossier_antecedent).
 *
 * <p>Indique pour ce patient particulier quel est le niveau de risque encouru
 * (FAIBLE, MOYEN, ELEVE) et des observations cliniques éventuelles.</p>
 *
 * <p>Sa clé primaire en base est composée de (dossier_id, antecedent_id).</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DossierAntecedent {

    // Référence vers le dossier médical (clé étrangère).
    private Long dossierId;

    // Référence vers l'antécédent du catalogue (clé étrangère).
    private Long antecedentId;

    // Niveau de risque pour ce patient : FAIBLE, MOYEN ou ELEVE.
    private NiveauRisque niveauRisque;

    // Précisions ou remarques du praticien (ex. "Réaction cutanée en 2019").
    private String commentaire;

    // Vrai si cet antécédent est actuellement actif pour ce patient.
    @Builder.Default
    private boolean actif = true;

    /** Retourne l'identifiant composite de cette relation (dossierId, antecedentId). */
    public DossierAntecedentId getId() {
        return new DossierAntecedentId(dossierId, antecedentId);
    }

    /** Permet de définir la clé composite en une seule fois. */
    public void setId(DossierAntecedentId id) {
        if (id != null) {
            this.dossierId = id.getDossierId();
            this.antecedentId = id.getAntecedentId();
        }
    }

    /** Deux lignes sont égales si elles lient le même dossier au même antécédent. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DossierAntecedent)) {
            return false;
        }
        DossierAntecedent autre = (DossierAntecedent) o;
        return Objects.equals(dossierId, autre.dossierId) &&
               Objects.equals(antecedentId, autre.antecedentId);
    }

    /** Cohérent avec equals. */
    @Override
    public int hashCode() {
        return Objects.hash(dossierId, antecedentId);
    }
}
