package app;

public class Daten
{
    private int nr;

    private String land;

    private String ausstoss;



    public Daten(int nr, String land, String ausstoss)
    {
        this.nr = nr;
        this.land = land;
        this.ausstoss = ausstoss;

    }

    @Override
    public String toString() {
        return "Daten{" +
                "nr= " + nr +
                ", land='" + land + '\'' +
                ", ausstoss='" + ausstoss + '\'' +
                '}';
    }

    public String getLand()
    {
        return land;
    }

    public int getNr()
    {
        return nr;
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

    public void setNr(int nr) {this.nr = nr;}


}
