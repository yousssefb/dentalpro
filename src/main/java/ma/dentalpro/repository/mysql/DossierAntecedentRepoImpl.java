package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.DossierAntecedentId;
import ma.dentalpro.repository.api.DossierAntecedentRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository pour les antécédents associés aux dossiers médicaux.
 */
public class DossierAntecedentRepoImpl implements DossierAntecedentRepo {

    private static final String SELECT_DOSSIER_ANTECEDENT =
            "SELECT " + RowMappers.DOSSIER_ANTECEDENT_COLUMNS + " FROM dossier_antecedent";

    private final Connection connection;

    public DossierAntecedentRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<DossierAntecedent> findAll() {
        return query(SELECT_DOSSIER_ANTECEDENT + " ORDER BY dossier_id, antecedent_id",
                "Erreur pendant la récupération des antécédents de dossiers");
    }

    @Override
    public DossierAntecedent findById(DossierAntecedentId identity) {
        if (identity == null) {
            return null;
        }
        return findByDossierIdAndAntecedentId(identity.getDossierId(), identity.getAntecedentId());
    }

    @Override
    public List<DossierAntecedent> findByDossierId(Long dossierId) {
        if (dossierId == null) {
            return new ArrayList<>();
        }
        return query(SELECT_DOSSIER_ANTECEDENT + " WHERE dossier_id = ? ORDER BY antecedent_id",
                "Erreur pendant la recherche des antécédents pour le dossier " + dossierId, dossierId);
    }

    @Override
    public List<DossierAntecedent> findByAntecedentId(Long antecedentId) {
        if (antecedentId == null) {
            return new ArrayList<>();
        }
        return query(SELECT_DOSSIER_ANTECEDENT + " WHERE antecedent_id = ? ORDER BY dossier_id",
                "Erreur pendant la recherche des dossiers ayant l'antécédent " + antecedentId, antecedentId);
    }

    @Override
    public DossierAntecedent findByDossierIdAndAntecedentId(Long dossierId, Long antecedentId) {
        if (dossierId == null || antecedentId == null) {
            return null;
        }
        List<DossierAntecedent> liste = query(
                SELECT_DOSSIER_ANTECEDENT + " WHERE dossier_id = ? AND antecedent_id = ?",
                "Erreur pendant la recherche de l'antécédent " + antecedentId + " pour le dossier " + dossierId,
                dossierId, antecedentId);
        if (liste.isEmpty()) {
            return null;
        }
        return liste.get(0);
    }

    @Override
    public DossierAntecedent save(DossierAntecedent da) {
        if (da == null || da.getDossierId() == null || da.getAntecedentId() == null) {
            throw new IllegalArgumentException("Le dossierId et l'antecedentId sont obligatoires.");
        }
        String sql = "INSERT INTO dossier_antecedent (dossier_id, antecedent_id, niveau_risque, commentaire, actif) "
                   + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            String risque = null;
            if (da.getNiveauRisque() != null) {
                risque = da.getNiveauRisque().name();
            }
            statement = JdbcUtil.prepareStatement(connection, sql, false,
                    da.getDossierId(), da.getAntecedentId(), risque, da.getCommentaire(), da.isActif());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout de l'antécédent au dossier", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucune ligne n'a été insérée dans dossier_antecedent.");
            }
            return da;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'ajout de l'antécédent au dossier", e);
        } finally {
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(DossierAntecedent da) {
        if (da == null || da.getDossierId() == null || da.getAntecedentId() == null) {
            return false;
        }
        String sql = "UPDATE dossier_antecedent SET niveau_risque = ?, commentaire = ?, actif = ? "
                   + "WHERE dossier_id = ? AND antecedent_id = ?";
        String risque = null;
        if (da.getNiveauRisque() != null) {
            risque = da.getNiveauRisque().name();
        }
        return executeUpdate(sql, "Modification de l'antécédent du dossier",
                "Erreur pendant la modification de l'antécédent du dossier",
                risque, da.getCommentaire(), da.isActif(), da.getDossierId(), da.getAntecedentId()) > 0;
    }

    @Override
    public boolean delete(DossierAntecedent element) {
        if (element == null) {
            return false;
        }
        return deleteByDossierIdAndAntecedentId(element.getDossierId(), element.getAntecedentId());
    }

    @Override
    public boolean deleteById(DossierAntecedentId identity) {
        if (identity == null) {
            return false;
        }
        return deleteByDossierIdAndAntecedentId(identity.getDossierId(), identity.getAntecedentId());
    }

    @Override
    public boolean deleteByDossierIdAndAntecedentId(Long dossierId, Long antecedentId) {
        if (dossierId == null || antecedentId == null) {
            return false;
        }
        String sql = "DELETE FROM dossier_antecedent WHERE dossier_id = ? AND antecedent_id = ?";
        return executeUpdate(sql, "Suppression de l'antécédent du dossier",
                "Erreur pendant la suppression de l'antécédent du dossier", dossierId, antecedentId) > 0;
    }

    // =====================================================================================
    // Méthodes utilitaires d'exécution SQL
    // =====================================================================================

    private List<DossierAntecedent> query(String sql, String errorMessage, Object... parameters) {
        List<DossierAntecedent> liste = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                liste.add(RowMappers.toDossierAntecedent(resultSet));
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
