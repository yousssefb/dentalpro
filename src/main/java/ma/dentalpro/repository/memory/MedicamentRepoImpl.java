package ma.dentalpro.repository.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ma.dentalpro.domain.Medicament;
import ma.dentalpro.repository.api.MedicamentRepo;

/**
 * Implémentation en mémoire du repository des médicaments (sans MySQL).
 * Les médicaments sont stockés dans la liste unique de {@link VirtualDataBase}.
 */
public class MedicamentRepoImpl implements MedicamentRepo {

    @Override
    public List<Medicament> findAll() {
        // Copie de la liste : l'appelant ne peut pas modifier la "base" directement.
        return new ArrayList<>(VirtualDataBase.medicaments());
    }

    @Override
    public Medicament findById(Long identity) {
        if (identity == null) {
            return null;
        }
        for (Medicament m : VirtualDataBase.medicaments()) {
            if (identity.equals(m.getId())) {
                return m;
            }
        }
        return null; // aucun médicament avec cet identifiant
    }

    @Override
    public List<Medicament> findByNomLike(String mc) {
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim().toLowerCase();
        }

        List<Medicament> resultat = new ArrayList<>();
        for (Medicament m : VirtualDataBase.medicaments()) {
            if (m.getNom() != null && m.getNom().toLowerCase().contains(motCle)) {
                resultat.add(m);
            }
        }
        Collections.sort(resultat); // tri par nom (compareTo de Medicament)
        return resultat;
    }

    @Override
    public synchronized Medicament save(Medicament newElement) {
        if (newElement == null) {
            throw new IllegalArgumentException("Le médicament à enregistrer ne peut pas être null");
        }
        // Nouvel identifiant = le plus grand identifiant existant + 1.
        long maxId = 0;
        for (Medicament m : VirtualDataBase.medicaments()) {
            if (m.getId() > maxId) {
                maxId = m.getId();
            }
        }
        newElement.setId(maxId + 1);
        VirtualDataBase.medicaments().add(newElement);
        return newElement;
    }

    @Override
    public synchronized boolean update(Medicament newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) {
            return false;
        }
        int index = VirtualDataBase.medicaments().indexOf(newValuesElement);
        if (index < 0) {
            return false; // médicament introuvable
        }
        VirtualDataBase.medicaments().set(index, newValuesElement);
        return true;
    }

    @Override
    public boolean delete(Medicament element) {
        if (element == null) {
            return false;
        }
        return VirtualDataBase.medicaments().remove(element);
    }

    @Override
    public boolean deleteById(Long identity) {
        return delete(findById(identity));
    }
}
