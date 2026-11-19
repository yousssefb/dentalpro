package ma.dentalpro.repository.mysql;

import ma.dentalpro.config.JdbcConnectionFactory;
import ma.dentalpro.domain.Rdv;
import ma.dentalpro.domain.StatutRdv;
import ma.dentalpro.repository.api.RdvRepo;
import ma.dentalpro.shared.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class RdvRepoMySQL implements RdvRepo {
    private Properties properties;
    public RdvRepoMySQL(Properties properties) {
        this.properties = properties;
    }
    private Rdv convertirEnRdv(ResultSet rs) throws SQLException {
        Rdv rdv = new Rdv();
        rdv.setId(rs.getLong("id"));
        rdv.setDossierId(rs.getLong("dossier_id"));
        rdv.setMedecinId(rs.getLong("medecin_id"));
        rdv.setDebut(rs.getTimestamp("debut").toLocalDateTime());
        rdv.setFin(rs.getTimestamp("fin").toLocalDateTime());
        rdv.setMotif(rs.getString("motif"));
        rdv.setStatut(StatutRdv.valueOf(rs.getString("statut")));
        Timestamp arrivee = rs.getTimestamp("arrivee_at");
        if (arrivee != null) {
            rdv.setArriveeAt(arrivee.toLocalDateTime());
        }

        return rdv;
    }
    @Override
    public List<Rdv> findAll() {

        List<Rdv> liste = new ArrayList<>();

        String sql = "SELECT * FROM rdv";

        try (Connection con = JdbcConnectionFactory.createConnection(properties);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(convertirEnRdv(rs));
            }

        } catch (Exception e) {
            throw new RepositoryException("Erreur lors de la lecture des RDV", e);
        }

        return liste;
    }
    @Override
    public Rdv findById(Long id) {

        String sql = "SELECT * FROM rdv WHERE id = ?";

        try (Connection con = JdbcConnectionFactory.createConnection(properties);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return convertirEnRdv(rs);
                }
            }

        } catch (Exception e) {
            throw new RepositoryException("Erreur lors de la recherche du RDV", e);
        }

        return null;
    }
    @Override
    public Rdv save(Rdv rdv) {

        String sql = "INSERT INTO rdv " +
                "(dossier_id, medecin_id, debut, fin, motif, statut, arrivee_at) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = JdbcConnectionFactory.createConnection(properties);
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, rdv.getDossierId());
            ps.setLong(2, rdv.getMedecinId());
            ps.setTimestamp(3, Timestamp.valueOf(rdv.getDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(rdv.getFin()));
            ps.setString(5, rdv.getMotif());
            ps.setString(6, rdv.getStatut().name());

            if (rdv.getArriveeAt() == null) {
                ps.setNull(7, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(7, Timestamp.valueOf(rdv.getArriveeAt()));
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    rdv.setId(rs.getLong(1));
                }
            }

            return rdv;

        } catch (Exception e) {
            throw new RepositoryException("Erreur lors de l'ajout du RDV", e);
        }
    }
    @Override
    public boolean update(Rdv rdv) {

        String sql = "UPDATE rdv SET dossier_id=?, medecin_id=?, debut=?, " +
                "fin=?, motif=?, statut=?, arrivee_at=? WHERE id=?";

        try (Connection con = JdbcConnectionFactory.createConnection(properties);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, rdv.getDossierId());
            ps.setLong(2, rdv.getMedecinId());
            ps.setTimestamp(3, Timestamp.valueOf(rdv.getDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(rdv.getFin()));
            ps.setString(5, rdv.getMotif());
            ps.setString(6, rdv.getStatut().name());

            if (rdv.getArriveeAt() == null) {
                ps.setNull(7, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(7, Timestamp.valueOf(rdv.getArriveeAt()));
            }

            ps.setLong(8, rdv.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RepositoryException("Erreur lors de la modification du RDV", e);
        }
    }
    @Override
    public boolean delete(Rdv rdv) {
        return deleteById(rdv.getId());
    }
    @Override
    public boolean deleteById(Long id) {

        String sql = "DELETE FROM rdv WHERE id = ?";

        try (Connection con = JdbcConnectionFactory.createConnection(properties);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RepositoryException("Erreur lors de la suppression du RDV", e);
        }
    }
}


}
