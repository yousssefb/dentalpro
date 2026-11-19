package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Antecedent;
import ma.dentalpro.repository.api.AntecedentRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository pour les antécédents médicaux (table antecedent).
 */
public class AntecedentRepoImpl implements AntecedentRepo {

    private static final String SELECT_ANTECEDENT =
            "SELECT " + RowMappers.ANTECEDENT_COLUMNS + " FROM antecedent";

    private final Connection connection;

    public AntecedentRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Antecedent> findAll() {
        return query(SELECT_ANTECEDENT + " ORDER BY id",
                "Erreur pendant la récupération de tous les antécédents");
    }

    @Override
    public Antecedent findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Antecedent> resultat = query(SELECT_ANTECEDENT + " WHERE id = ?",
                "Erreur pendant la recherche de l'antécédent " + identity, identity);
        if (resultat.isEmpty()) {
            return null;
        }
        return resultat.get(0);
    }

    @Override
    public List<Antecedent> findByLibelleLike(String motCle) {
        String recherche = "";
        if (motCle != null) {
            recherche = motCle.trim();
        }
        String motif = "%" + recherche + "%";
        return query(SELECT_ANTECEDENT + " WHERE LOWER(libelle) LIKE LOWER(?) ORDER BY libelle, id",
                "Erreur pendant la recherche d'antécédents par libellé", motif);
    }

    @Override
    public List<Antecedent> findByCategorie(String categorie) {
        if (categorie == null) {
            return new ArrayList<>();
        }
        return query(SELECT_ANTECEDENT + " WHERE categorie = ? ORDER BY libelle, id",
                "Erreur pendant la recherche d'antécédents par catégorie", categorie);
    }

    @Override
    public Antecedent save(Antecedent a) {
        if (a == null) {
            throw new IllegalArgumentException("L'antécédent à enregistrer ne peut pas être null.");
        }
        String sql = "INSERT INTO antecedent (libelle, categorie, actif) VALUES (?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    a.getLibelle(), a.getCategorie(), a.isActif());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout de l'antécédent", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucun antécédent n'a été inséré.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant pour l'antécédent.");
            }
            a.setId(generatedKeys.getLong(1));
            return a;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement de l'antécédent", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Antecedent a) {
        if (a == null || a.getId() == null) {
            return false;
        }
        String sql = "UPDATE antecedent SET libelle = ?, categorie = ?, actif = ? WHERE id = ?";
        return executeUpdate(sql, "Modification de l'antécédent",
                "Erreur pendant la modification de l'antécédent " + a.getId(),
                a.getLibelle(), a.getCategorie(), a.isActif(), a.getId()) > 0;
    }

    @Override
    public boolean delete(Antecedent element) {
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
        String sql = "DELETE FROM antecedent WHERE id = ?";
        return executeUpdate(sql, "Suppression de l'antécédent",
                "Erreur pendant la suppression de l'antécédent " + identity, identity) > 0;
    }

    // =====================================================================================
    // Méthodes utilitaires d'exécution SQL
    // =====================================================================================

    private List<Antecedent> query(String sql, String errorMessage, Object... parameters) {
        List<Antecedent> liste = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                liste.add(RowMappers.toAntecedent(resultSet));
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
