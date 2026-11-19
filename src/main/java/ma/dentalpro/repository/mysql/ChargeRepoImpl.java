package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.repository.api.ChargeRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository des charges (table charge).
 *
 * <p>C'est le seul endroit du module Charge qui contient du SQL.</p>
 */
public class ChargeRepoImpl implements ChargeRepo {

    private static final String SELECT_CHARGE = "SELECT " + RowMappers.CHARGE_COLUMNS + " FROM charge";

    private final Connection connection;

    public ChargeRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Charge> findAll() {
        return query(SELECT_CHARGE + " ORDER BY date_charge DESC, id DESC",
                "Erreur pendant la lecture des charges");
    }

    @Override
    public Charge findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Charge> resultat = query(SELECT_CHARGE + " WHERE id = ?",
                "Erreur pendant la recherche de la charge " + identity, identity);
        if (resultat.isEmpty()) {
            return null;
        }
        return resultat.get(0);
    }

    @Override
    public List<Charge> findByTitreLike(String mc) {
        String motCle = "";
        if (mc != null) {
            motCle = mc.trim();
        }
        // Les % autour du mot-clé veulent dire « contient » en SQL.
        String motif = "%" + motCle + "%";
        return query(SELECT_CHARGE + " WHERE titre LIKE ? ORDER BY date_charge DESC, id DESC",
                "Erreur pendant la recherche des charges par titre", motif);
    }

    @Override
    public Charge save(Charge c) {
        if (c == null) {
            throw new IllegalArgumentException("La charge à enregistrer ne peut pas être null");
        }
        String sql = "INSERT INTO charge (auteur_id, titre, description, montant, date_charge) "
                + "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    c.getAuteurId(), c.getTitre(), c.getDescription(), c.getMontant(), c.getDateCharge());
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Ajout de la charge", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucune charge n'a été ajoutée");
            }

            // MySQL renvoie l'identifiant (AUTO_INCREMENT) qu'il vient de créer.
            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant charge");
            }
            c.setId(generatedKeys.getLong(1));
            return c;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement de la charge", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Charge c) {
        if (c == null || c.getId() == null) {
            return false;
        }
        String sql = "UPDATE charge SET auteur_id = ?, titre = ?, description = ?, montant = ?, "
                + "date_charge = ? WHERE id = ?";
        return executeUpdate(sql, "Modification de la charge",
                "Erreur pendant la modification de la charge " + c.getId(),
                c.getAuteurId(), c.getTitre(), c.getDescription(), c.getMontant(),
                c.getDateCharge(), c.getId()) > 0;
    }

    @Override
    public boolean delete(Charge element) {
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
        return executeUpdate("DELETE FROM charge WHERE id = ?", "Suppression de la charge",
                "Erreur pendant la suppression de la charge " + identity, identity) > 0;
    }

    /** Exécute un SELECT et transforme chaque ligne en objet Charge. */
    private List<Charge> query(String sql, String errorMessage, Object... parameters) {
        List<Charge> charges = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                charges.add(RowMappers.toCharge(resultSet));
            }
            return charges;
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
