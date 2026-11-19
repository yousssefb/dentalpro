package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Acte;

public interface ActeRepo extends CrudRepo<Acte, Long>
{
    /** Cherche les actes dont le libellé contient le mot-clé (sans tenir compte de la casse). */
    List<Acte> findByLibelleLike(String mc);
}
