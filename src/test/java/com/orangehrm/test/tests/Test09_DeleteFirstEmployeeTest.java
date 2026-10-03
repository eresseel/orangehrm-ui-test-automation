package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import com.orangehrm.test.pages.PimPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Vizsgakövetelmény: <b>Adat vagy adatok törlése</b>.
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes; az Employee List legalább
 * egy alkalmazottat tartalmaz. A teszt a megosztott demo adatait törli.
 */
public class Test09_DeleteFirstEmployeeTest extends BaseTest {

    /**
     * <b>TC-09 – Alkalmazott törlése</b>
     * <p><b>Tesztfeltétel:</b> az Employee List első alkalmazottja törölhető.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin, az Employee List nem üres.
     * <p><b>Tesztlépések:</b> belépés; PIM / Employee List; az első sor törlés ikonja; megerősítés a
     * „Yes, Delete” gombbal. A törölt név naplózásra kerül.
     * <p><b>Elvárt eredmény:</b> megjelenik a sikeres törlést jelző üzenet.
     */
    @Test
    public void testAdatTorlese() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        PimPage pimPage = new PimPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        // Megvárjuk a bejelentkezés végét, különben a menüre kattintás időtúllépéssel elszállhat
        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");
        dashboardPage.navigateToPim();
        pimPage.navigateToEmployeeList();

        String deletedUser = pimPage.deleteFirstEmployee();
        System.out.println("Törölt alkalmazott neve: '" + deletedUser + "'");

        boolean isDeleted = pimPage.isDeleteSuccessful();
        Assertions.assertTrue(isDeleted, "A törlési művelet nem volt sikeres!");
    }
}
