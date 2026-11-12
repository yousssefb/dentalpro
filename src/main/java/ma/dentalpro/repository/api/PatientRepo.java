package ma.dentalpro.repository.api;

import java.util.List;
import ma.dentalpro.domain.Patient;

public interface PatientRepo extends CrudRepo<Patient, Long>
{


    List<Patient> findByNomLike(String mc);
}
