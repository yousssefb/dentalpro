package ma.dentalpro.repository.mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import ma.dentalpro.config.JdbcConnectionFactory;
import ma.dentalpro.domain.Patient;
import ma.dentalpro.domain.Sexe;
import ma.dentalpro.shared.RepositoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test d'intégration du DAO patient sur la vraie base MySQL (schema.sql + seed.sql chargés).
 *
 * <p>Chaque test s'exécute dans une transaction annulée à la fin : la base n'est pas modifiée.
 * Si db.properties est absent ou si MySQL est arrêté, les tests sont ignorés (et non en échec).</p>
 */
class PatientRepoImplIT {

    private Connection connection;
    private PatientRepoImpl repo;

    @BeforeEach
    void ouvrirTransaction() throws Exception {
        var props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            assumeTrue(in != null, "db.properties absent : tests MySQL ignorés");
            props.load(in);
        }
        try {
            connection = JdbcConnectionFactory.createConnection(props);
        } catch (IllegalStateException e) {
            assumeTrue(false, "MySQL indisponible : tests MySQL ignorés (" + e.getMessage() + ")");
        }
        connection.setAutoCommit(false);
        repo = new PatientRepoImpl(connection);
    }

    @AfterEach
    void annulerTransaction() throws SQLException {
        if (connection != null) {
            connection.rollback();
            connection.close();
        }
    }

    @Test
    void findByIdLitToutesLesColonnesDuSeed() {
        var p = repo.findById(1L);

        assertNotNull(p);
        assertEquals("Berrada", p.getNom());
        assertEquals("Fadoua", p.getPrenom());
        assertEquals(LocalDate.of(2005, 3, 14), p.getDateNaissance());
        assertEquals(Sexe.FEMME, p.getSexe());
        assertEquals("ZZ100001", p.getCin());
        assertNotNull(p.getCreatedAt());
    }

    @Test
    void saveEtUpdateFontUnAllerRetourComplet() {
        var nouveau = Patient.builder()
                .nom("Testeur").prenom("Integration")
                .dateNaissance(LocalDate.of(1995, 5, 5)).sexe(Sexe.HOMME)
                .telephone("0600009999").assurance("CNSS")
                .build();

        var id = repo.save(nouveau).getId();
        assertNotNull(id);

        nouveau.setTelephone("0600008888");
        nouveau.setArchive(true);
        assertTrue(repo.update(nouveau));

        var relu = repo.findById(id);
        assertEquals("Integration", relu.getPrenom());
        assertEquals(LocalDate.of(1995, 5, 5), relu.getDateNaissance());
        assertEquals("0600008888", relu.getTelephone());
        assertTrue(relu.isArchive());
    }

    @Test
    void findByNomLikeChercheDansNomEtPrenom() {
        var noms = repo.findByNomLike("ben").stream().map(Patient::getNom).toList();
        assertEquals(java.util.List.of("Benali", "Bennani"), noms);
    }

    @Test
    void unPatientAvecDossierNePeutPasEtreSupprimePhysiquement() {
        assertThrows(RepositoryException.class, () -> repo.deleteById(1L));
    }
}
