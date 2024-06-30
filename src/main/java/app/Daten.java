package app;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class Daten implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String land;
    private String ausstoss;

    public Daten(String land, String ausstoss) {
        this.land = land;
        this.ausstoss = ausstoss;
    }

    public Daten() {}

    @Override
    public String toString() {
        return "Daten{" +
                "id=" + id +
                ", land='" + land + '\'' +
                ", ausstoss='" + ausstoss + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getLand() {
        return land;
    }

    public String getAusstoss() {
        return ausstoss;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public void setAusstoss(String ausstoss) {
        this.ausstoss = ausstoss;
    }
}
