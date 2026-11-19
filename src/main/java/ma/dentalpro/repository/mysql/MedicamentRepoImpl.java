package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Medicament;
import ma.dentalpro.repository.api.MedicamentRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository des médicaments (table medicament).
 */
public class MedicamentRepoImpl implements MedicamentRepo {

    private static final String SELECT_MEDICAMENT =
            "SELECT " + RowMappers.MEDICAMENT_COLUMNS + " FROM medicament";

    private final Connection connection;

    public MedicamentRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Medicament> findAll() {
        return query(SELECT_MEDICAMENT + " ORDER BY id", "Erreur pendant la lecture des médicaments");
    }

    @Override
    public Medicament findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Medicament> resultat = query(SELECT_MEDICAMENT + " WHERE id = ?",
                "Erreur pendant la recherche du médicament " + identity, identity);
        if (resultat.isEmpty()) {
            return null; // aucun médicament avec cet identifiant
        }
        return resultat.get(0);
    }

    /** Recherche par nom (contient, insensible à la casse), médicaments inactifs compris. */
    @Override
    public List<Medicament> findByNomLike(String mc) {
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim();
        }
        String motif = "%" + motCle + "%";
        return query(SELECT_MEDICAMENT + " WHERE nom LIKE ? ORDER BY nom, id",
                "Erreur pendant la recherche des médicaments par nom", motif);
    }

    @Override
    public Medicament save(Medicament m) {
        if (m == null) {
            throw new IllegalArgumentException("Le médicament à enregistrer ne peut pas être null");
        }
        String sql = "INSERT INTO medicament (nom, forme, actif) VALUES (?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    m.getNom(), m.getForme(), m.isActif());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout du médicament", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucun médicament n'a été ajouté");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant de médicament");
            }
            m.setId(generatedKeys.getLong(1));
            return m;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement du médicament", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Medicament m) {
        if (m == null || m.getId() == null) {
            return false;
        }
        String sql = "UPDATE medicament SET nom = ?, forme = ?, actif = ? WHERE id = ?";
        return executeUpdate(sql, "Modification du médicament",
                "Erreur pendant la modification du médicament " + m.getId(),
                m.getNom(), m.getForme(), m.isActif(), m.getId()) > 0;
    }

    @Override
    public boolean delete(Medicament element) {
        if (element == null) {
            return false;
        }
        return deleteById(element.getId());
    }

    /**
     * Suppression physique : refusée par MySQL si le médicament figure déjà dans une
     * prescription (clé étrangère). Dans ce cas, on le désactive avec update (actif = false).
     */
    @Override
    public boolean deleteById(Long identity) {
        if (identity == null) {
            return false;
        }
        return executeUpdate("DELETE FROM medicament WHERE id = ?", "Suppression du médicament",
                "Erreur pendant la suppression du médicament " + identity, identity) > 0;
    }

    // ———————————————————————————————————————————————————————————————————————————————————————

    /** Exécute un SELECT et transforme chaque ligne du résultat en objet Medicament. */
    private List<Medicament> query(String sql, String errorMessage, Object... parameters) {
        List<Medicament> medicaments = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                medicaments.add(RowMappers.toMedicament(resultSet));
            }
            return medicaments;
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
