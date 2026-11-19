package ma.dentalpro.ui.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Charge;
import org.junit.jupiter.api.Test;

/**
 * Test simple du modèle de tableau des charges.
 */
class ChargeTableModelTest {

    @Test
    void setChargesRemplitLeTableau() {
        ChargeTableModel modele = new ChargeTableModel();

        List<Charge> liste = new ArrayList<>();
        Charge c = Charge.builder()
                .id(1L)
                .auteurId(2L)
                .titre("Loyer")
                .description("Mensuel")
                .montant(new BigDecimal("5000.00"))
                .dateCharge(LocalDateTime.of(2026, 11, 1, 9, 0))
                .build();
        liste.add(c);

        modele.setCharges(liste);

        assertEquals(1, modele.getRowCount());
        assertEquals(5, modele.getColumnCount());
        assertEquals("Loyer", modele.getValueAt(0, 1));
        assertEquals(new BigDecimal("5000.00"), modele.getValueAt(0, 3));
    }
}
