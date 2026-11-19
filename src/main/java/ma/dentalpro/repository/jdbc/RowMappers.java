package ma.dentalpro.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.domain.Medicament;
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

    /** Colonnes lues par {@link #toActe(ResultSet)}. */
    public static final String ACTE_COLUMNS = "id, libelle, categorie, prix_base, actif";

    /** Colonnes lues par {@link #toMedicament(ResultSet)}. */
    public static final String MEDICAMENT_COLUMNS = "id, nom, forme, actif";

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

    /** Transforme une ligne de la table acte en objet Acte. */
    public static Acte toActe(ResultSet rs) throws SQLException {
        return Acte.builder()
                .id(rs.getLong("id"))
                .libelle(rs.getString("libelle"))
                .categorie(rs.getString("categorie"))
                .prixBase(rs.getBigDecimal("prix_base")) // DECIMAL(12,2) -> BigDecimal
                .actif(rs.getBoolean("actif"))
                .build();
    }

    /** Transforme une ligne de la table medicament en objet Medicament. */
    public static Medicament toMedicament(ResultSet rs) throws SQLException {
        return Medicament.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .forme(rs.getString("forme"))
                .actif(rs.getBoolean("actif"))
                .build();
    }
}
