package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Intervention;
import ma.dentalpro.repository.api.InterventionRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository des interventions (table intervention).
 */
public class InterventionRepoImpl implements InterventionRepo {

    private static final String SELECT_INTERVENTION =
            "SELECT " + RowMappers.INTERVENTION_COLUMNS + " FROM intervention";

    private final Connection connection;

    public InterventionRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Intervention> findAll() {
        return query(SELECT_INTERVENTION + " ORDER BY id",
                "Erreur pendant la récupération des interventions");
    }

    @Override
    public Intervention findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Intervention> liste = query(SELECT_INTERVENTION + " WHERE id = ?",
                "Erreur pendant la recherche de l'intervention " + identity, identity);
        if (liste.isEmpty()) {
            return null;
        }
        return liste.get(0);
    }

    @Override
    public List<Intervention> findByConsultationId(Long consultationId) {
        if (consultationId == null) {
            return new ArrayList<>();
        }
        return query(SELECT_INTERVENTION + " WHERE consultation_id = ? ORDER BY id",
                "Erreur pendant la recherche des interventions de la consultation " + consultationId, consultationId);
    }

    @Override
    public List<Intervention> findByActeId(Long acteId) {
        if (acteId == null) {
            return new ArrayList<>();
        }
        return query(SELECT_INTERVENTION + " WHERE acte_id = ? ORDER BY id",
                "Erreur pendant la recherche des interventions pour l'acte " + acteId, acteId);
    }

    @Override
    public Intervention save(Intervention i) {
        if (i == null) {
            throw new IllegalArgumentException("L'intervention ne peut pas être null.");
        }
        String sql = "INSERT INTO intervention (consultation_id, acte_id, libelle_acte_snapshot, dent, quantite, prix_unitaire) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            int qte = 1;
            if (i.getQuantite() != null) {
                qte = i.getQuantite();
            }
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    i.getConsultationId(), i.getActeId(), i.getLibelleActeSnapshot(),
                    i.getDent(), qte, i.getPrixUnitaire());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout de l'intervention", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucune intervention n'a été insérée.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant pour l'intervention.");
            }
            i.setId(generatedKeys.getLong(1));
            return i;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement de l'intervention", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Intervention i) {
        if (i == null || i.getId() == null) {
            return false;
        }
        String sql = "UPDATE intervention SET consultation_id = ?, acte_id = ?, libelle_acte_snapshot = ?, "
                   + "dent = ?, quantite = ?, prix_unitaire = ? WHERE id = ?";
        int qte = 1;
        if (i.getQuantite() != null) {
            qte = i.getQuantite();
        }
        return executeUpdate(sql, "Modification de l'intervention",
                "Erreur pendant la modification de l'intervention " + i.getId(),
                i.getConsultationId(), i.getActeId(), i.getLibelleActeSnapshot(),
                i.getDent(), qte, i.getPrixUnitaire(), i.getId()) > 0;
    }

    @Override
    public boolean delete(Intervention element) {
        if (element == null) {
            return false;
        }
        return deleteById(element.getId());
    }

    @Override
    public boolean deleteById(Long identity) {
        if (identity == null) {
            return false;
        }
        String sql = "DELETE FROM intervention WHERE id = ?";
        return executeUpdate(sql, "Suppression de l'intervention",
                "Erreur pendant la suppression de l'intervention " + identity, identity) > 0;
    }

    // =====================================================================================
    // Méthodes utilitaires d'exécution SQL
    // =====================================================================================

    private List<Intervention> query(String sql, String errorMessage, Object... parameters) {
        List<Intervention> liste = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                liste.add(RowMappers.toIntervention(resultSet));
            }
            return liste;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(errorMessage, e);
        } finally {
            JdbcUtil.closeResource(resultSet);
            JdbcUtil.closeResource(statement);
        }
    }

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
