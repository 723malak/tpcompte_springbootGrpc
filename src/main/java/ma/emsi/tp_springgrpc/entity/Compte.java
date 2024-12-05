package ma.emsi.tp_springgrpc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import ma.emsi.tp_springgrpc.stubs.TypeCompte;

@Entity
public class Compte {

    @Id
    private String id;

    private Float solde;
    private String dateCreation;

    @Enumerated(EnumType.STRING)
    private TypeCompte type;

    // Constructeur par défaut
    public Compte() {}

    // Constructeur avec paramètres
    public Compte(String id, Float solde, String dateCreation, TypeCompte type) {
        this.id = id;
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.type = type;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getSolde() {
        return solde;
    }

    public void setSolde(Float solde) {
        this.solde = solde;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public TypeCompte getType() {
        return type;
    }

    public void setType(TypeCompte type) {
        this.type = type;
    }
}
