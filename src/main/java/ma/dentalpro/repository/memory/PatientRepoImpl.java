package ma.dentalpro.repository.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.repository.api.PatientRepo;

/**
 * Implémentation en mémoire du repository des patients (sans MySQL).
 */
public class PatientRepoImpl implements PatientRepo {

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(VirtualDataBase.patients());
    }

    @Override
    public Patient findById(Long identity) {
        return VirtualDataBase.patients()
                .stream()
                .filter(p -> Objects.equals(p.getIdPatient(), identity))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Patient> findByNomLike(String mc) {
        var motCle = mc == null ? "" : mc.trim().toLowerCase();
        return VirtualDataBase.patients()
                .stream()
                .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(motCle))
                .sorted()
                .toList();
    }

    @Override
    public synchronized Patient save(Patient newElement) {
        if (newElement == null) {
            throw new IllegalArgumentException("Le patient à enregistrer ne peut pas être null");
        }
        var maxId = VirtualDataBase.patients().stream()
                .mapToLong(Patient::getIdPatient)
                .max()
                .orElse(0L);
        newElement.setIdPatient(maxId + 1);
        VirtualDataBase.patients().add(newElement);
        return newElement;
    }

    @Override
    public synchronized boolean update(Patient newValuesElement) {
        if (newValuesElement == null || newValuesElement.getIdPatient() == null) {
            return false;
        }
        var index = VirtualDataBase.patients().indexOf(newValuesElement);
        if (index < 0) {
            return false;
        }
        VirtualDataBase.patients().set(index, newValuesElement);
        return true;
    }

    @Override
    public boolean delete(Patient element) {
        return element != null && VirtualDataBase.patients().remove(element);
    }

    @Override
    public boolean deleteById(Long identity) {
        return delete(findById(identity));
    }
}
