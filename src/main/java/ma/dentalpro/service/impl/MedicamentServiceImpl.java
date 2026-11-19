package ma.dentalpro.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import ma.dentalpro.domain.Medicament;
import ma.dentalpro.repository.api.MedicamentRepo;
import ma.dentalpro.service.api.MedicamentService;

/**
 * Règles métier du référentiel des médicaments : on vérifie les données, puis on délègue au repository.
 */
@AllArgsConstructor
public class MedicamentServiceImpl implements MedicamentService {

    // Couplage faible : le service ne connaît que l'interface du repository.
    private final MedicamentRepo repo;

    @Override
    public List<Medicament> findAll() {
        return repo.findAll();
    }

    @Override
    public Medicament findById(Long idMedicament) {
        return repo.findById(idMedicament);
    }

    @Override
    public List<Medicament> findByNomLike(String nom) {
        return repo.findByNomLike(nom);
    }

    @Override
    public Medicament save(Medicament medicament) {
        valider(medicament);
        return repo.save(medicament);
    }

    @Override
    public boolean update(Medicament medicament) {
        valider(medicament);
        return repo.update(medicament);
    }

    @Override
    public boolean delete(Medicament medicament) {
        return repo.delete(medicament);
    }

    @Override
    public boolean deleteById(Long idMedicament) {
        return repo.deleteById(idMedicament);
    }

    /** Vérifie les règles du référentiel ; lance IllegalArgumentException avec un message clair sinon. */
    private void valider(Medicament medicament) {
        if (medicament == null) {
            throw new IllegalArgumentException("Le médicament ne peut pas être null");
        }
        if (medicament.getNom() == null || medicament.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom du médicament est obligatoire");
        }

        medicament.setNom(medicament.getNom().trim()); // on enlève les espaces inutiles

        // Même nom + même forme = doublon (contrainte uq_medicament_nom_forme en base).
        for (Medicament existant : repo.findAll()) {
            boolean memeNom = existant.getNom().equalsIgnoreCase(medicament.getNom());
            boolean memeForme = memeTexte(existant.getForme(), medicament.getForme());
            boolean autreMedicament = !existant.equals(medicament);
            if (memeNom && memeForme && autreMedicament) {
                throw new IllegalArgumentException("Ce médicament existe déjà avec la même forme");
            }
        }
    }

    /** Compare deux textes sans tenir compte de la casse ; deux textes absents (null) sont égaux. */
    private static boolean memeTexte(String a, String b) {
        if (a == null || b == null) {
            return a == b; // vrai seulement si les deux sont null
        }
        return a.equalsIgnoreCase(b);
    }
}
