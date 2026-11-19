package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Antecedent;

/**
 * Interface du repository pour les antécédents médicaux (table antecedent).
 */
public interface AntecedentRepo extends CrudRepo<Antecedent, Long> {

    /**
     * Recherche les antécédents dont le libellé contient le mot-clé (insensible à la casse).
     *
     * @param motCle texte à rechercher (ex: "allergie")
     * @return la liste des antécédents correspondants
     */
    List<Antecedent> findByLibelleLike(String motCle);

    /**
     * Recherche les antécédents appartenant à une catégorie spécifique.
     *
     * @param categorie nom de la catégorie (ex: "MALADIE_CHRONIQUE", "ALLERGIE")
     * @return la liste des antécédents de cette catégorie
     */
    List<Antecedent> findByCategorie(String categorie);
}
