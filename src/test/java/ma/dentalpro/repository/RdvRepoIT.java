package ma.dentalpro.repository;

package ma.dentalpro.repository;

import ma.dentalpro.config.JdbcConnectionFactory;
import ma.dentalpro.domain.Rdv;
import ma.dentalpro.domain.StatutRdv;
import ma.dentalpro.repository.mysql.RdvRepoMySQL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class RdvRepoIT {
    private static Properties properties;
    private static RdvRepoMySQL repository;

    private static Long dossierId;
    private static Long medecinId;

    @BeforeAll
    static void initialiser() throws Exception {

        properties = new Properties();

        nputStream fichier = RdvRepoIT.class
                .getClassLoader()
                .getResourceAsStream("db.properties");

        Assumptions.assumeTrue(fichier != null,
                "db.properties absent : test ignoré");

        properties.load(fichier);
        fichier.close();
        repository = new RdvRepoMySQL(properties);
        try (Connection con = JdbcConnectionFactory.createConnection(properties);
             Statement st = con.createStatement()) {

            ResultSet rs = st.executeQuery(
                    "SELECT id FROM dossier_medical LIMIT 1");

            Assumptions.assumeTrue(rs.next(),
                    "Aucun dossier médical dans la base");

            dossierId = rs.getLong("id");
            rs.close();

            rs = st.executeQuery(
                    "SELECT id FROM utilisateur LIMIT 1");
            Assumptions.assumeTrue(rs.next(),
                    "Aucun médecin/utilisateur dans la base");

            medecinId = rs.getLong("id");
            rs.close();
        }
    }
    @Test
    void testAjouterEtSupprimerRdvDansMySQL() {

        Rdv rdv = new Rdv(
                dossierId,
                medecinId,
                LocalDateTime.of(2026, 11, 25, 10, 0),
                LocalDateTime.of(2026, 11, 25, 10, 30),
                "Test d'intégration",
                StatutRdv.PLANIFIE
        );
        Rdv resultat = repository.save(rdv);

        assertNotNull(resultat.getId());

        Rdv recherche = repository.findById(resultat.getId());

        assertNotNull(recherche);
        assertEquals("Test d'intégration", recherche.getMotif());

        boolean supprime = repository.deleteById(resultat.getId());

        assertTrue(supprime);
    }

}
