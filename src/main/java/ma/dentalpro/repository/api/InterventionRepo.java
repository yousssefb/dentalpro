package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Intervention;

/**
 * Interface du repository pour les actes/interventions d'une consultation (table intervention).
 */
public interface InterventionRepo extends CrudRepo<Intervention, Long> {

    /**
     * Récupère toutes les interventions pratiquées au cours d'une consultation.
     *
     * @param consultationId identifiant de la consultation
     * @return la liste des interventions ordonnées
     */
    List<Intervention> findByConsultationId(Long consultationId);

    /**
     * Recherche les interventions qui ont utilisé un acte donné du catalogue.
     *
     * @param acteId identifiant de l'acte
     * @return la liste des interventions
     */
    List<Intervention> findByActeId(Long acteId);
}
