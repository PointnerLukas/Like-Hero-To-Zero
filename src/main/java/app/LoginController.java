package app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named ("loginController")
@ApplicationScoped
public class LoginController implements Serializable {

    @Inject
    DatenAnzeige datenAnzeige;

    @Inject
    CurrentUser currentUser;

    // TODO: diese Wert sollte aus einer Konfiguration kommen.
    //       Jede Installation sollte eine Unterschiedlich haben.
    //       Dieser Salt muss geheim bleiben.
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    String user, password;
    String tempUsername;


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


    public void checkLogin() {
        if(!currentUser.isValid()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            NavigationHandler nh = fc.getApplication().getNavigationHandler();
            nh.handleNavigation(fc, null, "login.xhtml?faces-redirect=true");
        }

    }

    public boolean isLoggedIn() {
        return currentUser != null && currentUser.isAdmin();
    }

    public String logout() {
        currentUser.reset();
        return "Nutzeransicht.xhtml?faces-redirect=true";
    }

    public void postValidateUser(ComponentSystemEvent ev) {
        System.out.println("postValidateUser Username:" + currentUser);
        UIInput temp = (UIInput) ev.getComponent();
        this.tempUsername = (String) temp.getValue();
    }

    public void validateLogin(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String Password = (String) value;
        System.out.println("validateLogin reached");
        System.out.println("tempUsername: " + tempUsername);
        System.out.println("password: " + Password);

        datenAnzeige.validateUsernameAndPassword(currentUser, tempUsername, Password, salt);
    }

    public String login() {
        System.out.println("login");
        System.out.println("tempUsername: " + tempUsername);
        System.out.println("password: " + password);
        System.out.println(currentUser.isAdmin());
        if (currentUser.isAdmin()) {
            return "Wissenschaftleransicht.xhtml?faces-redirect=true";
        } else {
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