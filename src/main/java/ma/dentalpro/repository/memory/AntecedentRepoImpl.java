package ma.dentalpro.repository.memory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Antecedent;
import ma.dentalpro.repository.api.AntecedentRepo;

/**
 * Implémentation en mémoire du repository pour les antécédents médicaux.
 *
 * <p>Utilise la liste statique de VirtualDataBase, sans base de données MySQL.</p>
 */
public class AntecedentRepoImpl implements AntecedentRepo {

    /** Constructeur par défaut. */
    public AntecedentRepoImpl() {}

    /**
     * Constructeur avec Connection pour compatibilité avec ApplicationContext
     * si l'on bascule l'application en mode mémoire via application.properties.
     */
    public AntecedentRepoImpl(Connection connection) {
        // La connexion n'est pas utilisée en mode mémoire.
    }

    @Override
    public List<Antecedent> findAll() {
        return new ArrayList<>(VirtualDataBase.antecedents());
    }

    @Override
    public Antecedent findById(Long identity) {
        if (identity == null) {
            return null;
        }
        for (Antecedent a : VirtualDataBase.antecedents()) {
            if (identity.equals(a.getId())) {
                return a;
            }
        }
        return null;
    }

    @Override
    public List<Antecedent> findByLibelleLike(String motCle) {
        List<Antecedent> resultat = new ArrayList<>();
        if (motCle == null) {
            return resultat;
        }
        String recherche = motCle.trim().toLowerCase();
        for (Antecedent a : VirtualDataBase.antecedents()) {
            if (a.getLibelle() != null && a.getLibelle().toLowerCase().contains(recherche)) {
                resultat.add(a);
            }
        }
        return resultat;
    }

    @Override
    public List<Antecedent> findByCategorie(String categorie) {
        List<Antecedent> resultat = new ArrayList<>();
        if (categorie == null) {
            return resultat;
        }
        for (Antecedent a : VirtualDataBase.antecedents()) {
            if (categorie.equalsIgnoreCase(a.getCategorie())) {
                resultat.add(a);
            }
        }
        return resultat;
    }

    @Override
    public Antecedent save(Antecedent a) {
        if (a == null) {
            throw new IllegalArgumentException("L'antécédent ne peut pas être null.");
        }
        if (a.getId() == null) {
            long maxId = 0L;
            for (Antecedent existant : VirtualDataBase.antecedents()) {
                if (existant.getId() != null && existant.getId() > maxId) {
                    maxId = existant.getId();
                }
            }
            a.setId(maxId + 1L);
        }
        VirtualDataBase.antecedents().add(a);
        return a;
    }

    @Override
    public boolean update(Antecedent a) {
        if (a == null || a.getId() == null) {
            return false;
        }
        for (int i = 0; i < VirtualDataBase.antecedents().size(); i++) {
            Antecedent existant = VirtualDataBase.antecedents().get(i);
            if (a.getId().equals(existant.getId())) {
                VirtualDataBase.antecedents().set(i, a);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Antecedent element) {
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
        for (int i = 0; i < VirtualDataBase.antecedents().size(); i++) {
            Antecedent existant = VirtualDataBase.antecedents().get(i);
            if (identity.equals(existant.getId())) {
                VirtualDataBase.antecedents().remove(i);
                return true;
            }
        }
        return false;
    }
}
