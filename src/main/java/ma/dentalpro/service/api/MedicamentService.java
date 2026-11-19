package ma.dentalpro.service.api;

import java.util.List;
import ma.dentalpro.domain.Medicament;

public interface MedicamentService {

    List<Medicament> findAll();

    Medicament findById(Long idMedicament);

    List<Medicament> findByNomLike(String nom);

    Medicament save(Medicament medicament);

    boolean update(Medicament medicament);

    boolean delete(Medicament medicament);

    boolean deleteById(Long idMedicament);
}
