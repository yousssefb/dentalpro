package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Charge;

/**
 * Contrat du repository des charges (dépenses du cabinet).
 *
 * <p>Le service parle à cette interface, jamais directement à MySQL ou à la mémoire.
 * Ainsi on peut changer d'implémentation dans application.properties.</p>
 */
public interface ChargeRepo extends CrudRepo<Charge, Long> {

    /**
     * Cherche les charges dont le titre contient le texte donné
     * (insensible aux majuscules / minuscules).
     */
    List<Charge> findByTitreLike(String motCle);
}
