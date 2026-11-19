package ma.dentalpro.service.api;

import java.util.List;
import ma.dentalpro.domain.Acte;

public interface ActeService {

    List<Acte> findAll();

    Acte findById(Long idActe);

    List<Acte> findByLibelleLike(String libelle);

    Acte save(Acte acte);

    boolean update(Acte acte);

    boolean delete(Acte acte);

    boolean deleteById(Long idActe);
}
