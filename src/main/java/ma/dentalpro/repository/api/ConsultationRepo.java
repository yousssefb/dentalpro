package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.StatutConsultation;

/**
 * Interface du repository pour les consultations (table consultation).
 */
public interface ConsultationRepo extends CrudRepo<Consultation, Long> {

    /**
     * Recherche toutes les consultations rattachées à un dossier médical.
     *
     * @param dossierId numéro du dossier médical
     * @return la liste des consultations du patient
     */
    List<Consultation> findByDossierId(Long dossierId);

    /**
     * Recherche toutes les consultations conduites par un médecin donné.
     *
     * @param medecinId identifiant du praticien
     * @return la liste des consultations du médecin
     */
    List<Consultation> findByMedecinId(Long medecinId);

    /**
     * Recherche la consultation issue d'un rendez-vous planifié.
     * (Chaque RDV produit au plus une consultation).
     *
     * @param rdvId identifiant du rendez-vous
     * @return la consultation correspondante, ou null
     */
    Consultation findByRdvId(Long rdvId);

    /**
     * Recherche les consultations selon leur statut (BROUILLON ou VALIDEE).
     *
     * @param statut statut recherché
     * @return la liste des consultations filtrées
     */
    List<Consultation> findByStatut(StatutConsultation statut);
}
