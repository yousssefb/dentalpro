package ma.dentalpro.repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.StatutConsultation;
import ma.dentalpro.repository.api.ConsultationRepo;
import ma.dentalpro.repository.jdbc.JdbcUtil;
import ma.dentalpro.repository.jdbc.RowMappers;
import ma.dentalpro.shared.RepositoryException;

/**
 * Implémentation JDBC/MySQL du repository des consultations (table consultation).
 */
public class ConsultationRepoImpl implements ConsultationRepo {

    private static final String SELECT_CONSULTATION =
            "SELECT " + RowMappers.CONSULTATION_COLUMNS + " FROM consultation";

    private final Connection connection;

    public ConsultationRepoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Consultation> findAll() {
        return query(SELECT_CONSULTATION + " ORDER BY date_consultation DESC, id DESC",
                "Erreur pendant la récupération des consultations");
    }

    @Override
    public Consultation findById(Long identity) {
        if (identity == null) {
            return null;
        }
        List<Consultation> liste = query(SELECT_CONSULTATION + " WHERE id = ?",
                "Erreur pendant la recherche de la consultation " + identity, identity);
        if (liste.isEmpty()) {
            return null;
        }
        return liste.get(0);
    }

    @Override
    public List<Consultation> findByDossierId(Long dossierId) {
        if (dossierId == null) {
            return new ArrayList<>();
        }
        return query(SELECT_CONSULTATION + " WHERE dossier_id = ? ORDER BY date_consultation DESC",
                "Erreur pendant la recherche des consultations pour le dossier " + dossierId, dossierId);
    }

    @Override
    public List<Consultation> findByMedecinId(Long medecinId) {
        if (medecinId == null) {
            return new ArrayList<>();
        }
        return query(SELECT_CONSULTATION + " WHERE medecin_id = ? ORDER BY date_consultation DESC",
                "Erreur pendant la recherche des consultations du médecin " + medecinId, medecinId);
    }

    @Override
    public Consultation findByRdvId(Long rdvId) {
        if (rdvId == null) {
            return null;
        }
        List<Consultation> liste = query(SELECT_CONSULTATION + " WHERE rdv_id = ?",
                "Erreur pendant la recherche de la consultation pour le rendez-vous " + rdvId, rdvId);
        if (liste.isEmpty()) {
            return null;
        }
        return liste.get(0);
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        if (statut == null) {
            return new ArrayList<>();
        }
        return query(SELECT_CONSULTATION + " WHERE statut = ? ORDER BY date_consultation DESC",
                "Erreur pendant la recherche des consultations avec le statut " + statut, statut.name());
    }

    @Override
    public Consultation save(Consultation c) {
        if (c == null) {
            throw new IllegalArgumentException("La consultation ne peut pas être null.");
        }
        String sql = "INSERT INTO consultation (dossier_id, medecin_id, rdv_id, date_consultation, observation, statut) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            String statutTexte = "BROUILLON";
            if (c.getStatut() != null) {
                statutTexte = c.getStatut().name();
            }
            statement = JdbcUtil.prepareStatement(connection, sql, true,
                    c.getDossierId(), c.getMedecinId(), c.getRdvId(), c.getDateConsultation(),
                    c.getObservation(), statutTexte);
            int affectedRows = statement.executeUpdate();
            JdbcUtil.logAffectedRows("Création de la consultation", affectedRows);
            if (affectedRows == 0) {
                throw new RepositoryException("Aucune consultation n'a été insérée.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new RepositoryException("MySQL n'a retourné aucun identifiant de consultation.");
            }
            c.setId(generatedKeys.getLong(1));
            return c;
        } catch (SQLException e) {
            throw JdbcUtil.dataAccessException("Erreur pendant l'enregistrement de la consultation", e);
        } finally {
            JdbcUtil.closeResource(generatedKeys);
            JdbcUtil.closeResource(statement);
        }
    }

    @Override
    public boolean update(Consultation c) {
        if (c == null || c.getId() == null) {
            return false;
        }
        String sql = "UPDATE consultation SET dossier_id = ?, medecin_id = ?, rdv_id = ?, "
                   + "date_consultation = ?, observation = ?, statut = ? WHERE id = ?";
        String statutTexte = "BROUILLON";
        if (c.getStatut() != null) {
            statutTexte = c.getStatut().name();
        }
        return executeUpdate(sql, "Modification de la consultation",
                "Erreur pendant la modification de la consultation " + c.getId(),
                c.getDossierId(), c.getMedecinId(), c.getRdvId(), c.getDateConsultation(),
                c.getObservation(), statutTexte, c.getId()) > 0;
    }

    @Override
    public boolean delete(Consultation element) {
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
        String sql = "DELETE FROM consultation WHERE id = ?";
        return executeUpdate(sql, "Suppression de la consultation",
                "Erreur pendant la suppression de la consultation " + identity, identity) > 0;
    }

    // =====================================================================================
    // Méthodes utilitaires d'exécution SQL
    // =====================================================================================

    private List<Consultation> query(String sql, String errorMessage, Object... parameters) {
        List<Consultation> liste = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = JdbcUtil.prepareStatement(connection, sql, false, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                liste.add(RowMappers.toConsultation(resultSet));
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
