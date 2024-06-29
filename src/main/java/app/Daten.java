package app;

public class Daten
{


    private String land;

    private String ausstoss;



    public Daten(String land, String ausstoss)
    {
        this.land = land;
        this.ausstoss = ausstoss;

    }

    @Override
    public String toString() {
        return "Daten{" +
                "land='" + land + '\'' +
                ", ausstoss='" + ausstoss + '\'' +
                '}';
    }

    public String getLand()
    {
        return land;
    }



    public String getAusstoss()
    {
        return ausstoss;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public void setAusstoss(String ausstoss) {
        this.ausstoss = ausstoss;
    }




}
