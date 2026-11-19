package ma.dentalpro.service.api;

import java.util.List;
import ma.dentalpro.domain.Charge;

/**
 * Règles métier autour des charges (dépenses).
 *
 * <p>Le service vérifie les données (titre, montant…) avant d'appeler le repository.</p>
 */
public interface ChargeService {

    /** Liste toutes les charges, les plus récentes en premier. */
    List<Charge> getChargesSortedByDate();

    /** Enregistre une nouvelle charge après validation. */
    Charge save(Charge charge);

    /** Met à jour une charge existante après validation. */
    boolean update(Charge charge);

    /** Supprime une charge (semaine 1 : suppression simple). */
    boolean delete(Charge charge);

    /** Recherche par titre (contient). */
    List<Charge> findByTitreLike(String motCle);
}
