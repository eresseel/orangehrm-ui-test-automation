package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import com.orangehrm.test.pages.PimPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Vizsgakövetelmények: <b>Új adat bevitel</b> és <b>ismételt, sorozatos adatbevitel adatforrásból</b>.
 * Az adatforrás a {@code src/test/resources/testdata.csv}; minden sorára lefut ugyanaz a teszt.
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes; a CSV fejlécet és
 * adatsorokat tartalmaz (FirstName, LastName, EmpId). Az EmpId-t az alkalmazás automatikusan
 * generálja, ezért a teszt nem viszi be külön mezőbe.
 */
public class Test06_07_DataDrivenAddEmployeeTest extends BaseTest {

    /**
     * <b>TC-06/07 – Új alkalmazott felvétele, CSV-sorokra ismételve</b>
     * <p><b>Tesztfeltétel:</b> a PIM modulban új alkalmazott vihető fel a CSV minden sorának adataival.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin; a CSV adatsorai: John Doe, Jane Smith, Robert Johnson.
     * <p><b>Tesztlépések:</b> belépés; PIM menü; Add Employee: keresztnév és vezetéknév kitöltése, mentés.
     * <p><b>Elvárt eredmény:</b> mindhárom adatsornál megjelenik a sikeres mentést jelző üzenet.
     *
     * @param firstName az alkalmazott keresztneve (CSV)
     * @param lastName  az alkalmazott vezetékneve (CSV)
     * @param empId     a CSV EmpId oszlopa (a tesztben nem kerül bevitelre)
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/testdata.csv", numLinesToSkip = 1)
    public void testIsmeteltEsSorozatosAdatbevitel(String firstName, String lastName, String empId) {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        PimPage pimPage = new PimPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        // Megvárjuk, hogy a bejelentkezés tényleg lefusson, mielőtt tovább lépünk
        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");

        dashboardPage.navigateToPim();

        // 1. Új adat bevitele
        pimPage.addEmployee(firstName, lastName);

        // 2. Ellenőrzés (Assert) hozzáadása a sikeres mentés igazolására
        boolean isSaved = pimPage.isEmployeeSavedSuccessfully();
        Assertions.assertTrue(isSaved, "Az új adat (" + firstName + " " + lastName + ") bevitele nem volt sikeres!");
    }
}
