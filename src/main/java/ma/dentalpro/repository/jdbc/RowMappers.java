package ma.dentalpro.repository.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.dentalpro.domain.Charge;
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

    /** Colonnes lues par {@link #toCharge(ResultSet)}. */
    public static final String CHARGE_COLUMNS =
            "id, auteur_id, titre, description, montant, date_charge";

    public static Patient toPatient(ResultSet rs) throws SQLException {
        String sexe = rs.getString("sexe");
        Sexe sexePatient = null;
        if (sexe != null) {
            sexePatient = Sexe.valueOf(sexe); // transforme "HOMME" en Sexe.HOMME
        }
        return Patient.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .dateNaissance(rs.getObject("date_naissance", LocalDate.class))
                .sexe(sexePatient)
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

    /** Transforme une ligne de la table charge en objet Charge. */
    public static Charge toCharge(ResultSet rs) throws SQLException {
        return Charge.builder()
                .id(rs.getLong("id"))
                .auteurId(rs.getLong("auteur_id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(rs.getObject("montant", BigDecimal.class))
                .dateCharge(rs.getObject("date_charge", LocalDateTime.class))
                .build();
    }
}
