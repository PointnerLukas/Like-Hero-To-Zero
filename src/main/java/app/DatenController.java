package app;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named ("datenController")
@ViewScoped
public class DatenController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private DatenAnzeige datenAnzeige;

    private int index = 0;

    public Daten getDaten()
    {
        return datenAnzeige.getData().get(index);
    }

    public void vor()
    {
        if (index < datenAnzeige.getData().size() - 1) {
            index++;
        }
    }

    public void zurueck() {
        if (index > 0) {
            index--;
        }
    }

    public void removeDaten() {
        if(!datenAnzeige.getData().isEmpty())
            datenAnzeige.getData().remove(index);
    }

    public int getIndex() {
        return index;
    }

    public int getMaxIndex() {
        return datenAnzeige.getData().size()-1;
    }
}
