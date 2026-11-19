package ma.dentalpro.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import ma.dentalpro.domain.Charge;
import ma.dentalpro.repository.api.ChargeRepo;
import ma.dentalpro.service.api.ChargeService;

/**
 * Implémentation simple du service des charges.
 *
 * <p>On vérifie les règles métier ICI (avant d'appeler le repository) :</p>
 * <ul>
 *   <li>le titre est obligatoire ;</li>
 *   <li>le montant doit être strictement positif.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class ChargeServiceImpl implements ChargeService {

    // Couplage faible : on dépend de l'interface ChargeRepo, pas de MySQL directement.
    private final ChargeRepo repo;

    @Override
    public List<Charge> getChargesSortedByDate() {
        // On copie la liste pour pouvoir la trier sans modifier celle du repository.
        List<Charge> charges = new ArrayList<>(repo.findAll());

        // Tri classique avec Comparator (classe anonyme) : dates les plus récentes d'abord.
        // Pas de lambda ici, pour rester simple comme un débutant.
        Collections.sort(charges, new Comparator<Charge>() {
            @Override
            public int compare(Charge a, Charge b) {
                LocalDateTime dateA = a.getDateCharge();
                LocalDateTime dateB = b.getDateCharge();

                // Une date absente (null) passe en dernier.
                if (dateA == null && dateB == null) {
                    return 0;
                }
                if (dateA == null) {
                    return 1;
                }
                if (dateB == null) {
                    return -1;
                }

                // compareTo normal = du plus ancien au plus récent.
                // On inverse (b.compareTo(a)) pour avoir le plus récent en premier.
                return dateB.compareTo(dateA);
            }
        });
        return charges;
    }

    @Override
    public Charge save(Charge charge) {
        verifierCharge(charge);
        return repo.save(charge);
    }

    @Override
    public boolean update(Charge charge) {
        verifierCharge(charge);
        return repo.update(charge);
    }

    @Override
    public boolean delete(Charge charge) {
        return repo.delete(charge);
    }

    @Override
    public List<Charge> findByTitreLike(String motCle) {
        return repo.findByTitreLike(motCle);
    }

    /**
     * Vérifie les champs obligatoires d'une charge.
     * Si une règle n'est pas respectée, on lance IllegalArgumentException
     * avec un message en français (l'écran pourra l'afficher).
     */
    private void verifierCharge(Charge charge) {
        if (charge == null) {
            throw new IllegalArgumentException("La charge ne peut pas être vide.");
        }

        // isBlank() est vrai pour "" ou "   " (texte vide ou seulement des espaces).
        if (charge.getTitre() == null || charge.getTitre().isBlank()) {
            throw new IllegalArgumentException("Le titre de la charge est obligatoire.");
        }

        if (charge.getMontant() == null) {
            throw new IllegalArgumentException("Le montant de la charge est obligatoire.");
        }

        // compareTo renvoie négatif si montant < 0, 0 si égal, positif si supérieur.
        // On exige montant > 0, donc compareTo(ZERO) doit être > 0.
        if (charge.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif.");
        }

        if (charge.getAuteurId() == null) {
            throw new IllegalArgumentException("L'auteur de la charge est obligatoire.");
        }
    }
}
