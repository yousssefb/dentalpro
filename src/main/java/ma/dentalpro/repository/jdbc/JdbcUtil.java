package ma.dentalpro.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import ma.dentalpro.shared.RepositoryException;

/**
 * Regroupe les opérations techniques communes aux repositories JDBC.
 */
public final class JdbcUtil {

    private JdbcUtil() {}
    /**
     * Crée un PreparedStatement et affecte ses paramètres dans l'ordre des « ? » SQL.
     */
    public static PreparedStatement prepareStatement(
            Connection connection,
            String sql,
            boolean returnGeneratedKeys,
            Object... parameters
    ) throws SQLException {
        // Faut-il demander à MySQL l'identifiant généré (utile pour un INSERT) ?
        int generatedKeysOption = Statement.NO_GENERATED_KEYS;
        if (returnGeneratedKeys) {
            generatedKeysOption = Statement.RETURN_GENERATED_KEYS;
        }

        PreparedStatement statement = connection.prepareStatement(sql, generatedKeysOption);
        // Le premier « ? » a le numéro 1 (et pas 0), d'où index + 1.
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
        return statement;
    }

    /**
     * Transforme une SQLException en exception propre à la couche repository.
     */
    public static RepositoryException dataAccessException(
            String message,
            SQLException cause
    ) {
        return new RepositoryException(message, cause);
    }

    /**
     * Ferme explicitement une ressource JDBC utilisée par un try classique.
     */
    public static void closeResource(AutoCloseable resource) {
        if (resource == null) {
            return;
        }

        try {
            resource.close();
        } catch (Exception e) {
            System.err.println("Impossible de fermer cette ressource JDBC : " + e.getMessage());
        }
    }

    /**
     * Affiche le nombre de lignes affectées par une requête INSERT, UPDATE ou DELETE.
     */
    public static void logAffectedRows(String operation, int affectedRows) {
        if (affectedRows == 0) {
            System.out.printf("[JDBC] %s : aucune ligne affectée.%n", operation);
        } else {
            System.out.printf(
                    "[JDBC] %s : %d ligne(s) affectée(s).%n",
                    operation,
                    affectedRows
            );
        }
    }
}
