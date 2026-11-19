package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Medicament;

public interface MedicamentRepo extends CrudRepo<Medicament, Long>
{
    /** Cherche les médicaments dont le nom contient le mot-clé (sans tenir compte de la casse). */
    List<Medicament> findByNomLike(String mc);
}
