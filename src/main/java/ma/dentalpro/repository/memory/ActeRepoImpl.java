package ma.dentalpro.repository.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.repository.api.ActeRepo;

/**
 * Implémentation en mémoire du repository des actes (sans MySQL).
 * Les actes sont stockés dans la liste unique de {@link VirtualDataBase}.
 */
public class ActeRepoImpl implements ActeRepo {

    @Override
    public List<Acte> findAll() {
        // On renvoie une copie pour que l'appelant ne modifie pas la "base" par erreur.
        return new ArrayList<>(VirtualDataBase.actes());
    }

    @Override
    public Acte findById(Long identity) {
        if (identity == null) {
            return null;
        }
        // On parcourt la liste jusqu'à trouver le bon identifiant.
        for (Acte a : VirtualDataBase.actes()) {
            if (identity.equals(a.getId())) {
                return a;
            }
        }
        return null; // aucun acte avec cet identifiant
    }

    @Override
    public List<Acte> findByLibelleLike(String mc) {
        // Mot-clé en minuscules pour une recherche insensible à la casse.
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim().toLowerCase();
        }

        List<Acte> resultat = new ArrayList<>();
        for (Acte a : VirtualDataBase.actes()) {
            if (a.getLibelle() != null && a.getLibelle().toLowerCase().contains(motCle)) {
                resultat.add(a);
            }
        }
        Collections.sort(resultat); // tri par libellé (compareTo de Acte)
        return resultat;
    }

    @Override
    public synchronized Acte save(Acte newElement) {
        if (newElement == null) {
            throw new IllegalArgumentException("L'acte à enregistrer ne peut pas être null");
        }
        // Nouvel identifiant = le plus grand identifiant existant + 1.
        long maxId = 0;
        for (Acte a : VirtualDataBase.actes()) {
            if (a.getId() > maxId) {
                maxId = a.getId();
            }
        }
        newElement.setId(maxId + 1);
        VirtualDataBase.actes().add(newElement);
        return newElement;
    }

    @Override
    public synchronized boolean update(Acte newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) {
            return false;
        }
        // indexOf utilise equals, qui compare uniquement l'identifiant.
        int index = VirtualDataBase.actes().indexOf(newValuesElement);
        if (index < 0) {
            return false; // acte introuvable
        }
        VirtualDataBase.actes().set(index, newValuesElement);
        return true;
    }

    @Override
    public boolean delete(Acte element) {
        if (element == null) {
            return false;
        }
        return VirtualDataBase.actes().remove(element);
    }

    @Override
    public boolean deleteById(Long identity) {
        return delete(findById(identity));
    }
}
