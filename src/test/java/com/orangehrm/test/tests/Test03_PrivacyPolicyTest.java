package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.LoginPage;
import com.orangehrm.test.pages.PrivacyPolicyPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Vizsgakövetelmény: <b>Adatkezelési nyilatkozat használata</b>.
 * <p><b>Előfeltételek:</b> a demo login oldal elérhető (bejelentkezés nem szükséges); a lábléc
 * orangehrm.com linkje új ablakban nyílik meg.
 */
public class Test03_PrivacyPolicyTest extends BaseTest {
    private PrivacyPolicyPage privacyPolicyPage;

    // A BaseTest @BeforeEach metódusa (driver létrehozása) ez előtt fut le
    @BeforeEach
    public void setUpPage() {
        privacyPolicyPage = new PrivacyPolicyPage(driver);
        new LoginPage(driver).navigateTo();
    }

    /**
     * <b>TC-03 – Adatkezelési nyilatkozat megnyitása</b>
     * <p><b>Tesztfeltétel:</b> a login oldal láblécéből elérhető az adatkezelési nyilatkozat.
     * <p><b>Előfeltétel:</b> a login oldal megnyitva.
     * <p><b>Tesztlépések:</b>
     * <ol>
     *   <li>Kattintás a láblécben lévő orangehrm.com linkre.</li>
     *   <li>Átváltás az újonnan megnyílt ablakra.</li>
     *   <li>A sütik elfogadása után a Privacy Policy oldal megnyitása.</li>
     * </ol>
     * <p><b>Elvárt eredmény:</b> az aktuális URL a privacy-policy oldalra mutat.
     */
    @Test
    public void testAdatkezelesiNyilatkozatMegtekinteseNavigacioval() {
        privacyPolicyPage.clickLoginFooterLink();
        privacyPolicyPage.switchToNewWindow();
        privacyPolicyPage.clickPrivacyPolicyOnMainPage();

        // Ellenőrzés és 5 másodperces időzés a betöltött oldalon
        boolean isCorrectUrl = privacyPolicyPage.isPrivacyPolicyUrlCorrectAndWait();
        Assertions.assertTrue(isCorrectUrl, "Az adatkezelési nyilatkozat (privacy-policy) oldala nem töltődött be!");
    }
}
