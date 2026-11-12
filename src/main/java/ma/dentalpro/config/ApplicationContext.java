package ma.dentalpro.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import ma.dentalpro.repository.api.PatientRepo;
import ma.dentalpro.service.api.PatientService;
import ma.dentalpro.ui.controllers.api.PatientController;

/**
 * Fabrique (Factory) et Singleton de l'application.
 *
 * <p>Lit application.properties pour savoir quelles implémentations utiliser, lit
 * db.properties pour la connexion JDBC, puis crée et relie les composants :
 * Repository → Service → Controller. Changer d'implémentation se fait dans le fichier
 * de configuration, sans modifier le code (principe ouvert/fermé).</p>
 *
 * <p>Toute erreur de configuration arrête le démarrage avec un message clair, au lieu
 * de laisser l'application continuer avec des composants null.</p>
 */
public final class ApplicationContext {

    private static final String APP_CONFIG = "application.properties";
    private static final String DB_CONFIG = "db.properties";

    private static ApplicationContext instance;

    private final Map<Class<?>, Object> beansByClass = new HashMap<>();
    private Connection connection;

    private ApplicationContext() {
        var props = loadProperties(APP_CONFIG, true);
        props.putAll(loadProperties(DB_CONFIG, false));

        try {
            connection = JdbcConnectionFactory.createConnection(props);

            PatientRepo repo = createRepository(props.getProperty("dao.patientDao"), PatientRepo.class);
            PatientService service = create(props.getProperty("service.patientService"),
                    PatientService.class, PatientRepo.class, repo);
            PatientController controller = create(props.getProperty("controller.patientController"),
                    PatientController.class, PatientService.class, service);

            beansByClass.put(Connection.class, connection);
            beansByClass.put(PatientRepo.class, repo);
            beansByClass.put(PatientService.class, service);
            beansByClass.put(PatientController.class, controller);

            // La JVM fermera la connexion juste avant l'arrêt de l'application.
            Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnection));
        } catch (RuntimeException e) {
            closeConnection();
            throw e;
        }
    }

    /** Retourne l'instance unique, créée au premier appel (thread-safe). */
    public static synchronized ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public <T> T getBean(Class<T> beanClass) {
        var bean = beansByClass.get(beanClass);
        if (bean == null) {
            throw new IllegalStateException("Aucun composant enregistré pour " + beanClass.getSimpleName());
        }
        return beanClass.cast(bean);
    }

    // ———————————————————————————————————————————————————————————————————————————————————————
    // Lecture de la configuration
    // ———————————————————————————————————————————————————————————————————————————————————————

    private static Properties loadProperties(String fileName, boolean required) {
        var props = new Properties();
        var cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(fileName)) {
            if (in == null) {
                if (required) {
                    throw new IllegalStateException("Fichier de configuration introuvable : " + fileName);
                }
                return props;
            }
            props.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Lecture impossible du fichier " + fileName, e);
        }
    }

    // ———————————————————————————————————————————————————————————————————————————————————————
    // Création des composants par réflexion
    // ———————————————————————————————————————————————————————————————————————————————————————

    /**
     * Un repository JDBC reçoit la connexion dans son constructeur ; un repository en mémoire
     * n'a qu'un constructeur sans argument. Les deux sont acceptés.
     */
    private <T> T createRepository(String className, Class<T> type) {
        var clazz = loadClass(className, type);
        try {
            return type.cast(clazz.getDeclaredConstructor(Connection.class).newInstance(connection));
        } catch (NoSuchMethodException e) {
            return create(className, type, null, null);
        } catch (ReflectiveOperationException e) {
            throw creationError(className, e);
        }
    }

    private static <T, D> T create(String className, Class<T> type, Class<D> dependencyType, D dependency) {
        var clazz = loadClass(className, type);
        try {
            Object bean = dependencyType == null
                    ? clazz.getDeclaredConstructor().newInstance()
                    : clazz.getDeclaredConstructor(dependencyType).newInstance(dependency);
            return type.cast(bean);
        } catch (ReflectiveOperationException e) {
            throw creationError(className, e);
        }
    }

    private static Class<?> loadClass(String className, Class<?> expectedType) {
        if (className == null || className.isBlank()) {
            throw new IllegalStateException(
                    "Aucune implémentation configurée pour " + expectedType.getSimpleName());
        }
        try {
            var clazz = Class.forName(className.trim());
            if (!expectedType.isAssignableFrom(clazz)) {
                throw new IllegalStateException(
                        className + " n'implémente pas " + expectedType.getSimpleName());
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Classe introuvable (nom erroné ?) : " + className, e);
        }
    }

    private static IllegalStateException creationError(String className, ReflectiveOperationException e) {
        Throwable cause = e instanceof InvocationTargetException ite ? ite.getCause() : e;
        return new IllegalStateException("Création impossible de " + className, cause);
    }

    private void closeConnection() {
        if (connection == null) {
            return;
        }
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erreur pendant la fermeture de la connexion JDBC : " + e.getMessage());
        }
    }
}
