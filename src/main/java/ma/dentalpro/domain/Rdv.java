package ma.dentalpro.domain;
import java.time.LocalDateTime;

public class Rdv {
    private Long id;
    private Long dossierId;
    private Long medecinId;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private String motif;
    private StatuRdv statut;
    private LocalDateTime arriveeAT;

    public Rdv(){}

    public Rdv(Long dossierId, Long medecinId, LocalDateTime debut, LocalDateTime fin, String motif, StatuRdv statut){
        this.dossierId = dossierId;
        this.medecinId = medecinId;
        this.debut = debut;
        this.motif = motif;
        this.statut = statut;
    }

    public Long getId(){
        return id;
    }
    public void setId(Long Id){
        this.id = id;
    }
    public Long getDossierId(){
        return dossierId;
    }
    public void setDossierId(Long dossierId){
        this.dossierId = dossierId;
    }
    public Long getMedecinId(){
        return medecinId;
    }
    public void setMedecinId(Long medecinId){
        this.medecinId = medecinId;
    }
    public LocalDateTime getDebut(){
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
    public StatutRdv getStatut() {
        return statut;
    }
    public void setStatut(StatutRdv statut) {
        this.statut = statut;
    }
    public LocalDateTime getArriveeAt() {
        return arriveeAt;
    }
    public void setArriveeAt(LocalDateTime arriveeAt) {
        this.arriveeAt = arriveeAt;
    }



}
