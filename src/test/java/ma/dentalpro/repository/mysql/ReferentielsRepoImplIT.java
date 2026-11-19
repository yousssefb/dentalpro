package ma.dentalpro.repository.mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import ma.dentalpro.config.JdbcConnectionFactory;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.domain.Medicament;
import ma.dentalpro.shared.RepositoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test d'intégration des repositories Acte et Medicament sur la vraie base MySQL
 * (schema.sql + seed.sql chargés).
 *
 * <p>Chaque test s'exécute dans une transaction annulée à la fin : la base n'est pas modifiée.
 * Si db.properties est absent ou si MySQL est arrêté, les tests sont ignorés (et non en échec).</p>
 */
class ReferentielsRepoImplIT {

    private Connection connection;
    private ActeRepoImpl acteRepo;
    private MedicamentRepoImpl medicamentRepo;

    @BeforeEach
    void ouvrirTransaction() throws Exception {
        Properties props = new Properties();
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
        acteRepo = new ActeRepoImpl(connection);
        medicamentRepo = new MedicamentRepoImpl(connection);
    }

    @AfterEach
    void annulerTransaction() throws SQLException {
        if (connection != null) {
            connection.rollback(); // on annule tout : la base reste intacte
            connection.close();
        }
    }

    @Test
    void findAllLitLesActesEtLesMedicamentsDuSeed() {
        assertEquals(11, acteRepo.findAll().size());
        assertEquals(8, medicamentRepo.findAll().size());
    }

    @Test
    void findByIdLitToutesLesColonnesDUnActe() {
        Acte a = acteRepo.findById(2L);

        assertNotNull(a);
        assertEquals("Détartrage", a.getLibelle());
        assertEquals("PREVENTION", a.getCategorie());
        assertEquals(0, new BigDecimal("400.00").compareTo(a.getPrixBase()));
        assertTrue(a.isActif());
    }

    @Test
    void saveEtUpdateDUnActeFontUnAllerRetour() {
        Acte nouveau = Acte.builder().libelle("Acte de test").categorie("TEST")
                .prixBase(new BigDecimal("123.45")).build();

        Long id = acteRepo.save(nouveau).getId();
        assertNotNull(id);

        nouveau.setPrixBase(new BigDecimal("150.00"));
        nouveau.setActif(false);
        assertTrue(acteRepo.update(nouveau));

        Acte relu = acteRepo.findById(id);
        assertEquals(0, new BigDecimal("150.00").compareTo(relu.getPrixBase()));
        assertEquals(false, relu.isActif());
    }

    @Test
    void findByLibelleLikeChercheDansLeLibelle() {
        List<Acte> actes = acteRepo.findByLibelleLike("extraction");

        assertEquals(2, actes.size());
        assertEquals("Extraction dent de sagesse", actes.get(0).getLibelle());
        assertEquals("Extraction simple", actes.get(1).getLibelle());
    }

    @Test
    void unActeDejaUtiliseNePeutPasEtreSupprimePhysiquement() {
        // L'acte 2 (Détartrage) est utilisé par une intervention du seed : MySQL refuse (clé étrangère).
        try {
            acteRepo.deleteById(2L);
            fail("Une RepositoryException était attendue");
        } catch (RepositoryException e) {
            // c'est le comportement attendu : MySQL a refusé la suppression
        }
    }

    @Test
    void saveEtUpdateDUnMedicamentFontUnAllerRetour() {
        Medicament nouveau = Medicament.builder().nom("Médicament de test").forme("Gel").build();

        Long id = medicamentRepo.save(nouveau).getId();
        assertNotNull(id);

        nouveau.setForme("Sirop");
        assertTrue(medicamentRepo.update(nouveau));
        assertEquals("Sirop", medicamentRepo.findById(id).getForme());
    }

    @Test
    void findByNomLikeChercheDansLeNom() {
        List<Medicament> medicaments = medicamentRepo.findByNomLike("ibu");

        assertEquals(1, medicaments.size());
        assertEquals("Ibuprofène 400 mg", medicaments.get(0).getNom());
    }

    @Test
    void unMedicamentDejaPrescritNePeutPasEtreSupprimePhysiquement() {
        // Le médicament 2 (Paracétamol) figure dans une prescription du seed : MySQL refuse.
        try {
            medicamentRepo.deleteById(2L);
            fail("Une RepositoryException était attendue");
        } catch (RepositoryException e) {
            // c'est le comportement attendu : MySQL a refusé la suppression
        }
    }
}
