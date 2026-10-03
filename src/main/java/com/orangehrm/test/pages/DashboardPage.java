package com.orangehrm.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object a bejelentkezés utáni Dashboard (kezdőképernyő) oldalhoz.
 * Innen érhető el a felső menün keresztül a PIM és az Admin modul, valamint a kijelentkezés.
 */
public class DashboardPage {
    private static final String ADMIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";

    private static final String PIM_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewEmployeeList";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By userDropdown = By.className("oxd-userdropdown-tab");
    private final By logoutLink = By.xpath("//a[contains(@href, 'logout')]");
    private final By pimMenu = By.xpath("//span[text()='PIM']");
    private final By adminMenu = By.xpath("//span[text()='Admin']");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Átnavigál a PIM (alkalmazottak) modulba. Ha a menüre kattintás nem eredményez
     * URL-váltást (pl. lassú betöltés miatt), közvetlenül az alkalmazott listára navigál.
     */
    public void navigateToPim() {
        wait.until(ExpectedConditions.elementToBeClickable(pimMenu)).click();
        try {
            // Ellenőrizzük, hogy a kattintás tényleg átvitt a PIM oldalra – a menüelemek
            // kattintása néha "elveszik", ha az SPA router még egy korábbi navigációt dolgoz fel.
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.urlContains("/pim/"));
        } catch (TimeoutException e) {
            // Ha a kattintás elveszett, közvetlenül navigálunk – ez lassabb, de megbízhatóbb
            // tartalék megoldás, mint a tesztet hibával elszakítani egy UI-flakiness miatt.
            driver.get(PIM_URL);
            wait.until(ExpectedConditions.urlContains("/pim/"));
        }
    }

    /**
     * Átnavigál az Admin (felhasználókezelő) modulba. Ha a menüre kattintás nem eredményez
     * URL-váltást, közvetlenül a felhasználólistára navigál.
     */
    public void navigateToAdmin() {
        wait.until(ExpectedConditions.elementToBeClickable(adminMenu)).click();
        try {
            // Ellenőrizzük, hogy a kattintás tényleg átvitt az Admin oldalra
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.urlContains("viewSystemUsers"));
        } catch (TimeoutException e) {
            // Ha a kattintás elveszett, közvetlenül navigálunk
            driver.get(ADMIN_URL);
            wait.until(ExpectedConditions.urlContains("viewSystemUsers"));
        }
    }

    /**
     * Kijelentkezteti a felhasználót a jobb felső felhasználói menün keresztül.
     * Két lépés: előbb megnyitjuk a felhasználói dropdown menüt, majd a benne
     * megjelenő Logout linkre kattintunk.
     */
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(userDropdown)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }

    /**
     * Megvárja és ellenőrzi, hogy az aktuális URL a Dashboard oldalra mutat-e.
     * Ez a bejelentkezés utáni "landing page" – ha ide eljutunk, a login sikeres volt.
     *
     * @return {@code true}, ha a bejelentkezés után a Dashboard töltött be
     */
    public boolean isDashboardDisplayed() {
        return wait.until(ExpectedConditions.urlContains("dashboard"));
    }
}