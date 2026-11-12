package ma.dentalpro.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import ma.dentalpro.domain.Patient;
import org.junit.jupiter.api.Test;

class ListUtilTest {

    private static Patient patient(Long id, String nom, String prenom) {
        return Patient.builder().id(id).nom(nom).prenom(prenom).build();
    }

    @Test
    void maxMarcheAvecDesEntiersDesChainesEtDesPatients() {
        assertEquals(9, ListUtil.max(List.of(3, 9, 1, 7)));
        assertEquals("Tazi", ListUtil.max(List.of("Alaoui", "Tazi", "Benali")));

        var tazi = patient(3L, "Tazi", "Sara");
        assertEquals(tazi, ListUtil.max(List.of(patient(1L, "Alaoui", "Ayman"), tazi, patient(2L, "Benali", "Omar"))));
    }

    @Test
    void maxSurUneListeVideLanceUneException() {
        assertThrows(IllegalArgumentException.class, () -> ListUtil.max(List.<Integer>of()));
    }

    @Test
    void inverserRetourneLesElementsALEnvers() {
        var liste = List.of(1, 2, 3, 4);

        assertEquals(List.of(4, 3, 2, 1), ListUtil.inverser(liste));
        assertEquals(List.of(1, 2, 3, 4), liste);
        assertEquals(List.of(), ListUtil.inverser(List.of()));
    }

    @Test
    void estOrdonneeVerifieLOrdreCroissant() {
        assertTrue(ListUtil.estOrdonnee(List.of(1, 2, 2, 5)));
        assertTrue(ListUtil.estOrdonnee(List.<Integer>of()));
        assertFalse(ListUtil.estOrdonnee(List.of(1, 5, 3)));
        assertTrue(ListUtil.estOrdonnee(List.of("Alaoui", "Benali", "Tazi")));
    }

    @Test
    void fusionnerDeuxListesTrieesDonneUneListeTriee() {
        var resultat = ListUtil.fusionner(List.of(1, 4, 6), List.of(2, 3, 5, 7, 8));

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), resultat);
        assertTrue(ListUtil.estOrdonnee(resultat));
        assertEquals(List.of(1, 2), ListUtil.fusionner(List.of(1, 2), List.of()));
    }

    @Test
    void fusionnerRefuseUneListeNonTriee() {
        assertThrows(IllegalArgumentException.class,
                () -> ListUtil.fusionner(List.of(3, 1), List.of(2, 4)));
    }
}
