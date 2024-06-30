package app;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("datenAnzeige")
@ApplicationScoped
public class DatenAnzeige {

    private final String[][] users = new String[][]{
            // password hash obtained with java LoginController koch i-am-the-boss
            new String[]{"koch",
                    "+INdDt2JaxoJLHzD4iAlWPYMJA0uJhusP37DvMHBKmen15EMj1Vn7BAxWS1TYFniKFKjuSyIEFbxy9jSx4d8Tw==",
                    "admin"},
    };

    private List<Daten> data = new ArrayList<>();

    private static final Logger LOGGER = Logger.getLogger(DatenAnzeige.class.getName());

    static final List<Daten> baseCountries = new ArrayList<>();

    private EntityManagerFactory emf;
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            emf = Persistence.createEntityManagerFactory("LikeHeroToZero");
            entityManager = emf.createEntityManager();

            long count = (long) entityManager.createQuery("SELECT count(a) FROM Daten a").getSingleResult();
            LOGGER.log(Level.INFO, "Datensätze Anzahl: " + count);

            if (count == 0) {
                File excelFile = new File("/Users/Lukas/IdeaProjects/Like-Hero-To-Zero/src/main/resources/Co2Daten.xlsx");
                importDataFromExcel(excelFile);
                entityManager.getTransaction().begin();
                try {
                    for (Daten data : baseCountries) {
                        entityManager.persist(data);
                    }
                    entityManager.getTransaction().commit();
                    LOGGER.log(Level.INFO, "Basisländer erfolgreich eingefügt");
                } catch (Exception e) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    LOGGER.log(Level.SEVERE, "Fehler beim Einfügen der Basisländer", e);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler bei Initialisierung von DatenAnzeige", e);
        }
    }

    public void importDataFromExcel(File excelFile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row currentRow : sheet) {
                Cell landCell = currentRow.getCell(0);
                Cell ausstossCell = currentRow.getCell(1);

                String land = getStringValue(landCell);
                String ausstoss = getFormattedNumericValue(ausstossCell);

                Daten newData = new Daten(land, ausstoss);
                baseCountries.add(newData);
            }
            fileInputStream.close();
            LOGGER.log(Level.INFO, "Daten erfolgreich aus Excel importiert.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Importieren von Daten aus Excel: " + e.getMessage(), e);
        }
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                throw new IllegalStateException("Unsupported cell type");
        }
    }

    private String getFormattedNumericValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double value = cell.getNumericCellValue();
            // Format the numeric value to three decimal places
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            return decimalFormat.format(value);
        } else {
            throw new IllegalStateException("Cell is not of type NUMERIC");
        }
    }

    public List<Daten> getData() {
        TypedQuery<Daten> query = entityManager.createQuery("SELECT a FROM Daten a", Daten.class);
        List<Daten> resultList = query.getResultList();
        return resultList;
    }

    public void onRowEdit(RowEditEvent<Daten> event) {
        Daten editedData = event.getObject();
        System.out.println("onRowEdit erreicht");
        System.out.println(data.toString());

        entityManager.getTransaction().begin();
        try {
            entityManager.merge(editedData);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
        FacesMessage msg = new FacesMessage("Land wurde bearbeitet", String.valueOf(event.getObject().getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent<Daten> event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", String.valueOf(event.getObject().getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNew() {
        Daten newData = new Daten("Neues Land", "Neuer Ausstoß");

        entityManager.getTransaction().begin();
        try {
            entityManager.persist(newData);
            entityManager.getTransaction().commit();
            data.add(newData);
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
        FacesMessage msg = new FacesMessage("New Product added", String.valueOf(newData.getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void removeDaten(Daten daten) {
        entityManager.getTransaction().begin();
        try {
            Daten managedDaten = entityManager.find(Daten.class, daten.getId());
            if (managedDaten != null) {
                entityManager.remove(managedDaten);
                data.remove(daten);
                entityManager.getTransaction().commit();
            }
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
        FacesMessage msg = new FacesMessage("Daten gelöscht", String.valueOf(daten.getLand()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    static String hashPassword(String name, String pass, String salt) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((name + pass + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void validateUsernameAndPassword(CurrentUser currentUser, String name, String pass, String salt) {
        String passHash = hashPassword(name, pass, salt);
        currentUser.reset();
        boolean loginSuccessful = false;

        for (String[] user : users) {
            if (user[0].equals(name) && user[1].equals(passHash)) {
                loginSuccessful = true;
                if (user[2].equals("admin")) {
                    currentUser.setAdmin(true);
                }
                break;
            }
        }

        if (!loginSuccessful) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fehlgeschlagen", "Benutzername oder Passwort ist falsch.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
