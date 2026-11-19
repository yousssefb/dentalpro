package ma.dentalpro.repository.mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import ma.dentalpro.config.JdbcConnectionFactory;
import ma.dentalpro.domain.Antecedent;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.DossierAntecedent;
import ma.dentalpro.domain.Intervention;
import ma.dentalpro.domain.NiveauRisque;
import ma.dentalpro.domain.StatutConsultation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests d'intégration des repositories du module clinique (M3) sur la base MySQL réelle.
 *
 * <p>Chaque test s'exécute dans une transaction annulée à la fin (rollback), ce qui préserve
 * l'intégrité de la base. Si db.properties est absent ou si MySQL est éteint, les tests sont
 * ignorés (skipped) sans faire échouer la construction Maven.</p>
 */
class CliniqueRepoImplIT {

    private Connection connection;
    private AntecedentRepoImpl antecedentRepo;
    private DossierAntecedentRepoImpl dossierAntecedentRepo;
    private ConsultationRepoImpl consultationRepo;
    private InterventionRepoImpl interventionRepo;

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

        antecedentRepo = new AntecedentRepoImpl(connection);
        dossierAntecedentRepo = new DossierAntecedentRepoImpl(connection);
        consultationRepo = new ConsultationRepoImpl(connection);
        interventionRepo = new InterventionRepoImpl(connection);
    }

    @AfterEach
    void annulerTransaction() throws SQLException {
        if (connection != null) {
            connection.rollback();
            connection.close();
        }
    }

    // =====================================================================================
    // Tests AntecedentRepoImpl
    // =====================================================================================

    @Test
    void antecedentFindByIdLitLesDonneesDuSeed() {
        Antecedent a = antecedentRepo.findById(1L);
        assertNotNull(a);
        assertEquals("Diabète", a.getLibelle());
        assertEquals("MALADIE_CHRONIQUE", a.getCategorie());
        assertTrue(a.isActif());
    }

    @Test
    void antecedentSaveEtUpdateFonctionnentEnBase() {
        Antecedent nouveau = Antecedent.builder()
                .libelle("Allergie aux AINS (Test IT)")
                .categorie("ALLERGIE")
                .actif(true)
                .build();

        Antecedent sauve = antecedentRepo.save(nouveau);
        assertNotNull(sauve.getId());

        sauve.setActif(false);
        assertTrue(antecedentRepo.update(sauve));

        Antecedent relu = antecedentRepo.findById(sauve.getId());
        assertEquals(false, relu.isActif());
    }

    // =====================================================================================
    // Tests DossierAntecedentRepoImpl
    // =====================================================================================

    @Test
    void dossierAntecedentFindByDossierIdLitLesLignesDuSeed() {
        List<DossierAntecedent> liste = dossierAntecedentRepo.findByDossierId(1L);
        assertNotNull(liste);
        assertTrue(liste.size() >= 1);
        DossierAntecedent premier = liste.get(0);
        assertEquals(1L, premier.getDossierId());
        assertEquals(3L, premier.getAntecedentId());
        assertEquals(NiveauRisque.ELEVE, premier.getNiveauRisque());
    }

    @Test
    void dossierAntecedentSaveEtUpdateFonctionnentEnBase() {
        DossierAntecedent da = DossierAntecedent.builder()
                .dossierId(2L)
                .antecedentId(5L) // Asthme
                .niveauRisque(NiveauRisque.FAIBLE)
                .commentaire("Léger sifflement")
                .actif(true)
                .build();

        dossierAntecedentRepo.save(da);

        DossierAntecedent relu = dossierAntecedentRepo.findByDossierIdAndAntecedentId(2L, 5L);
        assertNotNull(relu);
        assertEquals(NiveauRisque.FAIBLE, relu.getNiveauRisque());

        relu.setNiveauRisque(NiveauRisque.MOYEN);
        assertTrue(dossierAntecedentRepo.update(relu));

        DossierAntecedent misAJour = dossierAntecedentRepo.findByDossierIdAndAntecedentId(2L, 5L);
        assertEquals(NiveauRisque.MOYEN, misAJour.getNiveauRisque());
    }

    // =====================================================================================
    // Tests ConsultationRepoImpl
    // =====================================================================================

    @Test
    void consultationFindByIdLitLeSeed() {
        Consultation c = consultationRepo.findById(1L);
        assertNotNull(c);
        assertEquals(1L, c.getDossierId());
        assertEquals(2L, c.getMedecinId());
        assertEquals(StatutConsultation.VALIDEE, c.getStatut());
        assertNotNull(c.getDateConsultation());
    }

    @Test
    void consultationSaveEtUpdateFonctionnentEnBase() {
        Consultation nouvelle = Consultation.builder()
                .dossierId(1L)
                .medecinId(2L)
                .dateConsultation(LocalDateTime.now())
                .observation("Contrôle semestriel (Test IT)")
                .statut(StatutConsultation.BROUILLON)
                .build();

        Consultation sauvee = consultationRepo.save(nouvelle);
        assertNotNull(sauvee.getId());

        sauvee.setStatut(StatutConsultation.VALIDEE);
        assertTrue(consultationRepo.update(sauvee));

        Consultation relue = consultationRepo.findById(sauvee.getId());
        assertEquals(StatutConsultation.VALIDEE, relue.getStatut());
    }

    // =====================================================================================
    // Tests InterventionRepoImpl
    // =====================================================================================

    @Test
    void interventionFindByConsultationIdLitLeSeed() {
        List<Intervention> interventions = interventionRepo.findByConsultationId(1L);
        assertNotNull(interventions);
        assertTrue(interventions.size() >= 2);
    }

    @Test
    void interventionSaveEtCalculTotalFonctionnentEnBase() {
        Intervention nouvelle = Intervention.builder()
                .consultationId(1L)
                .acteId(1L)
                .libelleActeSnapshot("Consultation de contrôle")
                .quantite(2)
                .prixUnitaire(new BigDecimal("200.00"))
                .build();

        Intervention sauvee = interventionRepo.save(nouvelle);
        assertNotNull(sauvee.getId());
        assertEquals(new BigDecimal("400.00"), sauvee.getTotal());

        sauvee.setQuantite(3);
        assertTrue(interventionRepo.update(sauvee));

        Intervention relue = interventionRepo.findById(sauvee.getId());
        assertEquals(3, relue.getQuantite());
        assertEquals(new BigDecimal("600.00"), relue.getTotal());
    }
}
