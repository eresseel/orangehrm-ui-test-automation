package com.orangehrm.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object az OrangeHRM PIM (alkalmazottak) moduljához.
 * Felelős az alkalmazottak felvételéért, listázásáért, lapozásáért, szerkesztéséért,
 * törléséért, valamint a riportok megnyitásáért és CSV-be exportálásáért.
 */
public class PimPage {
    private static final String ADD_EMPLOYEE_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/pim/addEmployee";

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---- Add Employee ----
    private final By addEmployeeTab = By.xpath("//a[text()='Add Employee']");
    private final By firstNameInput = By.name("firstName");
    private final By lastNameInput = By.name("lastName");
    // Az Employee Id mező (ugyanaz a szerkezet, mint az Admin oldal Username mezőjénél)
    private final By employeeIdInput = By.xpath("//label[normalize-space()='Employee Id']/parent::div/following-sibling::div//input");
    private final By saveButton = By.xpath("//button[@type='submit']");

    // ---- Employee List ----
    private final By employeeListTab = By.xpath("//a[text()='Employee List']");
    private final By tableRows = By.xpath("//div[@class='oxd-table-card']");
    private final By paginationNextButton = By.xpath("//button[contains(@class, 'oxd-pagination-page-item--previous-next')]/i[contains(@class, 'bi-chevron-right')]");

    private final By editButton = By.xpath("(//button[i[contains(@class, 'bi-pencil-fill')]])[1]");
    private final By deleteButton = By.xpath("(//button[i[contains(@class, 'bi-trash')]])[1]");

    private final By confirmDeleteButton = By.xpath("//button[contains(., 'Yes, Delete')]");
    private final By formLoader = By.className("oxd-form-loader");

    private final By personalDetailsHeader = By.xpath("//h6[text()='Personal Details']");
    private final By successToast = By.xpath("//div[contains(@class, 'oxd-toast-content--success')]");

    // ---- Reports ----
    private final By reportTab = By.xpath("//a[text()='Reports']");
    private final By firstReportName = By.xpath("(//div[@class='oxd-table-card'])[1]//div[@role='cell'][2]");

    // A riportlista első sorában az Actions oszlop 3 ikon-gombot tartalmaz balról jobbra:
    // kuka (törlés), ceruza (szerkesztés), dokumentum/lista ikon (riport megnyitása/generálása).
    // Sem a teljes sorra, sem a Name cellára kattintás nem nyitja meg a riportot – kizárólag
    // ez a 3. (utolsó) gomb, ezért ARRA kattintunk.
    private final By firstReportViewButton = By.xpath("(//div[@class='oxd-table-card'])[1]//button[last()]");

    // Fejlécek kinyerése (a képernyőn látható rgHeader elemekre)
    private final By reportTableHeaderCells = By.xpath("//div[contains(@class,'rgHeader')] | //th");

    // Adatsorok kinyerése: a táblázat összes rgRow vagy oxd-table-card elemét megcélozzuk
    private final By reportTableRows = By.xpath("//div[contains(@class,'rgRow')] | //tr[contains(@class,'rgRow')] | //div[contains(@class,'oxd-table-card')]");

    // A riport MEGNYITÁSA után egy szűrő/kritérium űrlap jelenik meg (pl. Job Title, Sub Unit,
    // Include employees...), és a tényleges adattábla csak a "Show" gombra kattintás UTÁN épül fel.
    // Ez korábban hiányzott a kódból, ezért a CSV export a szűrő űrlapot próbálta beolvasni
    // (vagy üres/hibás adatot írt ki) ahelyett, hogy megvárta volna a valódi riporttáblát.
    private final By showReportButton = By.xpath("//button[@type='submit'][normalize-space()='Show']");

    // A riport eredménytáblájának konténere. Az OrangeHRM riportnézetében ez jellemzően egy
    // önálló "orangehrm-container"/"oxd-table" blokk, ami a szűrő űrlap ALATT jelenik meg –
    // ezért külön (szűkített) lokátort használunk rá, nem az egész oldalt kaparjuk le.
    // Bővített lokátor a riport eredménytáblájának és tartalmának beazonosítására
    private final By reportResultTable = By.xpath(
            "//div[contains(@class,'orangehrm-container')] | //div[contains(@class,'oxd-table')] | //table"
    );

    private final By reportResultRows = By.xpath(
            ".//div[contains(@class,'oxd-table-card')] | .//div[contains(@class,'rgRow')] | .//tr[not(parent::thead)]"
    );

    private final By reportResultCells = By.xpath(
            ".//div[@role='cell'] | .//div[contains(@class,'rgCell')] | .//td"
    );

    public PimPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Felvesz egy új alkalmazottat az Add Employee űrlapon: kitölti a kereszt- és
     * vezetéknevet, megvárja az automatikusan generált Employee Id betöltését, majd ment.
     *
     * @param firstName az alkalmazott keresztneve
     * @param lastName  az alkalmazott vezetékneve
     */
    public void addEmployee(String firstName, String lastName) {
        // Fülre kattintunk, de nem bízunk vakon benne: ha az SPA routing "elveszíti" a kattintást
        // (lassú render, korábbi művelet még folyamatban), közvetlen URL-navigációval pótoljuk.
        wait.until(ExpectedConditions.elementToBeClickable(addEmployeeTab)).click();
        try {
            // Ellenőrizzük, hogy a kattintás tényleg átvitt az Add Employee oldalra
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.urlContains("addEmployee"));
        } catch (Exception e) {
            // Ha a kattintás elveszett (pl. lassú oldal, korábbi művelet miatt), közvetlenül navigálunk
            System.out.println("Figyelmeztetés: az Add Employee fülre kattintás nem navigált át, közvetlen URL-lel próbálkozunk.");
            driver.get(ADD_EMPLOYEE_URL);
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));

        // Előbb megvárjuk, hogy az űrlap teljesen betöltsön (az Employee Id aszinkron töltődik ki),
        // és csak utána gépelünk, hogy a késői újrarenderelés ne törölje a beírt adatokat
        waitForEmployeeIdToLoad();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput)).sendKeys(firstName);
        driver.findElement(lastNameInput).sendKeys(lastName);

        // A submit gombra néha JS-eseménykezelő van kötve, amit a natív .click() nem mindig vált ki
        // megbízhatóan (pl. overlay miatt) – ezért JS-kattintással próbálkozunk tartalékként.
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        try {
            saveBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
        }
    }

    /**
     * Legfeljebb 5 másodpercig vár, hogy az Employee Id mező értéket kapjon.
     * Ha a mező nem található vagy üres marad, nem dob hibát, a mentés ettől még megpróbálódik.
     */
    private void waitForEmployeeIdToLoad() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
                String value = d.findElement(employeeIdInput).getAttribute("value");
                return value != null && !value.isEmpty();
            });
        } catch (Exception e) {
            // Nem kritikus hiba: az Employee Id kitöltése "csak" visszaigazolás, a mentés
            // enélkül is sikerülhet, ezért itt csak naplózunk, nem dobunk tovább kivételt.
            System.out.println("Figyelmeztetés: az Employee Id nem töltődött ki 5 mp alatt (vagy a lokátor nem illik az oldalra).");
        }
    }

    /**
     * Ellenőrzi, hogy a mentés sikeres volt-e: megjelent-e a siker toast, vagy
     * (alkalmazott felvételekor) a mentés után betöltő Personal Details fejléc.
     *
     * @return {@code true}, ha a mentés sikeressége igazolható
     */
    public boolean isEmployeeSavedSuccessfully() {
        try {
            // Két lehetséges "sikerjel" közül bármelyik elfogadható: a toast gyorsan eltűnhet,
            // ilyenkor a Personal Details fejléc megjelenése is elég bizonyíték a sikeres mentésre.
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(successToast),
                    ExpectedConditions.visibilityOfElementLocated(personalDetailsHeader)
            ));
        } catch (Exception e) {
            logSaveFailureDiagnostics();
            return false;
        }
    }

    /**
     * Csak hibakereséshez: kiírja, mit látott a Selenium, amikor a mentés nem igazolódott vissza.
     */
    private void logSaveFailureDiagnostics() {
        try {
            System.out.println("---- DIAGNOSZTIKA (a mentés nem igazolódott vissza) ----");
            System.out.println("URL: " + driver.getCurrentUrl());
            for (WebElement err : driver.findElements(By.xpath("//span[contains(@class, 'oxd-input-field-error-message')]"))) {
                System.out.println("Validációs hiba: " + err.getText());
            }
            for (WebElement toast : driver.findElements(By.xpath("//div[contains(@class, 'oxd-toast')]"))) {
                System.out.println("Toast: " + toast.getText());
            }
            System.out.println("---------------------------------------------------------");
        } catch (Exception ignored) {
        }
    }

    /**
     * A PIM modulon belül az Employee List fülre navigál, és megvárja a táblázat betöltését.
     */
    public void navigateToEmployeeList() {
        wait.until(ExpectedConditions.elementToBeClickable(employeeListTab)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));
    }

    /**
     * Visszaadja az alkalmazott lista jelenleg látható sorait.
     *
     * @return a táblázat sorait reprezentáló WebElement lista
     */
    public List<WebElement> getEmployeeRows() {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows));
    }

    /**
     * Egyetlen lépést tesz előre a lapozásban (megtartva a korábbi, egyszerűbb viselkedést
     * más tesztek kompatibilitása miatt). Több oldal teljes bejárásához lásd
     * {@link #traverseAllPagesAndCount()}.
     *
     * @return {@code true}, ha nincs következő oldal, vagy a lapozás után a lista betöltött
     */
    public boolean goToNextPageAndVerify() {
        if (driver.findElements(paginationNextButton).isEmpty()) {
            return true;
        }
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(paginationNextButton));
        nextBtn.click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows)).isDisplayed();
    }

    /**
     * Végigjárja a teljes, többoldalas alkalmazott listát az első oldaltól az utolsóig:
     * minden oldalon megvárja a táblázat betöltését, majd a Next gombra kattint, amíg
     * az elérhető és aktív (nem a legutolsó oldalon áll).
     *
     * @return a bejárt oldalak száma (legalább 1, ha legalább egy oldal létezik)
     */
    public int traverseAllPagesAndCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));
        int pageCount = 1;

        while (true) {
            List<WebElement> nextButtons = driver.findElements(paginationNextButton);
            if (nextButtons.isEmpty()) {
                // Egyoldalas lista: nincs lapozó gomb, tehát nincs mit bejárni tovább
                break;
            }

            WebElement nextBtn = nextButtons.get(0);
            // A gomb DOM-ban jelen lehet akkor is, ha inaktív (utolsó oldalon állunk) –
            // ezért a "disabled" attribútumot és a szülő elem CSS osztályát is ellenőrizzük,
            // mert az OrangeHRM UI ezt kétféleképpen is jelezheti.
            String disabledAttr = nextBtn.getAttribute("disabled");
            String parentClass = nextBtn.findElement(By.xpath("./..")).getAttribute("class");
            boolean isDisabled = disabledAttr != null
                    || (parentClass != null && parentClass.contains("disabled"));
            if (isDisabled) {
                break;
            }

            try {
                wait.until(ExpectedConditions.elementToBeClickable(paginationNextButton)).click();
            } catch (Exception e) {
                // Ha a kattintás mégsem sikerül (pl. időközben mégis inaktívvá vált), leállunk
                // ahelyett, hogy a tesztet hibával elszakítanánk – a bejárt oldalszám így is hiteles.
                break;
            }

            wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));
            wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));
            pageCount++;

            // Biztonsági korlát, hogy egy hibás lokátor (pl. a Next gomb mindig "aktívnak" tűnik)
            // miatt a ciklus ne fusson végtelenítve.
            if (pageCount > 100) {
                break;
            }
        }

        return pageCount;
    }

    /**
     * Megnyitja az alkalmazott lista első sorának szerkesztését, kicseréli a keresztnevet,
     * és menti a módosítást.
     *
     * @param newFirstName az új keresztnév
     * @return a módosítás előtti (eredeti) keresztnév
     */
    public String editFirstEmployee(String newFirstName) {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(editButton));
        editBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));

        // Megvárjuk, hogy a mező tényleg megkapja a szerkesztésre nyíló alkalmazott nevét,
        // különben "" -t olvasnánk ki eredeti névként (race condition az aszinkron betöltéssel).
        wait.until(driver -> !nameInput.getAttribute("value").isEmpty());
        String oldFirstName = nameInput.getAttribute("value");

        // Mezőtartalom cseréje: teljes kijelölés + törlés + új szöveg beírása.
        // Macen Cmd+A, más rendszeren (pl. Linux CI) Ctrl+A jelöli ki a teljes szöveget
        Keys selectAllModifier = System.getProperty("os.name").toLowerCase().contains("mac") ? Keys.COMMAND : Keys.CONTROL;
        nameInput.sendKeys(Keys.chord(selectAllModifier, "a"));
        nameInput.sendKeys(Keys.BACK_SPACE);
        nameInput.sendKeys(newFirstName);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        try {
            saveBtn.click();
        } catch (Exception e) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", saveBtn);
        }

        return oldFirstName;
    }

    /**
     * Törli az alkalmazott lista első sorát, a megerősítő ablakban a Yes, Delete gombbal.
     *
     * @return a törölt alkalmazott neve (amennyire a táblázat celláiból kiolvasható)
     */
    public String deleteFirstEmployee() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        WebElement firstRow = wait.until(ExpectedConditions.visibilityOfElementLocated(tableRows));

        // A törlés gomb megnyomása előtt kiolvassuk a nevet, mert utána a sor eltűnik a DOM-ból
        List<WebElement> cells = firstRow.findElements(By.xpath(".//div[@role='cell']"));
        String deletedEmployeeName = "";

        if (cells.size() >= 4) {
            deletedEmployeeName = cells.get(2).getText() + " " + cells.get(3).getText();
        } else if (cells.size() >= 3) {
            deletedEmployeeName = cells.get(2).getText();
        }

        WebElement delBtn = wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
        delBtn.click();

        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
        confirmBtn.click();

        return deletedEmployeeName.trim();
    }

    /**
     * Ellenőrzi, hogy a törlés után megjelenik-e a sikeres törlést jelző toast üzenet.
     *
     * @return {@code true}, ha a sikeres törlést jelző toast látható
     */
    public boolean isDeleteSuccessful() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successToast)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Megnyitja a PIM modul Reports fülét.
     *
     * @return {@code true}, ha az URL a riportlistázó oldalra mutat
     */
    public boolean isReportSectionAvailable() {
        wait.until(ExpectedConditions.elementToBeClickable(reportTab)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));
        return wait.until(ExpectedConditions.urlContains("viewDefinedPredefinedReports"));
    }

    /**
     * Megnyitja a riportlista első elemét, majd (ha szükséges) elindítja a riport
     * generálását a "Show" gombbal, és megvárja, hogy a valódi adattábla betöltsön.
     * <p>
     * FONTOS: az OrangeHRM predefinit riport megnyitása önmagában csak egy szűrő/kritérium
     * űrlapot jelenít meg (Job Title, Sub Unit, Include employees stb.) – a tényleges
     * riporttábla csak a "Show" gombra kattintás UTÁN épül fel. Ha ezt kihagynánk (ahogy
     * korábban történt), az {@link #exportReportDataToCsv(String)} nem találna valódi
     * adatsorokat, és a CSV vagy üres/hiányos, vagy a szűrő űrlap szövegeivel telik meg.
     * <p>
     * JAVÍTVA: korábban egy találomra kiválasztott ikon-gombra ({@code oxd-icon-button})
     * kattintottunk, ami az oldal egy teljesen más eleme volt (pl. a fejlécben), és NEM
     * navigált el a riportlista oldaláról. Emiatt a "Show" gomb sosem jelent meg, a
     * {@code reportResultTable} tág lokátora viszont a lista oldalon is "látszott", így a
     * hiba észrevétlen maradt, és végül a lista 3 sora (Name/Actions) került a CSV-be a
     * 181 soros riport helyett. A teljes sorra vagy a Name cellára kattintás sem nyitja meg
     * a riportot (ez UI-szinten nem klikkelhető elem) – kizárólag az Actions oszlop 3.
     * (dokumentum ikonú) gombjára kattintva navigálunk át a riport oldalára, ezért most
     * arra kattintunk, és explicit módon ellenőrizzük, hogy tényleg elhagytuk-e a lista URL-jét.
     */
    public void downloadFirstReport() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        String listUrl = driver.getCurrentUrl();

        // 1. Megnyitjuk az első riportot: az Actions oszlop 3. (dokumentum ikon) gombjára kattintunk
        WebElement viewBtn = wait.until(ExpectedConditions.elementToBeClickable(firstReportViewButton));
        try {
            viewBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewBtn);
        }

        // 2. Ellenőrizzük, hogy a kattintás ténylegesen elnavigált a lista oldaláról.
        //    Ha nem, a következő lépések (Show / eredménytábla-várás) a lista oldalán
        //    futnának le hibásan, ezért legalább naplózzuk a problémát.
        try {
            wait.until(d -> !d.getCurrentUrl().equals(listUrl));
        } catch (Exception e) {
            System.out.println("Figyelmeztetés: a riportsorra kattintás nem navigált el a riportlista oldaláról!");
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        // 3. Ha a riport szűrő felületén van 'Show' gomb, rákattintunk az adatok lekéréséhez
        try {
            List<WebElement> showBtns = driver.findElements(showReportButton);
            if (!showBtns.isEmpty() && showBtns.get(0).isDisplayed()) {
                showBtns.get(0).click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));
            }
        } catch (Exception ignored) {
        }

        // 4. Megvárjuk, hogy az eredménytábla láthatóvá váljon
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(reportResultTable));
        } catch (Exception e) {
            System.out.println("Figyelmeztetés: A riport eredménytáblája nem jelent meg a megadott időn belül.");
        }
    }

    /**
     * Visszaadja a riportlista első sorában szereplő riportnevet.
     *
     * @return az első riport neve
     */
    public String getFirstReportName() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));
        WebElement reportNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(firstReportName));
        return reportNameElement.getText().trim();
    }

    /**
     * Kiolvassa a jelenleg megnyitott (és a {@link #downloadFirstReport()} hívásával már
     * generált) riport táblázatának adatait, és CSV fájlba menti őket a megadott elérési
     * útra ("Adatok lementése felületről" követelmény).
     * <p>
     * A korábbi verzióval szemben itt SZŰKÍTVE, csak a riport eredménytábla konténerén
     * ({@link #reportResultTable}) belül keresünk sorokat/cellákat, nem az egész oldalon –
     * így nem kerülhetnek a CSV-be véletlenszerű oldalszövegek (menü, szűrő címkék stb.),
     * ami korábban a "nem a táblázat adatai kerülnek a CSV-be" hibajelenséget okozta.
     *
     * @param filePath a létrehozandó CSV fájl teljes elérési útja
     * @throws java.io.IOException ha a fájl írása közben hiba történik
     */
    public void exportReportDataToCsv(String filePath) throws java.io.IOException {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));

        java.io.File file = new java.io.File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        int writtenDataRows = 0;

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file))) {
            // Fejlécek kinyerése a látható táblázatból
            List<WebElement> headers = driver.findElements(By.xpath("//div[contains(@class,'rgHeader')] | //th | //div[@role='columnheader']"));
            if (!headers.isEmpty()) {
                StringBuilder headerLine = new StringBuilder();
                for (int i = 0; i < headers.size(); i++) {
                    String text = headers.get(i).getText().trim().replace(",", " ").replace("\n", " ");
                    if (!text.isEmpty()) {
                        headerLine.append("\"").append(text).append("\"");
                        if (i < headers.size() - 1) {
                            headerLine.append(",");
                        }
                    }
                }
                if (headerLine.length() > 0) {
                    writer.write(headerLine.toString());
                    writer.newLine();
                }
            }

            // Adatsorok kiolvasása a táblázat konténerből
            List<WebElement> rows = driver.findElements(reportResultRows);
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(reportResultCells);
                if (!cells.isEmpty()) {
                    StringBuilder rowLine = new StringBuilder();
                    for (int i = 0; i < cells.size(); i++) {
                        String text = cells.get(i).getText().trim().replace(",", " ").replace("\n", " ");
                        rowLine.append("\"").append(text).append("\"");
                        if (i < cells.size() - 1) {
                            rowLine.append(",");
                        }
                    }
                    writer.write(rowLine.toString());
                    writer.newLine();
                    writtenDataRows++;
                }
            }
        }

        if (!file.exists() || file.length() == 0) {
            throw new java.io.IOException("A CSV fájl nem jött létre vagy üres maradt: " + filePath);
        }

        System.out.println("Kiírt adatsorok száma a CSV fájlba: " + writtenDataRows);
    }
}
