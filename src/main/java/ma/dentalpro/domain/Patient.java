package ma.dentalpro.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fiche administrative d'un patient (table patient).
 *
 * <p>Un patient n'est jamais supprimé : il est archivé (RG-04). Les informations cliniques
 * ne sont pas ici mais dans son dossier médical.</p>
 *
 * <p>Lombok génère automatiquement les getters, les setters et les constructeurs
 * grâce aux annotations ci-dessous.</p>
 */
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Patient implements Comparable<Patient> {

    /** En dessous de cet âge, le patient est considéré comme enfant (décision D-02). */
    public static final int AGE_LIMITE_ENFANT = 12;

    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String cin;
    private String passeport;
    private String email;
    private String telephone;
    private String adresse;
    private String assurance;
    private boolean archive;
    private LocalDateTime createdAt;

    /** Âge en années révolues à la date du jour, ou null si la date de naissance est absente. */
    public Integer getAge() {
        if (dateNaissance == null) {
            return null;
        }
        // Period.between calcule la durée entre deux dates ; getYears() en garde les années.
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    /** Vrai si le patient a moins de {@link #AGE_LIMITE_ENFANT} ans. */
    public boolean isEnfant() {
        Integer age = getAge();
        return age != null && age < AGE_LIMITE_ENFANT;
    }

    /** Nom en majuscules suivi du prénom, par exemple « BENALI Omar ». */
    public String getNomComplet() {
        String nomAffiche = "";
        if (nom != null) {
            nomAffiche = nom.toUpperCase();
        }
        String prenomAffiche = "";
        if (prenom != null) {
            prenomAffiche = prenom;
        }
        return nomAffiche + " " + prenomAffiche;
    }

    /** Deux patients sont égaux s'ils ont le même identifiant (identité d'entité). */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Patient)) {
            return false;
        }
        Patient autre = (Patient) o;
        return id != null && id.equals(autre.id);
    }

    /** Cohérent avec equals : basé uniquement sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Ordre naturel : par nom puis par prénom, sans tenir compte des majuscules.
     * Résultat négatif = this avant autre ; positif = this après autre ; 0 = égalité.
     */
    @Override
    public int compareTo(Patient autre) {
        int resultat = comparerTextes(this.nom, autre.nom);
        if (resultat == 0) {
            // Même nom : on départage avec le prénom.
            resultat = comparerTextes(this.prenom, autre.prenom);
        }
        return resultat;
    }

    /** Compare deux textes sans tenir compte de la casse ; un texte absent (null) passe en dernier. */
    private static int comparerTextes(String a, String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return 1;
        }
        if (b == null) {
            return -1;
        }
        return a.compareToIgnoreCase(b);
    }
}
