
package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ma.dentalpro.domain.Paiement;
import ma.dentalpro.domain.ModePaiement;
import ma.dentalpro.repository.api.PaiementRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.shared.RepositoryException;

public class PaiementRepoImpl implements PaiementRepo {

    private final Connection connection;

    private static final String SELECT_PAIEMENT =
            "SELECT id, facture_id, auteur_id, reference, " +
                    "date_paiement, montant, mode FROM paiement";

    public PaiementRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Paiement> findAll() {
        return query(
                SELECT_PAIEMENT + " ORDER BY date_paiement DESC",
                "Erreur pendant la lecture des paiements"
        );
    }

    @Override
    public Paiement findById(Long identity) {

        if (identity == null) {
            return null;
        }

        List<Paiement> paiements = query(
                SELECT_PAIEMENT + " WHERE id = ?",
                "Erreur pendant la recherche du paiement",
                identity
        );

        return paiements.isEmpty() ? null : paiements.get(0);
    }

    @Override
    public Paiement save(Paiement paiement) {

        if (paiement == null) {
            throw new IllegalArgumentException(
                    "Le paiement ne peut pas être null"
            );
        }

        String sql = "INSERT INTO paiement " +
                "(facture_id, auteur_id, reference, date_paiement, montant, mode) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            statement = JdbcUtil.prepareStatement(
                    connection,
                    sql,
                    true,
                    paiement.getFactureId(),
                    paiement.getAuteurId(),
                    paiement.getReference().toString(),
                    paiement.getDatePaiement(),
                    paiement.getMontant(),
                    paiement.getMode().name()
            );

            int affectedRows = statement.executeUpdate();

            JdbcUtil.logAffectedRows(
                    "Ajout du paiement", affectedRows
            );

            if (affectedRows == 0) {
                throw new RepositoryException(
                        "Aucun paiement n'a été ajouté"
                );
            }

            generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                paiement.setId(generatedKeys.getLong(1));
            } else {
                throw new RepositoryException(
                        "Aucun identifiant généré pour le paiement"
                );
            }

            return paiement;

        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(
                    "Erreur pendant l'enregistrement du paiement", e
            );
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Paiement paiement) {

        if (paiement == null || paiement.getId() == null) {
            return false;
        }

        String sql = "UPDATE paiement SET " +
                "facture_id = ?, auteur_id = ?, reference = ?, " +
                "date_paiement = ?, montant = ?, mode = ? " +
                "WHERE id = ?";

        return executeUpdate(
                sql,
                "Modification du paiement",
                "Erreur pendant la modification du paiement",
                paiement.getFactureId(),
                paiement.getAuteurId(),
                paiement.getReference().toString(),
                paiement.getDatePaiement(),
                paiement.getMontant(),
                paiement.getMode().name(),
                paiement.getId()
        ) > 0;
    }

    @Override
    public boolean delete(Paiement paiement) {

        if (paiement == null) {
            return false;
        }

        return deleteById(paiement.getId());
    }

    @Override
    public boolean deleteById(Long identity) {

        if (identity == null) {
            return false;
        }

        return executeUpdate(
                "DELETE FROM paiement WHERE id = ?",
                "Suppression du paiement",
                "Erreur pendant la suppression du paiement",
                identity
        ) > 0;
    }

    private List<Paiement> query(
            String sql,
            String errorMessage,
            Object... parameters
    ) {

        List<Paiement> paiements = new ArrayList<>();

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = JdbcUtil.prepareStatement(
                    connection, sql, false, parameters
            );

            resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Paiement paiement = Paiement.builder()
                        .id(resultSet.getLong("id"))
                        .factureId(resultSet.getLong("facture_id"))
                        .auteurId(resultSet.getLong("auteur_id"))
                        .reference(
                                UUID.fromString(
                                        resultSet.getString("reference")
                                )
                        )
                        .datePaiement(
                                resultSet.getObject(
                                        "date_paiement",
                                        java.time.LocalDateTime.class
                                )
                        )
                        .montant(resultSet.getBigDecimal("montant"))
                        .mode(
                                ModePaiement.valueOf(
                                        resultSet.getString("mode")
                                )
                        )
                        .build();

                paiements.add(paiement);
            }

            return paiements;

        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException(
                    errorMessage, e
            );
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
            throw JdbcUtil.dataAccessException(
                    errorMessage, e
            );
        } finally {
            JdbcUtil.closeResource(statement);
        }
    }
}
