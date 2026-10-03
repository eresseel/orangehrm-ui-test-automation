package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import com.orangehrm.test.pages.PimPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Vizsgakövetelmény: <b>Meglévő adat módosítása</b>.
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes; az Employee List legalább
 * egy alkalmazottat tartalmaz. A teszt a megosztott demo adatait módosítja.
 */
public class Test08_EditEmployeeTest extends BaseTest {

    /**
     * <b>TC-08 – Meglévő alkalmazott módosítása</b>
     * <p><b>Tesztfeltétel:</b> egy meglévő alkalmazott keresztneve módosítható és menthető.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin, az Employee List nem üres.
     * <p><b>Tesztlépések:</b> belépés; PIM / Employee List; az első alkalmazott szerkesztése;
     * a keresztnév átírása {@code ModifiedName}-re; mentés. A régi név naplózásra kerül.
     * <p><b>Elvárt eredmény:</b> megjelenik a sikeres mentést jelző üzenet.
     */
    @Test
    public void testMeglevoAdatModositasa() {
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

        // Módosítás végrehajtása és a régi név kinyerése
        String updatedName = "ModifiedName";
        String previousName = pimPage.editFirstEmployee(updatedName);

        // Naplózás
        System.out.println("Módosítás megtörtént: '" + previousName + "' -> '" + updatedName + "'");

        // Ellenőrzés
        boolean isSaved = pimPage.isEmployeeSavedSuccessfully();
        Assertions.assertTrue(isSaved, "A meglévő adat módosítása és mentése nem sikerült!");
    }
}
