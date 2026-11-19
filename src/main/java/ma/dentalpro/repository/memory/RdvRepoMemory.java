package ma.dentalpro.repository.memory;

import ma.dentalpro.domain.Rdv;
import ma.dentalpro.repository.api.RdvRepo;
import java.util.ArrayList;
import java.util.List;

public class RdvRepoMemory implements RdvRepo {
    private List<Rdv> rendezVous = new ArrayList<>();
    private Long prochainId = 1L;

    @Override
    public List<Rdv> findAll() {
        return new ArrayList<>(rendezVous);
    }
    @Override
    public Rdv findById(Long id) {
        for (Rdv rdv : rendezVous) {
            if (rdv.getId().equals(id)) {
                return rdv;
            }
        }
        return null;
    }
    @Override
    public Rdv save(Rdv rdv) {
        rdv.setId(prochainId);
        prochainId++;

        rendezVous.add(rdv);

        return rdv;
    }
    @Override
    public boolean update(Rdv nouveauRdv) {
        for (int i = 0; i < rendezVous.size(); i++) {

            if (rendezVous.get(i).getId().equals(nouveauRdv.getId())) {
                rendezVous.set(i, nouveauRdv);
                return true;
            }
        }

        return false;
    }
    @Override
    public boolean delete(Rdv rdv) {
        return deleteById(rdv.getId());
    }
    @Override
    public boolean deleteById(Long id) {
        Rdv rdv = findById(id);

        if (rdv != null) {
            rendezVous.remove(rdv);
            return true;
        }

        return false;
    }
}
