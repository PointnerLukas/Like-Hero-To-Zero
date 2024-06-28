package app;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named ("currentUser")
@SessionScoped
public class CurrentUser implements Serializable {

    boolean admin;

    void reset() {
        admin = false;
    }

    boolean isAdmin() {
        return admin;
    }

    boolean isValid() {
        return isAdmin();
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
