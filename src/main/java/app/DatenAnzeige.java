package app;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Named ("datenAnzeige")
@ApplicationScoped
public class DatenAnzeige

{
    private final String[][] users =
            new String[][]{
                    // password hash obtained with java LoginController koch i-am-the-boss
                    new String[]{"koch",
                            "+INdDt2JaxoJLHzD4iAlWPYMJA0uJhusP37DvMHBKmen15EMj1Vn7BAxWS1TYFniKFKjuSyIEFbxy9jSx4d8Tw==",
                            "admin"},
            };

    private List<Daten> data = new ArrayList<>();

    @Inject
    DatenController datenController;


    public DatenAnzeige()
    {
        data.add(new Daten(1, "Deutschland",
                "1"));
        data.add(new Daten(2, "Österreich",
                "2"));
        data.add(new Daten(3, "Schweiz",
                "3"));
    }

    public List<Daten> getData()
    {
        return data;
    }

    public void onRowEdit(RowEditEvent<Daten> event) {
        System.out.println("onRowEdit erreicht");
        System.out.println(data.toString());
        FacesMessage msg = new FacesMessage("Land wurde bearbeitet", String.valueOf(event.getObject().getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent<Daten> event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", String.valueOf(event.getObject().getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNew() {
        // Add one new product to the table:
        Daten newData = new Daten(data.size()+1, "a", "b");
        System.out.println(datenController.getMaxIndex());
        System.out.println("data.size()" + data.size());
        data.add(newData);
        System.out.println("newData:" + newData);
        System.out.println("data: " + data.toString());

        FacesMessage msg = new FacesMessage("New Product added", String.valueOf(newData.getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }



    static String hashPassword(String name, String pass, String salt) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((name + pass + salt)
                    .getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void validateUsernameAndPassword(CurrentUser currentUser, String name, String pass, String salt) {
        System.out.println("validateUsernameAndPassword reached");
        System.out.println("name: " + name + ", pass: " + pass + ", salt: " + salt);
        String passHash = hashPassword(name, pass, salt);
        System.out.println("passHash: " + passHash);
        System.out.println("currentUser: " + currentUser);
        currentUser.reset();
        for (String[] user : users) {
            System.out.println("user: " + user);
            System.out.println("username: " + user[0]);
            System.out.println("password: " + user[1]);
            System.out.println("salt: " + user[2]);
            if (user[0].equals(name)) {
                System.out.println("user[0]: " + user[0]);
                System.out.println("user[1]: " + user[1]);
                System.out.println("username: " + user[0]);
                if (user[1].equals(passHash)) {
                    System.out.println("passhash: " + passHash);
                    System.out.println("user[0]: " + user[0]);
                    System.out.println("user[1]: " + user[1]);
                    if (user[2].equals("admin")) {
                        System.out.println("admin true");
                        currentUser.setAdmin(true);
                        return;
                    } else throw new RuntimeException("Benutzer " + name + " ist falsch angelegt.");
                }
            }
        }
    }
}



/*

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class DatenAnzeige
{
    private static List<Daten> data = new ArrayList<Daten>();

    /**
     * Creates a new instance of Shop

    public DatenAnzeige()
    {
        data.add(new Daten(1, "Deutschland",
                "123"));
        data.add(new Daten(2, "Österreich",
                "23"));
        data.add(new Daten(3, "Schweiz",
                "45"));
    }

    public static List<Daten> getData()
    {
        return data;
    }
}
*/