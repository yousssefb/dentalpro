package ma.dentalpro.repository.memory;

import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.repository.api.ChargeRepo;

/**
 * Implémentation en mémoire du repository des charges (sans MySQL).
 *
 * <p>Utile pour tester l'écran rapidement, ou pour les tests JUnit.
 * Les charges sont stockées dans {@link VirtualDataBase}.</p>
 */
public class ChargeRepoImpl implements ChargeRepo {

    @Override
    public List<Charge> findAll() {
        // On renvoie une copie pour que l'appelant ne modifie pas la "base" par erreur.
        return new ArrayList<>(VirtualDataBase.charges());
    }

    @Override
    public Charge findById(Long identity) {
        if (identity == null) {
            return null;
        }
        // On parcourt la liste jusqu'à trouver le bon identifiant.
        for (Charge c : VirtualDataBase.charges()) {
            if (identity.equals(c.getId())) {
                return c;
            }
        }
        return null; // aucune charge avec cet identifiant
    }

    @Override
    public List<Charge> findByTitreLike(String mc) {
        // Mot-clé en minuscules pour une recherche insensible à la casse.
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim().toLowerCase();
        }

        List<Charge> resultat = new ArrayList<>();
        for (Charge c : VirtualDataBase.charges()) {
            if (contient(c.getTitre(), motCle)) {
                resultat.add(c);
            }
        }
        return resultat;
    }

    @Override
    public synchronized Charge save(Charge newElement) {
        if (newElement == null) {
            throw new IllegalArgumentException("La charge à enregistrer ne peut pas être null");
        }

        // Nouvel identifiant = le plus grand identifiant existant + 1.
        long maxId = 0;
        for (Charge c : VirtualDataBase.charges()) {
            if (c.getId() != null && c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        newElement.setId(maxId + 1);
        VirtualDataBase.charges().add(newElement);
        return newElement;
    }

    @Override
    public synchronized boolean update(Charge newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) {
            return false;
        }
        // indexOf utilise equals, qui compare uniquement l'identifiant.
        int index = VirtualDataBase.charges().indexOf(newValuesElement);
        if (index < 0) {
            return false; // charge introuvable
        }
        VirtualDataBase.charges().set(index, newValuesElement);
        return true;
    }

    @Override
    public boolean delete(Charge element) {
        if (element == null) {
            return false;
        }
        return VirtualDataBase.charges().remove(element);
    }

    @Override
    public boolean deleteById(Long identity) {
        return delete(findById(identity));
    }

    /** Vrai si la valeur contient le mot-clé (déjà en minuscules). */
    private static boolean contient(String valeur, String motCleMinuscule) {
        return valeur != null && valeur.toLowerCase().contains(motCleMinuscule);
    }
}
