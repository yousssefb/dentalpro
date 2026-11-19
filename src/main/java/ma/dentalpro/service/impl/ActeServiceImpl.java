package ma.dentalpro.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import ma.dentalpro.domain.Acte;
import ma.dentalpro.repository.api.ActeRepo;
import ma.dentalpro.service.api.ActeService;

/**
 * Règles métier du catalogue des actes : on vérifie les données, puis on délègue au repository.
 */
@AllArgsConstructor
public class ActeServiceImpl implements ActeService {

    // Couplage faible : le service ne connaît que l'interface du repository.
    private final ActeRepo repo;

    @Override
    public List<Acte> findAll() {
        return repo.findAll();
    }

    @Override
    public Acte findById(Long idActe) {
        return repo.findById(idActe);
    }

    @Override
    public List<Acte> findByLibelleLike(String libelle) {
        return repo.findByLibelleLike(libelle);
    }

    @Override
    public Acte save(Acte acte) {
        valider(acte);
        return repo.save(acte);
    }

    @Override
    public boolean update(Acte acte) {
        valider(acte);
        return repo.update(acte);
    }

    @Override
    public boolean delete(Acte acte) {
        return repo.delete(acte);
    }

    @Override
    public boolean deleteById(Long idActe) {
        return repo.deleteById(idActe);
    }

    /** Vérifie les règles du catalogue ; lance IllegalArgumentException avec un message clair sinon. */
    private void valider(Acte acte) {
        if (acte == null) {
            throw new IllegalArgumentException("L'acte ne peut pas être null");
        }
        if (acte.getLibelle() == null || acte.getLibelle().isBlank()) {
            throw new IllegalArgumentException("Le libellé de l'acte est obligatoire");
        }
        // signum() vaut 0 pour zéro, -1 pour un nombre négatif : on veut un prix strictement positif.
        if (acte.getPrixBase() == null || acte.getPrixBase().signum() <= 0) {
            throw new IllegalArgumentException("Le prix de base doit être supérieur à 0");
        }

        acte.setLibelle(acte.getLibelle().trim()); // on enlève les espaces inutiles

        // Deux actes ne peuvent pas avoir le même libellé (contrainte uq_acte_libelle en base).
        for (Acte existant : repo.findAll()) {
            boolean memeLibelle = existant.getLibelle().equalsIgnoreCase(acte.getLibelle());
            // equals compare les identifiants : en modification, l'acte ne se compare pas à lui-même.
            boolean autreActe = !existant.equals(acte);
            if (memeLibelle && autreActe) {
                throw new IllegalArgumentException("Un acte nommé « " + acte.getLibelle() + " » existe déjà");
            }
        }
    }
}
