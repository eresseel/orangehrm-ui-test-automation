package com.orangehrm.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object az OrangeHRM bejelentkezési oldalához.
 * Felelős a belépéshez szükséges mezők kitöltéséért, a hibaüzenet kiolvasásáért,
 * valamint a lábléc adatkezelési linkjének eléréséért.
 */
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // A name attribútum a legstabilabb lokátor a bejelentkezési mezőkhöz, mert ez HTML
    // form-szemantika, nem generált CSS class, így kevésbé valószínű, hogy egy UI-frissítés
    // megváltoztatja.
    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.xpath("//button[@type='submit']");
    private final By privacyLink = By.xpath("//a[contains(@href, 'orangehrm.com')]");

    // Frissített, rugalmasabb XPath a hibaüzenet elemére: két alternatív mintát fogad el
    // ("|" OR-kapcsolat), mert az OrangeHRM UI verziófüggően kicsit eltérő DOM-struktúrát
    // használhat a hibaüzenet megjelenítésére.
    private final By errorMessageAlert = By.xpath("//p[contains(@class, 'oxd-alert-content-text')] | //div[contains(@class, 'oxd-alert')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Megnyitja a bejelentkezési oldalt.
     */
    public void navigateTo() {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }

    /**
     * Kitölti a felhasználónév és jelszó mezőt, majd rákattint a bejelentkezés gombra.
     *
     * @param username a felhasználónév
     * @param password a jelszó
     */
    public void login(String username, String password) {
        // clear() minden mezőnél kötelező lépés, mert a böngésző (pl. autofill) esetenként
        // előre kitöltheti a mezőket – enélkül a beírt szöveg a meglévő tartalom mögé kerülne.
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput)).clear();
        driver.findElement(usernameInput).sendKeys(username);

        WebElement passwordElement = driver.findElement(passwordInput);
        passwordElement.clear();
        passwordElement.sendKeys(password);

        driver.findElement(loginButton).click();
    }

    /**
     * A login oldal láblécében található adatkezelési (orangehrm.com) linkre kattint.
     */
    public void clickPrivacyPolicy() {
        wait.until(ExpectedConditions.elementToBeClickable(privacyLink)).click();
    }

    /**
     * Visszaadja a sikertelen bejelentkezés után megjelenő hibaüzenet szövegét.
     *
     * @return a hibaüzenet szövege (pl. "Invalid credentials")
     */
    public String getErrorMessageText() {
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageAlert));
        return alert.getText();
    }
}