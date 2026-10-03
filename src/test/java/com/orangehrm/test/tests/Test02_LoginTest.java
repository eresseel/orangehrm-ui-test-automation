package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Vizsgakövetelmény: <b>Bejelentkezés</b> (pozitív és két negatív eset).
 * <p><b>Előfeltételek:</b> a demo alkalmazás login oldala elérhető; az {@code Admin / admin123}
 * belépés érvényes. A tesztek egymástól függetlenek, mindegyik új böngészőt kap.
 */
public class Test02_LoginTest extends BaseTest {

    /**
     * <b>TC-02.1 – Sikeres bejelentkezés</b>
     * <p><b>Tesztfeltétel:</b> érvényes adatokkal a belépés sikeres.
     * <p><b>Előfeltétel:</b> a login oldal elérhető.
     * <p><b>Tesztlépések:</b> login oldal megnyitása; {@code Admin} / {@code admin123} megadása; belépés.
     * <p><b>Elvárt eredmény:</b> a dashboard oldal betöltődik.
     */
    @Test
    @DisplayName("Sikeres bejelentkezés helyes adatokkal")
    public void testSikeresBejelentkezes() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(), "A bejelentkezés nem volt sikeres.");
    }

    /**
     * <b>TC-02.2 – Sikertelen bejelentkezés hibás jelszóval</b>
     * <p><b>Tesztfeltétel:</b> hibás jelszóval a belépés elutasításra kerül.
     * <p><b>Előfeltétel:</b> a login oldal elérhető.
     * <p><b>Tesztlépések:</b> login oldal megnyitása; {@code Admin} / {@code hibasJelszo123} megadása; belépés.
     * <p><b>Elvárt eredmény:</b> „Invalid credentials” hibaüzenet jelenik meg.
     */
    @Test
    @DisplayName("Sikertelen bejelentkezés hibás jelszóval")
    public void testSikertelenBejelentkezesHibasJelszoval() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "hibasJelszo123");

        String actualErrorMessage = loginPage.getErrorMessageText();
        Assertions.assertTrue(actualErrorMessage.contains("Invalid credentials"),
                "A várt 'Invalid credentials' hibaüzenet nem jelent meg.");
    }

    /**
     * <b>TC-02.3 – Sikertelen bejelentkezés hibás felhasználónévvel</b>
     * <p><b>Tesztfeltétel:</b> nem létező felhasználónévvel a belépés elutasításra kerül.
     * <p><b>Előfeltétel:</b> a login oldal elérhető.
     * <p><b>Tesztlépések:</b> login oldal megnyitása; {@code RosszAdmin} / {@code admin123} megadása; belépés.
     * <p><b>Elvárt eredmény:</b> „Invalid credentials” hibaüzenet jelenik meg.
     */
    @Test
    @DisplayName("Sikertelen bejelentkezés hibás felhasználónévvel")
    public void testSikertelenBejelentkezesHibasFelhasznalonevvel() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.navigateTo();
        loginPage.login("RosszAdmin", "admin123");

        String actualErrorMessage = loginPage.getErrorMessageText();
        Assertions.assertTrue(actualErrorMessage.contains("Invalid credentials"),
                "A várt 'Invalid credentials' hibaüzenet nem jelent meg.");
    }
}
