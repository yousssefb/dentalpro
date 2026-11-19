package ma.dentalpro.ui.controllers.api;

import java.util.List;
import ma.dentalpro.domain.Charge;

/**
 * Contrat du contrôleur des charges.
 *
 * <p>L'écran Swing (la « vue ») parle uniquement à ce contrôleur.
 * Le contrôleur appelle ensuite le service.</p>
 */
public interface ChargeController {

    /** Liste des charges pour remplir le tableau Swing. */
    List<Charge> findAll();

    /** Recherche par titre. */
    List<Charge> findByTitreLike(String motCle);

    /** Ajoute une charge. */
    Charge save(Charge charge);

    /** Modifie une charge. */
    boolean update(Charge charge);

    /** Supprime une charge. */
    boolean delete(Charge charge);
}
