package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.repository.api.PatientRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository des patients (table patient).
 */
public class PatientRepoImpl implements PatientRepo {

    private static final String SELECT_PATIENT = "SELECT " + RowMappers.PATIENT_COLUMNS + " FROM patient";

    private final Connection connection;

    public PatientRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Patient> findAll() {
        return query(SELECT_PATIENT + " ORDER BY id", "Erreur pendant la lecture des patients");
    }

    @Override
    public Patient findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Patient> resultat = query(SELECT_PATIENT + " WHERE id = ?",
                "Erreur pendant la recherche du patient " + identity, identity);
        if (resultat.isEmpty()) {
            return null; // aucun patient avec cet identifiant
        }
        return resultat.get(0);
    }

    /** Recherche par nom ou prénom (contient, insensible à la casse), patients archivés compris. */
    @Override
    public List<Patient> findByNomLike(String mc) {
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim();
        }
        // Les % autour du mot-clé veulent dire « contient » en SQL.
        String motif = "%" + motCle + "%";
        return query(SELECT_PATIENT + " WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom, id",
                "Erreur pendant la recherche des patients par nom", motif, motif);
    }

    @Override
    public Patient save(Patient p) {
        if (p == null) {
            throw new IllegalArgumentException("Le patient à enregistrer ne peut pas être null");
        }
        String sql = "INSERT INTO patient (nom, prenom, date_naissance, sexe, cin, passeport, email, "
                + "telephone, adresse, assurance, archive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    p.getNom(), p.getPrenom(), p.getDateNaissance(), sexe(p), p.getCin(), p.getPasseport(),
                    p.getEmail(), p.getTelephone(), p.getAdresse(), p.getAssurance(), p.isArchive());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout du patient", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucun patient n'a été ajouté");
            }

            // MySQL renvoie l'identifiant (AUTO_INCREMENT) qu'il vient de créer.
            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant patient");
            }
            p.setId(generatedKeys.getLong(1));
            return p;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement du patient", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Patient p) {
        if (p == null || p.getId() == null) {
            return false;
        }
        String sql = "UPDATE patient SET nom = ?, prenom = ?, date_naissance = ?, sexe = ?, cin = ?, "
                + "passeport = ?, email = ?, telephone = ?, adresse = ?, assurance = ?, archive = ? "
                + "WHERE id = ?";
        return executeUpdate(sql, "Modification du patient",
                "Erreur pendant la modification du patient " + p.getId(),
                p.getNom(), p.getPrenom(), p.getDateNaissance(), sexe(p), p.getCin(), p.getPasseport(),
                p.getEmail(), p.getTelephone(), p.getAdresse(), p.getAssurance(), p.isArchive(), p.getId()) > 0;
    }

    @Override
    public boolean delete(Patient element) {
        if (element == null) {
            return false;
        }
        return deleteById(element.getId());
    }

    /**
     * Suppression physique : refusée par MySQL dès que le patient possède un dossier (clé étrangère).
     * Les écrans utiliseront l'archivage (RG-04) à la place.
     */
    @Override
    public boolean deleteById(Long identity) {
        if (identity == null) {
            return false;
        }
        return executeUpdate("DELETE FROM patient WHERE id = ?", "Suppression du patient",
                "Erreur pendant la suppression du patient " + identity, identity) > 0;
    }

    // ———————————————————————————————————————————————————————————————————————————————————————

    /** Convertit l'enum Sexe en texte pour la base ("HOMME" ou "FEMME"), ou null s'il est absent. */
    private static String sexe(Patient p) {
        if (p.getSexe() == null) {
            return null;
        }
        return p.getSexe().name();
    }

    /** Exécute un SELECT et transforme chaque ligne du résultat en objet Patient. */
    private List<Patient> query(String sql, String errorMessage, Object... parameters) {
        List<Patient> patients = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                patients.add(RowMappers.toPatient(resultSet));
            }
            return patients;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(errorMessage, e);
        } finally {
            JdbcUtil.closeResource(resultSet);
            JdbcUtil.closeResource(statement);
        }
    }

    /** Exécute un UPDATE ou un DELETE et renvoie le nombre de lignes modifiées. */
    private int executeUpdate(String sql, String operation, String errorMessage, Object... parameters) {
        PreparedStatement statement = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows(operation, affectedRows);
            return affectedRows;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(errorMessage, e);
        } finally {
            JdbcUtil.closeResource(statement);
        }
    }
}
