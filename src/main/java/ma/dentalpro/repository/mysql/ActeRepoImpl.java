package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.repository.api.ActeRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository des actes (table acte).
 */
public class ActeRepoImpl implements ActeRepo {

    private static final String SELECT_ACTE = "SELECT " + RowMappers.ACTE_COLUMNS + " FROM acte";

    private final Connection connection;

    public ActeRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Acte> findAll() {
        return query(SELECT_ACTE + " ORDER BY id", "Erreur pendant la lecture des actes");
    }

    @Override
    public Acte findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Acte> resultat = query(SELECT_ACTE + " WHERE id = ?",
                "Erreur pendant la recherche de l'acte " + identity, identity);
        if (resultat.isEmpty()) {
            return null; // aucun acte avec cet identifiant
        }
        return resultat.get(0);
    }

    /** Recherche par libellé (contient, insensible à la casse), actes inactifs compris. */
    @Override
    public List<Acte> findByLibelleLike(String mc) {
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim();
        }
        // Les % autour du mot-clé veulent dire « contient » en SQL.
        String motif = "%" + motCle + "%";
        return query(SELECT_ACTE + " WHERE libelle LIKE ? ORDER BY libelle, id",
                "Erreur pendant la recherche des actes par libellé", motif);
    }

    @Override
    public Acte save(Acte a) {
        if (a == null) {
            throw new IllegalArgumentException("L'acte à enregistrer ne peut pas être null");
        }
        String sql = "INSERT INTO acte (libelle, categorie, prix_base, actif) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    a.getLibelle(), a.getCategorie(), a.getPrixBase(), a.isActif());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout de l'acte", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucun acte n'a été ajouté");
            }

            // MySQL renvoie l'identifiant (AUTO_INCREMENT) qu'il vient de créer.
            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant d'acte");
            }
            a.setId(generatedKeys.getLong(1));
            return a;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement de l'acte", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Acte a) {
        if (a == null || a.getId() == null) {
            return false;
        }
        String sql = "UPDATE acte SET libelle = ?, categorie = ?, prix_base = ?, actif = ? WHERE id = ?";
        return executeUpdate(sql, "Modification de l'acte",
                "Erreur pendant la modification de l'acte " + a.getId(),
                a.getLibelle(), a.getCategorie(), a.getPrixBase(), a.isActif(), a.getId()) > 0;
    }

    @Override
    public boolean delete(Acte element) {
        if (element == null) {
            return false;
        }
        return deleteById(element.getId());
    }

    /**
     * Suppression physique : refusée par MySQL si l'acte a déjà été utilisé dans une
     * intervention (clé étrangère). Dans ce cas, on le désactive avec update (actif = false).
     */
    @Override
    public boolean deleteById(Long identity) {
        if (identity == null) {
            return false;
        }
        return executeUpdate("DELETE FROM acte WHERE id = ?", "Suppression de l'acte",
                "Erreur pendant la suppression de l'acte " + identity, identity) > 0;
    }

    // ———————————————————————————————————————————————————————————————————————————————————————

    /** Exécute un SELECT et transforme chaque ligne du résultat en objet Acte. */
    private List<Acte> query(String sql, String errorMessage, Object... parameters) {
        List<Acte> actes = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                actes.add(RowMappers.toActe(resultSet));
            }
            return actes;
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
