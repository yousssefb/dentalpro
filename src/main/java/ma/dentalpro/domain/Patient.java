package ma.dentalpro.domain;

import java.util.Comparator;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Java Bean
@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Patient implements Comparable<Patient> {

    /** Tri par nom, insensible à la casse ; les noms absents passent en dernier. */
    private static final Comparator<Patient> BY_NOM = Comparator.comparing(
            Patient::getNom, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    private Long idPatient;
    private String nom;
    private Integer age;

    /** Deux patients sont égaux s'ils ont le même identifiant (identité d'entité). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return idPatient != null && Objects.equals(idPatient, patient.idPatient);
    }

    /** Cohérent avec equals : basé uniquement sur l'identifiant. */
    @Override
    public int hashCode() {
        return Objects.hashCode(idPatient);
    }

    @Override
    public int compareTo(Patient autre) {
        return BY_NOM.compare(this, autre);
    }
}
