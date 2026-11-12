package ma.dentalpro.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;

/**
 * Centralise les fonctions qui transforment une ligne SQL en objet métier (mapping explicite).
 * Les mappings des autres entités (Consultation, Rdv, Facture…) seront ajoutés ici.
 */
public final class RowMappers {

    private RowMappers() {}

    /** Colonnes lues par {@link #toPatient(ResultSet)}, dans l'ordre de la table. */
    public static final String PATIENT_COLUMNS =
            "id, nom, prenom, date_naissance, sexe, cin, passeport, email, telephone, adresse, "
                    + "assurance, archive, created_at";

    public static Patient toPatient(ResultSet rs) throws SQLException {
        var sexe = rs.getString("sexe");
        return Patient.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .dateNaissance(rs.getObject("date_naissance", LocalDate.class))
                .sexe(sexe == null ? null : Sexe.valueOf(sexe))
                .cin(rs.getString("cin"))
                .passeport(rs.getString("passeport"))
                .email(rs.getString("email"))
                .telephone(rs.getString("telephone"))
                .adresse(rs.getString("adresse"))
                .assurance(rs.getString("assurance"))
                .archive(rs.getBoolean("archive"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .build();
    }
}
