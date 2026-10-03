package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.AdminPage;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Vizsgakövetelmény: <b>Adatok listázása</b> (Admin / System Users lista).
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes; a rendszerfelhasználók
 * listája legalább egy sort tartalmaz.
 */
public class Test04_ListEmployeesTest extends BaseTest {
    private static final String USER_LIST_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";

    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private AdminPage adminPage;

    // A BaseTest @BeforeEach metódusa (driver létrehozása) ez előtt fut le
    @BeforeEach
    public void setUpPages() {
        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
        adminPage = new AdminPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        // Megvárjuk a bejelentkezés végét, különben a login átirányítása felülírhatja a következő navigációt
        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");

        // Navigáció az Admin modulba
        driver.get(USER_LIST_URL);
    }

    /**
     * <b>TC-04 – Felhasználólista listázása, az első sor kiolvasása</b>
     * <p><b>Tesztfeltétel:</b> a System Users lista adatsorokat jelenít meg.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin, a felhasználólista oldal megnyitva.
     * <p><b>Tesztlépések:</b> a táblázat adatsorainak megvárása; az első sor felhasználónevének kiolvasása és kiírása.
     * <p><b>Elvárt eredmény:</b> a táblázatban van adatsor, és az első felhasználónév nem üres.
     */
    @Test
    public void testAdatokListazasaEsElsoUserKinyerese() {
        // Ellenőrizzük, hogy van-e adatsor a táblázatban
        Assertions.assertTrue(adminPage.hasTableData(), "A táblázatban nem találhatók adatok!");

        // Az első sor felhasználónevét a page osztály metódusa olvassa ki
        String firstUser = adminPage.getFirstUsername();

        System.out.println("Az első sorban talált felhasználónév: " + firstUser);

        // Ellenőrizzük, hogy a kinyert érték nem üres
        Assertions.assertNotNull(firstUser, "A felhasználónév nem lehet null!");
        Assertions.assertFalse(firstUser.trim().isEmpty(), "A felhasználónév nem lehet üres!");
    }
}
