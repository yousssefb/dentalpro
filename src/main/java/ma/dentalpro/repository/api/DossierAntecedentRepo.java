package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.DossierAntecedentId;

/**
 * Interface du repository pour les antécédents associés aux dossiers médicaux (table dossier_antecedent).
 */
public interface DossierAntecedentRepo extends CrudRepo<DossierAntecedent, DossierAntecedentId> {

    /**
     * Récupère tous les antécédents déclarés pour un dossier médical donné.
     *
     * @param dossierId numéro du dossier médical
     * @return la liste des antécédents du dossier
     */
    List<DossierAntecedent> findByDossierId(Long dossierId);

    /**
     * Récupère tous les dossiers médicaux qui ont un antécédent donné.
     *
     * @param antecedentId identifiant de l'antécédent
     * @return la liste des dossiers associés
     */
    List<DossierAntecedent> findByAntecedentId(Long antecedentId);

    /**
     * Recherche l'association précise entre un dossier médical et un antécédent.
     *
     * @param dossierId    numéro du dossier médical
     * @param antecedentId numéro de l'antécédent
     * @return l'objet DossierAntecedent s'il existe, null sinon
     */
    DossierAntecedent findByDossierIdAndAntecedentId(Long dossierId, Long antecedentId);

    /**
     * Supprime l'association entre un dossier et un antécédent.
     *
     * @param dossierId    numéro du dossier
     * @param antecedentId numéro de l'antécédent
     * @return true si la suppression a été effectuée
     */
    boolean deleteByDossierIdAndAntecedentId(Long dossierId, Long antecedentId);
}
