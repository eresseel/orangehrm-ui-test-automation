package com.orangehrm.test.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object az adatkezelési nyilatkozat (Privacy Policy) megtekintéséhez.
 * A login oldal láblécén keresztül az orangehrm.com weboldal új ablakban nyílik meg,
 * ahonnan a Privacy Policy oldal érhető el.
 */
public class PrivacyPolicyPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By loginFooterLink = By.xpath("//a[contains(@href, 'orangehrm.com')]");
    // Több lehetséges "cookie elfogadom" gomb azonosítóját is elfogadjuk, mert a
    // marketing/CMP (cookie consent) szolgáltató időnként változhat a cél oldalon.
    private final By acceptCookiesButton = By.xpath("//button[contains(@id, 'hs-eu-confirmation-button') or contains(@id, 'onetrust-accept-btn-handler') or contains(text(), 'Accept')]");
    private final By mainPagePrivacyPolicyLink = By.xpath("//footer//a[contains(@href, 'privacy-policy') or contains(text(), 'Privacy Policy')]");

    public PrivacyPolicyPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * A login oldal láblécében lévő orangehrm.com linkre kattint (görgetéssel és
     * JavaScript-alapú tartalék kattintással, ha a natív kattintás nem éri el az elemet).
     */
    public void clickLoginFooterLink() {
        // presenceOfElementLocated (nem visibility): a lábléc linkje a viewporton kívül is
        // jelen lehet a DOM-ban, ezért előbb csak a jelenlétét várjuk meg, aztán görgetünk hozzá.
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(loginFooterLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        wait.until(ExpectedConditions.elementToBeClickable(link));

        try {
            link.click();
        } catch (Exception e) {
            // Tartalék: ha egy másik elem (pl. sticky header) eltakarja, JS-kattintással
            // kényszerítjük ki a kattintás eseményt, mivel az a DOM-on hat, nem a képernyő-koordinátán.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
    }

    /**
     * Átvált a linkre kattintás után megnyíló új böngészőablakra/fülre.
     */
    public void switchToNewWindow() {
        String originalWindow = driver.getWindowHandle();
        // Megvárjuk, hogy ténylegesen két ablak/fül legyen nyitva, mielőtt váltanánk –
        // enélkül race condition alakulhatna ki, ha az új ablak megnyitása lassabb, mint a kód futása.
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        for (String windowHandle : driver.getWindowHandles()) {
            if (!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    /**
     * A fő orangehrm.com oldalon elfogadja a cookie-bannert (ha megjelenik), majd
     * megnyitja a lábléc Privacy Policy linkjét.
     */
    public void clickPrivacyPolicyOnMainPage() {
        try {
            // Rövid (4 mp) várakozás a cookie-bannerre: ha nem jelenik meg, nem akarunk
            // feleslegesen sokáig várni rá, mert lehet, hogy a látogató már korábban elfogadta.
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
            WebElement cookieBtn = shortWait.until(ExpectedConditions.elementToBeClickable(acceptCookiesButton));
            cookieBtn.click();
        } catch (Exception ignored) {
            // Ha a banner gombja nem kattintható a megadott időn belül, tartalékként megpróbáljuk
            // közvetlenül elrejteni a banner konténerét, hogy ne takarja el a további elemeket.
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "var elem = document.getElementById('hs-eu-cookie-confirmation'); if(elem) elem.style.display='none';"
                );
            } catch (Exception e) {
            }
        }

        WebElement privacyLink = wait.until(ExpectedConditions.presenceOfElementLocated(mainPagePrivacyPolicyLink));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", privacyLink);

        // A href attribútum alapján közvetlen navigációt preferáljuk a kattintás helyett,
        // mert ez megbízhatóbb: nem függ attól, hogy a link fizikailag kattintható-e éppen
        // (pl. animáció vagy sticky elem takarhatja).
        String targetHref = privacyLink.getAttribute("href");
        if (targetHref != null && !targetHref.isEmpty()) {
            driver.get(targetHref);
        } else {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", privacyLink);
        }
    }

    /**
     * Ellenőrzi, hogy az aktuális URL a privacy-policy oldalra mutat, majd 5 másodpercig
     * az oldalon marad, hogy a betöltés vizuálisan is látható legyen.
     *
     * @return {@code true}, ha az URL tartalmazza a "privacy-policy" szöveget
     */
    public boolean isPrivacyPolicyUrlCorrectAndWait() {
        boolean isCorrect = wait.until(ExpectedConditions.urlContains("privacy-policy"));

        if (isCorrect) {
            try {
                // 5 másodperces várakozás a tényleges policy oldalon – ez kizárólag demonstrációs
                // célú (hogy manuális megfigyeléskor is látható legyen az eredmény), a teszt
                // logikájának szempontjából nem szükséges.
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return isCorrect;
    }
}