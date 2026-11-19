package ma.dentalpro.repository.memory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Intervention;
import ma.dentalpro.repository.api.InterventionRepo;

/**
 * Implémentation en mémoire du repository des interventions cliniques.
 */
public class InterventionRepoImpl implements InterventionRepo {

    public InterventionRepoImpl() {}

    public InterventionRepoImpl(Connection connection) {}

    @Override
    public List<Intervention> findAll() {
        return new ArrayList<>(VirtualDataBase.interventions());
    }

    @Override
    public Intervention findById(Long identity) {
        if (identity == null) {
            return null;
        }
        for (Intervention i : VirtualDataBase.interventions()) {
            if (identity.equals(i.getId())) {
                return i;
            }
        }
        return null;
    }

    @Override
    public List<Intervention> findByConsultationId(Long consultationId) {
        List<Intervention> resultat = new ArrayList<>();
        if (consultationId == null) {
            return resultat;
        }
        for (Intervention i : VirtualDataBase.interventions()) {
            if (consultationId.equals(i.getConsultationId())) {
                resultat.add(i);
            }
        }
        return resultat;
    }

    @Override
    public List<Intervention> findByActeId(Long acteId) {
        List<Intervention> resultat = new ArrayList<>();
        if (acteId == null) {
            return resultat;
        }
        for (Intervention i : VirtualDataBase.interventions()) {
            if (acteId.equals(i.getActeId())) {
                resultat.add(i);
            }
        }
        return resultat;
    }

    @Override
    public Intervention save(Intervention i) {
        if (i == null) {
            throw new IllegalArgumentException("L'intervention ne peut pas être null.");
        }
        if (i.getId() == null) {
            long maxId = 0L;
            for (Intervention existante : VirtualDataBase.interventions()) {
                if (existante.getId() != null && existante.getId() > maxId) {
                    maxId = existante.getId();
                }
            }
            i.setId(maxId + 1L);
        }
        VirtualDataBase.interventions().add(i);
        return i;
    }

    @Override
    public boolean update(Intervention i) {
        if (i == null || i.getId() == null) {
            return false;
        }
        for (int idx = 0; idx < VirtualDataBase.interventions().size(); idx++) {
            Intervention existante = VirtualDataBase.interventions().get(idx);
            if (i.getId().equals(existante.getId())) {
                VirtualDataBase.interventions().set(idx, i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Intervention element) {
        if (element == null || element.getId() == null) {
            return false;
        }
        return deleteById(element.getId());
    }

    @Override
    public boolean deleteById(Long identity) {
        if (identity == null) {
            return false;
        }
        for (int idx = 0; idx < VirtualDataBase.interventions().size(); idx++) {
            Intervention existante = VirtualDataBase.interventions().get(idx);
            if (identity.equals(existante.getId())) {
                VirtualDataBase.interventions().remove(idx);
                return true;
            }
        }
        return false;
    }
}
