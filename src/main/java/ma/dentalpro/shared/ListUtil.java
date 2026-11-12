package ma.dentalpro.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Méthodes génériques sur les listes (exercice du cours sur les génériques).
 *
 * <p>{@code <T extends Comparable<T>>} veut dire : T peut être n'importe quel type
 * (Integer, String, Patient…) à condition qu'on puisse comparer deux T avec compareTo.</p>
 */
public final class ListUtil {

    private ListUtil() {}

    /**
     * Calcule le max dans une liste d'éléments comparables.
     */
    public static <T extends Comparable<T>> T max(List<T> liste) {
        if (liste == null || liste.isEmpty()) {
            throw new IllegalArgumentException("La liste est vide : pas de maximum.");
        }

        T max = liste.get(0);
        for (int i = 1; i < liste.size(); i++) {
            if (liste.get(i).compareTo(max) > 0) {
                max = liste.get(i);
            }
        }
        return max;
    }

    /**
     * Inverse les éléments d'une liste. La liste de départ n'est pas modifiée.
     * Ici T n'a pas besoin d'être comparable.
     */
    public static <T> List<T> inverser(List<T> liste) {
        List<T> resultat = new ArrayList<>();
        for (int i = liste.size() - 1; i >= 0; i--) {
            resultat.add(liste.get(i));
        }
        return resultat;
    }

    /**
     * Teste si la liste est ordonnée (ordre croissant).
     */
    public static <T extends Comparable<T>> boolean estOrdonnee(List<T> liste) {
        for (int i = 0; i < liste.size() - 1; i++) {
            if (liste.get(i).compareTo(liste.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fusionne deux listes triées dans une liste résultat, elle aussi triée.
     */
    public static <T extends Comparable<T>> List<T> fusionner(List<T> liste1, List<T> liste2) {
        if (!estOrdonnee(liste1) || !estOrdonnee(liste2)) {
            throw new IllegalArgumentException("Les deux listes doivent être triées.");
        }

        List<T> resultat = new ArrayList<>();
        int i = 0;
        int j = 0;

        // On prend à chaque fois le plus petit des deux éléments courants.
        while (i < liste1.size() && j < liste2.size()) {
            if (liste1.get(i).compareTo(liste2.get(j)) <= 0) {
                resultat.add(liste1.get(i));
                i++;
            } else {
                resultat.add(liste2.get(j));
                j++;
            }
        }

        // On ajoute ce qui reste dans l'une des deux listes.
        while (i < liste1.size()) {
            resultat.add(liste1.get(i));
            i++;
        }
        while (j < liste2.size()) {
            resultat.add(liste2.get(j));
            j++;
        }

        return resultat;
    }
}
