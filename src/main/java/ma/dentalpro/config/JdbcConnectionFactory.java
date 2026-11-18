package ma.dentalpro.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Crée la connexion JDBC à partir de db.properties.
 *
 * <p>Une variable d'environnement (DENTALPRO_DB_URL, DENTALPRO_DB_USER, DENTALPRO_DB_PASSWORD)
 * remplace la valeur du fichier lorsqu'elle est définie. Le mot de passe n'est jamais affiché.</p>
 */
public final class JdbcConnectionFactory {

    private JdbcConnectionFactory() {}

    public static Connection createConnection(Properties properties) {
        String driver   = required(properties, "dataSource.driver", null);
        String url      = required(properties, "dataSource.url", "DENTALPRO_DB_URL");
        String user     = required(properties, "dataSource.user", "DENTALPRO_DB_USER");
        String password = required(properties, "dataSource.pass", "DENTALPRO_DB_PASSWORD");

        try {
            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la base de données réussie : " + url);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Pilote JDBC introuvable : " + driver, e);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Impossible de se connecter à MySQL (" + url + ") : " + e.getMessage(), e);
        }
    }

    /** Lit une propriété obligatoire ; la variable d'environnement est prioritaire si elle existe. */
    private static String required(Properties properties, String key, String envName) {
        String value = null;
        if (envName != null) {
            value = System.getenv(envName);
        }
        if (value == null || value.isBlank()) {
            value = properties.getProperty(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Propriété JDBC obligatoire absente : " + key
                            + " (copier db.properties.example en db.properties)");
        }
        return value.trim();
    }
}
