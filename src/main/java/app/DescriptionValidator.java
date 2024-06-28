package app;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Named;

@ApplicationScoped
@Named ("descriptionValidator")
public class DescriptionValidator
{
   // Die Überprüfung eines leeren Feldes funktioniert so nicht, da die Überprüfung von leeren Feldern von JSF direkt übersprungen werden
    // wenn keine andere Überprüfung statt finden soll kann diese Datei gelöscht werden!!!
    public void check(FacesContext ctx, UIComponent cmp, Object value) throws ValidatorException {
        String land = (String) value;

        //Criteria 1: mindestens 1 Zeichen
        if (land.isEmpty())
            throw new ValidatorException(
                    new FacesMessage("Must not be empty"));

    }
}
