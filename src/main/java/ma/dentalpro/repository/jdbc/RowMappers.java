package ma.dentalpro.repository.jdbc;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.dentalpro.domain.Antecedent;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.Intervention;
import ma.dentalpro.domain.NiveauRisque;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;
import ma.dentalpro.domain.StatutConsultation;

/**
 * Centralise les fonctions qui transforment une ligne SQL (ResultSet) en objet métier (Java Bean).
 *
 * <p>C'est le seul endroit où l'on lit les colonnes MySQL pour construire nos objets.</p>
 */
public final class RowMappers {

    private RowMappers() {}

    // =====================================================================================
    // Module Patient
    // =====================================================================================

    /** Colonnes lues pour un patient (table patient). */
    public static final String PATIENT_COLUMNS =
            "id, nom, prenom, date_naissance, sexe, cin, passeport, email, telephone, adresse, "
                    + "assurance, archive, created_at";

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

    // =====================================================================================
    // Module Clinique (M3) : Antécédents, Dossier-Antécédents, Consultations, Interventions
    // =====================================================================================

    /** Colonnes lues pour un antécédent (table antecedent). */
    public static final String ANTECEDENT_COLUMNS = "id, libelle, categorie, actif";

    public static Antecedent toAntecedent(ResultSet rs) throws SQLException {
        return Antecedent.builder()
                .id(rs.getLong("id"))
                .libelle(rs.getString("libelle"))
                .categorie(rs.getString("categorie"))
                .actif(rs.getBoolean("actif"))
                .build();
    }

    /** Colonnes lues pour un antécédent lié à un dossier médical (table dossier_antecedent). */
    public static final String DOSSIER_ANTECEDENT_COLUMNS =
            "dossier_id, antecedent_id, niveau_risque, commentaire, actif";

    public static DossierAntecedent toDossierAntecedent(ResultSet rs) throws SQLException {
        String risqueTexte = rs.getString("niveau_risque");
        NiveauRisque risque = null;
        if (risqueTexte != null) {
            risque = NiveauRisque.valueOf(risqueTexte);
        }
        return DossierAntecedent.builder()
                .dossierId(rs.getLong("dossier_id"))
                .antecedentId(rs.getLong("antecedent_id"))
                .niveauRisque(risque)
                .commentaire(rs.getString("commentaire"))
                .actif(rs.getBoolean("actif"))
                .build();
    }

    /** Colonnes lues pour une consultation (table consultation). */
    public static final String CONSULTATION_COLUMNS =
            "id, dossier_id, medecin_id, rdv_id, date_consultation, observation, statut";

    public static Consultation toConsultation(ResultSet rs) throws SQLException {
        String statutTexte = rs.getString("statut");
        StatutConsultation statut = null;
        if (statutTexte != null) {
            statut = StatutConsultation.valueOf(statutTexte);
        }

        // rdv_id est facultatif (peut être NULL dans la table).
        // En JDBC, getLong() renvoie 0 si la valeur est NULL : on teste wasNull() pour remettre null.
        Long rdvId = rs.getLong("rdv_id");
        if (rs.wasNull()) {
            rdvId = null;
        }

        return Consultation.builder()
                .id(rs.getLong("id"))
                .dossierId(rs.getLong("dossier_id"))
                .medecinId(rs.getLong("medecin_id"))
                .rdvId(rdvId)
                .dateConsultation(rs.getObject("date_consultation", LocalDateTime.class))
                .observation(rs.getString("observation"))
                .statut(statut)
                .build();
    }

    /** Colonnes lues pour une intervention (table intervention). */
    public static final String INTERVENTION_COLUMNS =
            "id, consultation_id, acte_id, libelle_acte_snapshot, dent, quantite, prix_unitaire";

    public static Intervention toIntervention(ResultSet rs) throws SQLException {
        return Intervention.builder()
                .id(rs.getLong("id"))
                .consultationId(rs.getLong("consultation_id"))
                .acteId(rs.getLong("acte_id"))
                .libelleActeSnapshot(rs.getString("libelle_acte_snapshot"))
                .dent(rs.getString("dent"))
                .quantite(rs.getInt("quantite"))
                .prixUnitaire(rs.getBigDecimal("prix_unitaire"))
                .build();
    }
}
