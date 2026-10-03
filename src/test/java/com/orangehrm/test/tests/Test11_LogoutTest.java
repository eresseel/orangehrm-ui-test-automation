package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Vizsgakövetelmény: <b>Kijelentkezés</b>.
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes.
 */
public class Test11_LogoutTest extends BaseTest {

    /**
     * <b>TC-11 – Kijelentkezés</b>
     * <p><b>Tesztfeltétel:</b> bejelentkezett felhasználó kijelentkezhet.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin.
     * <p><b>Tesztlépések:</b> belépés; a jobb felső felhasználói menü megnyitása; Logout.
     * <p><b>Elvárt eredmény:</b> az URL a login oldalra mutat.
     */
    @Test
    public void testKijelentkezes() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        // Megvárjuk a bejelentkezés végét, mielőtt a felhasználói menüt keressük
        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");
        dashboardPage.logout();
        Assertions.assertTrue(driver.getCurrentUrl().contains("login"), "A kijelentkezés sikertelen.");
    }
}
