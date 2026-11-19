package ma.dentalpro.ui.controllers.console;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.service.api.ChargeService;
import ma.dentalpro.ui.controllers.api.ChargeController;

/**
 * Contrôleur des charges.
 *
 * <p>Il ne contient presque aucune logique : il demande au service
 * et renvoie le résultat à l'écran Swing.</p>
 */
@Data
@AllArgsConstructor
public class ChargeControllerImpl implements ChargeController {

    private final ChargeService service;

    @Override
    public List<Charge> findAll() {
        // Le contrôleur ne fait aucun traitement : il demande simplement la liste au service.
        return service.getChargesSortedByDate();
    }

    @Override
    public List<Charge> findByTitreLike(String motCle) {
        return service.findByTitreLike(motCle);
    }

    @Override
    public Charge save(Charge charge) {
        return service.save(charge);
    }

    @Override
    public boolean update(Charge charge) {
        return service.update(charge);
    }

    @Override
    public boolean delete(Charge charge) {
        return service.delete(charge);
    }
}
