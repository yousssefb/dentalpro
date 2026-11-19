package ma.dentalpro.repository.memory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import ma.dentalpro.domain.Consultation;
import ma.dentalpro.domain.StatutConsultation;
import ma.dentalpro.repository.api.ConsultationRepo;

/**
 * Implémentation en mémoire du repository des consultations.
 */
public class ConsultationRepoImpl implements ConsultationRepo {

    public ConsultationRepoImpl() {}

    public ConsultationRepoImpl(Connection connection) {}

    @Override
    public List<Consultation> findAll() {
        return new ArrayList<>(VirtualDataBase.consultations());
    }

    @Override
    public Consultation findById(Long identity) {
        if (identity == null) {
            return null;
        }
        for (Consultation c : VirtualDataBase.consultations()) {
            if (identity.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Consultation> findByDossierId(Long dossierId) {
        List<Consultation> resultat = new ArrayList<>();
        if (dossierId == null) {
            return resultat;
        }
        for (Consultation c : VirtualDataBase.consultations()) {
            if (dossierId.equals(c.getDossierId())) {
                resultat.add(c);
            }
        }
        return resultat;
    }

    @Override
    public List<Consultation> findByMedecinId(Long medecinId) {
        List<Consultation> resultat = new ArrayList<>();
        if (medecinId == null) {
            return resultat;
        }
        for (Consultation c : VirtualDataBase.consultations()) {
            if (medecinId.equals(c.getMedecinId())) {
                resultat.add(c);
            }
        }
        return resultat;
    }

    @Override
    public Consultation findByRdvId(Long rdvId) {
        if (rdvId == null) {
            return null;
        }
        for (Consultation c : VirtualDataBase.consultations()) {
            if (rdvId.equals(c.getRdvId())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        List<Consultation> resultat = new ArrayList<>();
        if (statut == null) {
            return resultat;
        }
        for (Consultation c : VirtualDataBase.consultations()) {
            if (statut == c.getStatut()) {
                resultat.add(c);
            }
        }
        return resultat;
    }

    @Override
    public Consultation save(Consultation c) {
        if (c == null) {
            throw new IllegalArgumentException("La consultation ne peut pas être null.");
        }
        if (c.getId() == null) {
            long maxId = 0L;
            for (Consultation existante : VirtualDataBase.consultations()) {
                if (existante.getId() != null && existante.getId() > maxId) {
                    maxId = existante.getId();
                }
            }
            c.setId(maxId + 1L);
        }
        VirtualDataBase.consultations().add(c);
        return c;
    }

    @Override
    public boolean update(Consultation c) {
        if (c == null || c.getId() == null) {
            return false;
        }
        for (int i = 0; i < VirtualDataBase.consultations().size(); i++) {
            Consultation existante = VirtualDataBase.consultations().get(i);
            if (c.getId().equals(existante.getId())) {
                VirtualDataBase.consultations().set(i, c);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Consultation element) {
        if (element == null || element.getId() == null) {
            return false;
        }
        return deleteById(element.getId());
    }

    @Override
    public boolean deleteById(Long identity) {
        if (identity == null) {
            return false;
        }
        for (int i = 0; i < VirtualDataBase.consultations().size(); i++) {
            Consultation existante = VirtualDataBase.consultations().get(i);
            if (identity.equals(existante.getId())) {
                VirtualDataBase.consultations().remove(i);
                return true;
            }
        }
        return false;
    }
}
