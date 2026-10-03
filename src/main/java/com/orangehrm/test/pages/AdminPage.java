package com.orangehrm.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object az OrangeHRM Admin / System Users moduljához.
 * Felelős új felhasználó létrehozásáért, a felhasználólista lekérdezéséért és a keresésért.
 */
public class AdminPage {
    private static final String ADD_USER_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/saveSystemUser";

    private final WebDriver driver;
    private final WebDriverWait wait;

    // A form-loader az OrangeHRM globális "töltés folyamatban" indikátora – amíg látszik,
    // az űrlap elemei még nem feltétlenül interakcióképesek, ezért sok metódus explicit
    // megvárja az eltűnését.
    private final By formLoader = By.className("oxd-form-loader");

    // Lista oldal: Add gomb (pontos szövegegyezés, nem illeszkedik a Search gombra)
    private final By addButton = By.xpath("//button[normalize-space()='Add']");
    private final By addUserHeader = By.xpath("//h6[normalize-space()='Add User']");

    // A dropdownok lokátorai a mezőcímke (label) szövegéből indulnak ki, és onnan navigálnak
    // a hozzá tartozó select-elemhez – ez stabilabb, mint egy generált CSS class-ra hagyatkozni,
    // mert a label szövege ritkábban változik, mint a belső implementáció.
    private final By userRoleDropdown = By.xpath("//label[text()='User Role']/parent::div/following-sibling::div//div[contains(@class, 'oxd-select-text')]");
    private final By statusDropdown = By.xpath("//label[text()='Status']/parent::div/following-sibling::div//div[contains(@class, 'oxd-select-text')]");

    private final By essOption = By.xpath("//div[@role='option']//span[text()='ESS']");
    private final By enabledOption = By.xpath("//div[@role='option']//span[text()='Enabled']");

    private final By employeeNameInput = By.xpath("//input[@placeholder='Type for hints...']");
    // Az autocomplete javaslatlistából kiszűrjük a "Searching..." és "No Records" pszeudo-opciókat,
    // hogy ne próbáljunk rájuk kattintani, csak a valódi találatokra.
    private final By employeeSuggestion = By.xpath(
            "//div[@role='listbox']//div[@role='option'][not(contains(., 'Searching')) and not(contains(., 'No Records'))]");
    private final By usernameInput = By.xpath("//label[text()='Username']/parent::div/following-sibling::div//input");

    // Label-független lokátorok: az Add User űrlapon pontosan két password típusú input van
    // (Password és Confirm Password) – ezek sorrend alapján, indexeléssel azonosíthatók
    // megbízhatóan, mert a type="password" attribútum stabilabb, mint a label-szöveg egyezés.
    private final By passwordInput = By.xpath("(//input[@type='password'])[1]");
    private final By confirmPasswordInput = By.xpath("(//input[@type='password'])[2]");

    private final By saveButton = By.xpath("//button[@type='submit']");
    private final By successToast = By.xpath("//div[contains(@class, 'oxd-toast--success')]");

    private final By searchUsernameInput = By.xpath("//label[text()='Username']/parent::div/following-sibling::div//input");
    private final By searchButton = By.xpath("//button[@type='submit'][contains(., 'Search')]");

    private final By tableCard = By.xpath("//div[contains(@class, 'oxd-table-card')]");
    // A második cella (index [2]) tartalmazza a felhasználónevet az első táblázatsorban.
    private final By firstUsernameLocator = By.xpath("//div[@class='oxd-table-body']/div[1]//div[@role='cell'][2]");

    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * A form-loader eltűnésére vár, de nem dob hibát, ha ez időtúllépéssel jár –
     * néhány oldalon a loader villanásszerűen jelenik meg, és előfordulhat, hogy
     * mire a wait elindulna, már el is tűnt, ami félrevezető TimeoutException-t okozna.
     */
    private void waitForLoaderToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(formLoader));
        } catch (Exception ignored) {
        }
    }

    /**
     * Közvetlenül az Add User oldalra navigál, megvárja a fejlécet és a betöltés végét.
     */
    public void openAddUserPage() {
        driver.get(ADD_USER_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(addUserHeader));
        waitForLoaderToDisappear();
    }

    /**
     * Alternatív útvonal: a felhasználólistáról az Add gombbal nyitja meg az űrlapot.
     */
    public void clickAddUser() {
        waitForLoaderToDisappear();

        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(addButton));
        try {
            addBtn.click();
        } catch (Exception e) {
            // Tartalék: ha a natív kattintás nem éri el a gombot (pl. overlay vagy scroll miatt),
            // JavaScript-tel közvetlenül kiváltjuk a click eseményt.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
        }

        // Megvárjuk, hogy tényleg az Add User űrlap töltött be
        wait.until(ExpectedConditions.visibilityOfElementLocated(addUserHeader));
        waitForLoaderToDisappear();
    }

    /**
     * Kitölti és elmenti az Add User űrlapot: User Role = ESS, Status = Enabled,
     * az employee autocomplete első találata, valamint a megadott felhasználónév és jelszó.
     *
     * @param employeeHint    az employee autocomplete mezőbe beírandó részszöveg (pl. "a")
     * @param newUsername     az új felhasználó neve
     * @param newPassword     az új felhasználó jelszava (a megerősítő mezőbe is ez kerül)
     */
    public void registerNewUser(String employeeHint, String newUsername, String newPassword) {
        // A dropdownok kattintásra nyílnak meg, majd az opciólistából választunk – ez a
        // szokásos OrangeHRM "custom select" minta, nem natív HTML <select>.
        wait.until(ExpectedConditions.elementToBeClickable(userRoleDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(essOption)).click();

        wait.until(ExpectedConditions.elementToBeClickable(statusDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(enabledOption)).click();

        // Az employee mező autocomplete: beírunk egy részleges szöveget, majd megvárjuk
        // és kiválasztjuk az első valódi (nem "Searching"/"No Records") találatot.
        WebElement empInput = wait.until(ExpectedConditions.visibilityOfElementLocated(employeeNameInput));
        empInput.sendKeys(employeeHint);
        wait.until(ExpectedConditions.elementToBeClickable(employeeSuggestion)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).sendKeys(newUsername);

        WebElement passwordField;
        try {
            passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        } catch (TimeoutException e) {
            // Diagnosztika: kiírjuk, mit lát valójában a Selenium az oldalon – ez segít
            // eldönteni, hogy a lokátor hibás-e, vagy az oldal állapota tér el a várttól.
            logInputsOnPage();
            throw e;
        }
        passwordField.sendKeys(newPassword);

        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPasswordInput)).sendKeys(newPassword);

        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    /**
     * Ellenőrzi, hogy a mentés után megjelenik-e a sikeres létrehozást jelző toast üzenet.
     *
     * @return {@code true}, ha a sikeres mentést jelző toast látható
     */
    public boolean isUserCreatedSuccessfully() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successToast)).isDisplayed();
    }

    /**
     * A felhasználólistán rákeres a megadott felhasználónévre, és ellenőrzi, hogy megjelenik-e.
     *
     * @param username a keresett felhasználónév
     * @return {@code true}, ha a felhasználó megtalálható a szűrt listában
     */
    public boolean isUserPresentInList(String username) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchUsernameInput));
        // clear() + sendKeys(): biztosítjuk, hogy a mezőben ne maradjon korábbi keresési szöveg,
        // mielőtt beírnánk az új értéket (pl. ha ugyanaz a mező kétszer kerül felhasználásra).
        searchInput.clear();
        searchInput.sendKeys(username);

        waitForLoaderToDisappear();
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();

        // A felhasználónevet tartalmazó cellát dinamikusan, a paraméterként kapott username
        // alapján építjük fel, hogy pontosan az adott felhasználót keressük, ne csak "valamit".
        By userCell = By.xpath("//div[contains(@class, 'oxd-table-card')]//div[text()='" + username + "']");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(userCell)).isDisplayed();
    }

    /**
     * Visszaadja a felhasználólista első sorában szereplő felhasználónevet.
     *
     * @return az első sor felhasználóneve
     */
    public String getFirstUsername() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(tableCard));
        WebElement firstUserElement = wait.until(ExpectedConditions.visibilityOfElementLocated(firstUsernameLocator));
        return firstUserElement.getText();
    }

    /**
     * Ellenőrzi, hogy a felhasználólista táblázata megjelent-e (van-e legalább egy adatsor).
     *
     * @return {@code true}, ha a táblázat látható
     */
    public boolean hasTableData() {
        waitForLoaderToDisappear();
        // Ennél a metódusnál hosszabb (30 mp) várakozást használunk az alapértelmezett 15 mp
        // helyett, mert a felhasználólista betöltése (szerveroldali lapozás/szűrés) néha
        // lassabb, mint a többi, egyszerűbb oldalművelet.
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            return longWait.until(ExpectedConditions.visibilityOfElementLocated(tableCard)).isDisplayed();
        } catch (TimeoutException e) {
            System.out.println("DIAGNOSZTIKA URL: " + driver.getCurrentUrl() + " | cím: " + driver.getTitle());
            throw e;
        }
    }

    /**
     * Csak hibakereséshez: kiírja az aktuális URL-t és az oldalon lévő input mezőket.
     * Ha a teszt stabilan lefut, ez és a hívása törölhető.
     */
    private void logInputsOnPage() {
        System.out.println("---- DIAGNOSZTIKA ----");
        System.out.println("URL: " + driver.getCurrentUrl());
        for (WebElement input : driver.findElements(By.tagName("input"))) {
            System.out.println(input.getAttribute("type") + " | "
                    + input.getAttribute("name") + " | "
                    + input.getAttribute("class"));
        }
        System.out.println("----------------------");
    }
}
