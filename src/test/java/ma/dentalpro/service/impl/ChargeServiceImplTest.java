package ma.dentalpro.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.repository.memory.ChargeRepoImpl;
import ma.dentalpro.repository.memory.VirtualDataBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires du service des charges (sans MySQL).
 */
class ChargeServiceImplTest {

    private ChargeServiceImpl service;

    @BeforeEach
    void setUp() {
        // Avant chaque test, on remet les données fictives de départ.
        VirtualDataBase.reset();
        service = new ChargeServiceImpl(new ChargeRepoImpl());
    }

    @Test
    void getChargesSortedByDateMetLesPlusRecentesEnPremier() {
        List<Charge> charges = service.getChargesSortedByDate();

        assertTrue(charges.size() >= 2);
        // La première date doit être >= à la deuxième (ordre décroissant).
        LocalDateTime premiere = charges.get(0).getDateCharge();
        LocalDateTime deuxieme = charges.get(1).getDateCharge();
        assertTrue(!premiere.isBefore(deuxieme));
    }

    @Test
    void saveRefuseUneChargeSansTitre() {
        Charge sansTitre = new Charge();
        sansTitre.setAuteurId(2L);
        sansTitre.setMontant(new BigDecimal("100.00"));
        sansTitre.setDateCharge(LocalDateTime.now());

        IllegalArgumentException erreur = assertThrows(IllegalArgumentException.class,
                () -> service.save(sansTitre));
        assertEquals("Le titre de la charge est obligatoire.", erreur.getMessage());
    }

    @Test
    void saveRefuseUnMontantNegatifOuNul() {
        Charge mauvaise = new Charge();
        mauvaise.setAuteurId(2L);
        mauvaise.setTitre("Test");
        mauvaise.setMontant(BigDecimal.ZERO);
        mauvaise.setDateCharge(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> service.save(mauvaise));
    }

    @Test
    void saveAccepteUneChargeValide() {
        Charge nouvelle = new Charge();
        nouvelle.setAuteurId(2L);
        nouvelle.setTitre("Achat compresses");
        nouvelle.setMontant(new BigDecimal("85.50"));
        nouvelle.setDateCharge(LocalDateTime.now());

        Charge sauvee = service.save(nouvelle);

        assertNotNull(sauvee.getId());
        assertEquals("Achat compresses", sauvee.getTitre());
    }
}
