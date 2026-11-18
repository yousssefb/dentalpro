package ma.dentalpro.repository.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.repository.api.PatientRepo;

/**
 * Implémentation en mémoire du repository des patients (sans MySQL).
 * Les patients sont stockés dans la liste unique de {@link VirtualDataBase}.
 */
public class PatientRepoImpl implements PatientRepo {

    @Override
    public List<Patient> findAll() {
        // On renvoie une copie pour que l'appelant ne modifie pas la "base" par erreur.
        return new ArrayList<>(VirtualDataBase.patients());
    }

    @Override
    public Patient findById(Long identity) {
        if (identity == null) {
            return null;
        }
        // On parcourt la liste jusqu'à trouver le bon identifiant.
        for (Patient p : VirtualDataBase.patients()) {
            if (identity.equals(p.getId())) {
                return p;
            }
        }
        return null; // aucun patient avec cet identifiant
    }

    @Override
    public List<Patient> findByNomLike(String mc) {
        // Mot-clé en minuscules pour une recherche insensible à la casse.
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim().toLowerCase();
        }

        List<Patient> resultat = new ArrayList<>();
        for (Patient p : VirtualDataBase.patients()) {
            if (contient(p.getNom(), motCle) || contient(p.getPrenom(), motCle)) {
                resultat.add(p);
            }
        }
        Collections.sort(resultat); // tri par nom puis prénom
        return resultat;
    }

    @Override
    public synchronized Patient save(Patient newElement) {
        if (newElement == null) {
            throw new IllegalArgumentException("Le patient à enregistrer ne peut pas être null");
        }
        // Nouvel identifiant = le plus grand identifiant existant + 1.
        long maxId = 0;
        for (Patient p : VirtualDataBase.patients()) {
            if (p.getId() > maxId) {
                maxId = p.getId();
            }
        }
        newElement.setId(maxId + 1);
        VirtualDataBase.patients().add(newElement);
        return newElement;
    }

    @Override
    public synchronized boolean update(Patient newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) {
            return false;
        }
        // indexOf utilise equals, qui compare uniquement l'identifiant.
        int index = VirtualDataBase.patients().indexOf(newValuesElement);
        if (index < 0) {
            return false; // patient introuvable
        }
        VirtualDataBase.patients().set(index, newValuesElement);
        return true;
    }

    @Override
    public boolean delete(Patient element) {
        if (element == null) {
            return false;
        }
        return VirtualDataBase.patients().remove(element);
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
