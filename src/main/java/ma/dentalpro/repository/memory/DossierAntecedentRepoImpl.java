package ma.dentalpro.repository.memory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.DossierAntecedentId;
import ma.dentalpro.repository.api.DossierAntecedentRepo;

/**
 * Implémentation en mémoire du repository des antécédents de dossiers médicaux.
 */
public class DossierAntecedentRepoImpl implements DossierAntecedentRepo {

    public DossierAntecedentRepoImpl() {}

    public DossierAntecedentRepoImpl(Connection connection) {}

    @Override
    public List<DossierAntecedent> findAll() {
        return new ArrayList<>(VirtualDataBase.dossierAntecedents());
    }

    @Override
    public DossierAntecedent findById(DossierAntecedentId identity) {
        if (identity == null) {
            return null;
        }
        return findByDossierIdAndAntecedentId(identity.getDossierId(), identity.getAntecedentId());
    }

    @Override
    public List<DossierAntecedent> findByDossierId(Long dossierId) {
        List<DossierAntecedent> resultat = new ArrayList<>();
        if (dossierId == null) {
            return resultat;
        }
        for (DossierAntecedent da : VirtualDataBase.dossierAntecedents()) {
            if (dossierId.equals(da.getDossierId())) {
                resultat.add(da);
            }
        }
        return resultat;
    }

    @Override
    public List<DossierAntecedent> findByAntecedentId(Long antecedentId) {
        List<DossierAntecedent> resultat = new ArrayList<>();
        if (antecedentId == null) {
            return resultat;
        }
        for (DossierAntecedent da : VirtualDataBase.dossierAntecedents()) {
            if (antecedentId.equals(da.getAntecedentId())) {
                resultat.add(da);
            }
        }
        return resultat;
    }

    @Override
    public DossierAntecedent findByDossierIdAndAntecedentId(Long dossierId, Long antecedentId) {
        if (dossierId == null || antecedentId == null) {
            return null;
        }
        for (DossierAntecedent da : VirtualDataBase.dossierAntecedents()) {
            if (dossierId.equals(da.getDossierId()) && antecedentId.equals(da.getAntecedentId())) {
                return da;
            }
        }
        return null;
    }

    @Override
    public DossierAntecedent save(DossierAntecedent da) {
        if (da == null || da.getDossierId() == null || da.getAntecedentId() == null) {
            throw new IllegalArgumentException("Le dossierId et l'antecedentId sont obligatoires.");
        }
        // Vérifie si l'association existe déjà
        DossierAntecedent existant = findByDossierIdAndAntecedentId(da.getDossierId(), da.getAntecedentId());
        if (existant != null) {
            update(da);
            return da;
        }
        VirtualDataBase.dossierAntecedents().add(da);
        return da;
    }

    @Override
    public boolean update(DossierAntecedent da) {
        if (da == null || da.getDossierId() == null || da.getAntecedentId() == null) {
            return false;
        }
        for (int i = 0; i < VirtualDataBase.dossierAntecedents().size(); i++) {
            DossierAntecedent existant = VirtualDataBase.dossierAntecedents().get(i);
            if (Objects.equals(da.getDossierId(), existant.getDossierId()) &&
                Objects.equals(da.getAntecedentId(), existant.getAntecedentId())) {
                VirtualDataBase.dossierAntecedents().set(i, da);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(DossierAntecedent element) {
        if (element == null) {
            return false;
        }
        return deleteByDossierIdAndAntecedentId(element.getDossierId(), element.getAntecedentId());
    }

    @Override
    public boolean deleteById(DossierAntecedentId identity) {
        if (identity == null) {
            return false;
        }
        return deleteByDossierIdAndAntecedentId(identity.getDossierId(), identity.getAntecedentId());
    }

    @Override
    public boolean deleteByDossierIdAndAntecedentId(Long dossierId, Long antecedentId) {
        if (dossierId == null || antecedentId == null) {
            return false;
        }
        for (int i = 0; i < VirtualDataBase.dossierAntecedents().size(); i++) {
            DossierAntecedent existant = VirtualDataBase.dossierAntecedents().get(i);
            if (dossierId.equals(existant.getDossierId()) && antecedentId.equals(existant.getAntecedentId())) {
                VirtualDataBase.dossierAntecedents().remove(i);
                return true;
            }
        }
        return false;
    }
}
