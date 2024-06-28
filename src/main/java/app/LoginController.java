package app;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named ("loginController")
@ViewScoped
public class LoginController implements Serializable {

    @Inject
    DatenAnzeige datenAnzeige;

    @Inject
    CurrentUser currentUser;

    // TODO: diese Wert sollte aus einer Konfiguration kommen.
    //       Jede Installation sollte eine Unterschiedlich haben.
    //       Dieser Salt muss geheim bleiben.
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    // das sind die text-felder (zB, um zu den Benutzern zu zeigen)
    String user, password;
    // dieses Feld ist für die Lagerung in den Validierungsstufen
    String tempUsername;
    // dieser Feld ist für die Anzeige zu den Benutzern das nächste Mal
    String failureMessage = "";

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    // this method should be called early to avoid providing information if the user is not logged in
    public void checkLogin() {
        if(!currentUser.isValid()) {
            System.out.println("Current User is not valid! checkLogin()");
            System.out.println("CurrentUser.isAdmin"+currentUser.isAdmin());
            System.out.println("CurrentUser.isValid"+currentUser.isValid());
            failureMessage = "Bitte loggen Sie sich ein.";
            FacesContext fc = FacesContext.getCurrentInstance();
            NavigationHandler nh = fc.getApplication().getNavigationHandler();
            nh.handleNavigation(fc, null, "login.xhtml?faces-redirect=true");
        }

    }

    public String logout() {
        currentUser.reset();
        return "Nutzeransicht.xhtml?faces-redirect=true";
    }

    public void postValidateUser(ComponentSystemEvent ev) {
        UIInput temp = (UIInput) ev.getComponent();
        this.tempUsername = (String) temp.getValue();
    }

    public void validateLogin(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        System.out.println("validateLogin reached");
        String password = (String) value;
        System.out.println("validateLogin Ergebniss: " + password +", " + ", " + currentUser + ", " + tempUsername + ", " + salt);
        datenAnzeige.validateUsernameAndPassword(currentUser, tempUsername, password, salt);
        if (!currentUser.isValid())
            throw new ValidatorException(new FacesMessage("Login falsch!"));
    }

    public String login() {
        System.out.println("login erreicht!!!");
        if (currentUser.isAdmin()) {
            this.failureMessage = "";
            System.out.println("Login: Wissenschaftleransicht");
            return "Wissenschaftleransicht.xhtml?faces-redirect=true";
        } else {
            System.out.println("Login: Passwort Bentutzer nicht bekannt");
            System.out.println("CurrentUser.isAdmin"+currentUser.isAdmin());
            System.out.println("CurrentUser.isValid"+currentUser.isValid());
            this.failureMessage = "Passwort und Benutzername nicht erkannt.";
            return "";
        }
    }

    public static void main(String[] args) {
        if(args.length!=2) {
            System.err.println("Usage: java LoginController user pass");
            System.exit(1);
        }

    }
}