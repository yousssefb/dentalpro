
package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ma.dentalpro.domain.Facture;
import ma.dentalpro.domain.StatutFacture;
import ma.dentalpro.repository.api.FactureRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.shared.RepositoryException;

public class FactureRepoImpl implements FactureRepo {

    private final Connection connection;

    private static final String SELECT_FACTURE =
            "SELECT id, consultation_id, numero, date_facture, " +
                    "total, statut, motif_annulation FROM facture";

    public FactureRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Facture> findAll() {
        return query(
                SELECT_FACTURE + " ORDER BY id",
                "Erreur pendant la lecture des factures"
        );
    }

    @Override
    public Facture findById(Long identity) {

        if (identity == null) {
            return null;
        }

        List<Facture> factures = query(
                SELECT_FACTURE + " WHERE id = ?",
                "Erreur pendant la recherche de la facture",
                identity
        );

        return factures.isEmpty() ? null : factures.get(0);
    }

    @Override
    public Facture save(Facture facture) {

        if (facture == null) {
            throw new IllegalArgumentException(
                    "La facture ne peut pas être null"
            );
        }

        String sql = "INSERT INTO facture " +
                "(consultation_id, numero, date_facture, total, statut, motif_annulation) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(
                    connection,
                    sql,
                    true,
                    facture.getConsultationId(),
                    facture.getNumero(),
                    facture.getDateFacture(),
                    facture.getTotal(),
                    facture.getStatut().name(),
                    facture.getMotifAnnulation()
            );

            int affectedRows = statement.executeUpdate();

            JdbcUtil.logAffectedRows("Ajout de la facture", affectedRows);

            if (affectedRows == 0) {
                throw new RepositoryException(
                        "Aucune facture n'a été ajoutée"
                );
            }

            generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                facture.setId(generatedKeys.getLong(1));
            } else {
                throw new RepositoryException(
                        "Aucun identifiant généré pour la facture"
                );
            }

            return facture;

        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(
                    "Erreur pendant l'enregistrement de la facture", e
            );
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Facture facture) {

        if (facture == null || facture.getId() == null) {
            return false;
        }

        String sql = "UPDATE facture SET " +
                "consultation_id = ?, numero = ?, date_facture = ?, " +
                "total = ?, statut = ?, motif_annulation = ? " +
                "WHERE id = ?";

        return executeUpdate(
                sql,
                "Modification de la facture",
                "Erreur pendant la modification de la facture",
                facture.getConsultationId(),
                facture.getNumero(),
                facture.getDateFacture(),
                facture.getTotal(),
                facture.getStatut().name(),
                facture.getMotifAnnulation(),
                facture.getId()
        ) > 0;
    }

    @Override
    public boolean delete(Facture facture) {

        if (facture == null) {
            return false;
        }

        return deleteById(facture.getId());
    }

    @Override
    public boolean deleteById(Long identity) {

        if (identity == null) {
            return false;
        }

        return executeUpdate(
                "DELETE FROM facture WHERE id = ?",
                "Suppression de la facture",
                "Erreur pendant la suppression de la facture",
                identity
        ) > 0;
    }

    private List<Facture> query(
            String sql,
            String errorMessage,
            Object... parameters
    ) {

        List<Facture> factures = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = JdbcUtil.prepareStatement(
                    connection, sql, false, parameters
            );

            resultSet = statement.executeQuery();

            while (resultSet.next()) {

                String statut = resultSet.getString("statut");

                Facture facture = Facture.builder()
                        .id(resultSet.getLong("id"))
                        .consultationId(
                                resultSet.getLong("consultation_id")
                        )
                        .numero(resultSet.getString("numero"))
                        .dateFacture(
                                resultSet.getObject(
                                        "date_facture",
                                        java.time.LocalDateTime.class
                                )
                        )
                        .total(resultSet.getBigDecimal("total"))
                        .statut(StatutFacture.valueOf(statut))
                        .motifAnnulation(
                                resultSet.getString("motif_annulation")
                        )
                        .build();

                factures.add(facture);
            }

            return factures;

        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(errorMessage, e);
        } finally {
            JdbcUtil.closeResource(resultSet);
            JdbcUtil.closeResource(statement);
        }
    }

    private int executeUpdate(
            String sql,
            String operation,
            String errorMessage,
            Object... parameters
    ) {

        PreparedStatement statement = null;

        try {
            statement = JdbcUtil.prepareStatement(
                    connection, sql, false, parameters
            );

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
