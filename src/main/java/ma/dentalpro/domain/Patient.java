package ma.dentalpro.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
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
 */
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Patient implements Comparable<Patient> {

    /** En dessous de cet âge, le patient est considéré comme enfant (décision à valider). */
    public static final int AGE_LIMITE_ENFANT = 12;

    /** Tri par nom puis prénom, insensible à la casse ; les valeurs absentes passent en dernier. */
    private static final Comparator<Patient> PAR_NOM_PRENOM = Comparator
            .comparing(Patient::getNom, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
            .thenComparing(Patient::getPrenom, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

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
        return dateNaissance == null ? null : Period.between(dateNaissance, LocalDate.now()).getYears();
    }

    /** Vrai si le patient a moins de {@link #AGE_LIMITE_ENFANT} ans. */
    public boolean isEnfant() {
        var age = getAge();
        return age != null && age < AGE_LIMITE_ENFANT;
    }

    public String getNomComplet() {
        return (nom == null ? "" : nom.toUpperCase()) + " " + (prenom == null ? "" : prenom);
    }

    /** Deux patients sont égaux s'ils ont le même identifiant (identité d'entité). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return id != null && Objects.equals(id, patient.id);
    }

    /** Cohérent avec equals : basé uniquement sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Patient autre) {
        return PAR_NOM_PRENOM.compare(this, autre);
    }
}
