package ma.dentalpro.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import ma.dentalpro.domain.Charge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests du repository mémoire des charges.
 */
class ChargeRepoImplTest {

    private ChargeRepoImpl repo;

    @BeforeEach
    void setUp() {
        VirtualDataBase.reset();
        repo = new ChargeRepoImpl();
    }

    @Test
    void findByTitreLikeTrouveParMotCle() {
        List<Charge> resultat = repo.findByTitreLike("loyer");

        assertEquals(1, resultat.size());
        assertEquals("Loyer du cabinet", resultat.get(0).getTitre());
    }

    @Test
    void saveAjouteUneChargeAvecNouvelId() {
        Charge nouvelle = Charge.builder()
                .auteurId(2L)
                .titre("Eau")
                .montant(new BigDecimal("200.00"))
                .dateCharge(LocalDateTime.now())
                .build();

        Charge sauvee = repo.save(nouvelle);

        assertNotNull(sauvee.getId());
        assertTrue(sauvee.getId() > 0);
        assertEquals("Eau", repo.findById(sauvee.getId()).getTitre());
    }
}
