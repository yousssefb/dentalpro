package ma.dentalpro.domain;

import java.time.LocalDateTime;

public class Indisponibilite {
    private Long id;
    private Long medecinId;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private String motif;
    public Indisponibilite() {}
    public Indisponibilite(Long medecinId, LocalDateTime debut, LocalDateTime fin, String motif) {
        this.medecinId = medecinId;
        this.debut = debut;
        this.fin = fin;
        this.motif = motif;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }
    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }
    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }
    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}

