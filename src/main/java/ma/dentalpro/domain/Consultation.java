package ma.dentalpro.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fiche d'une consultation clinique au cabinet dentaire (table consultation).
 *
 * <p>Une consultation relie un patient (via son dossier médical) au praticien (médecin).
 * Elle peut découler d'un rendez-vous planifié (rdv_id facultatif).
 * Les actes réalisés au cours de la consultation sont enregistrés sous forme d'interventions.</p>
 *
 * <p>Lombok génère automatiquement les getters, setters et constructeurs.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consultation implements Comparable<Consultation> {

    // Identifiant unique de la consultation (clé primaire auto-incrémentée).
    private Long id;

    // Numéro du dossier médical du patient concerné (clé étrangère).
    private Long dossierId;

    // Identifiant de l'utilisateur ayant le rôle de médecin (clé étrangère).
    private Long medecinId;

    // Rendez-vous d'origine (facultatif : une consultation d'urgence peut ne pas avoir de RDV préalable).
    private Long rdvId;

    // Date et heure précises de la consultation.
    private LocalDateTime dateConsultation;

    // Observations cliniques du médecin (donnée confidentielle réservée aux soignants).
    private String observation;

    // Statut de la consultation : BROUILLON ou VALIDEE.
    @Builder.Default
    private StatutConsultation statut = StatutConsultation.BROUILLON;

    /** Deux consultations sont identiques si elles partagent le même identifiant. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Consultation)) {
            return false;
        }
        Consultation autre = (Consultation) o;
        return id != null && id.equals(autre.id);
    }

    /** Cohérent avec equals : basé sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Tri naturel : consultations les plus récentes en premier (ordre antéchronologique).
     * C'est l'ordre classique d'affichage d'un historique médical.
     */
    @Override
    public int compareTo(Consultation autre) {
        if (this.dateConsultation == null && autre.dateConsultation == null) {
            return 0;
        }
        if (this.dateConsultation == null) {
            return 1;
        }
        if (autre.dateConsultation == null) {
            return -1;
        }
        // Le '-' ou l'inversion autre.compareTo(this) permet d'avoir la plus récente d'abord.
        return autre.dateConsultation.compareTo(this.dateConsultation);
    }
}
