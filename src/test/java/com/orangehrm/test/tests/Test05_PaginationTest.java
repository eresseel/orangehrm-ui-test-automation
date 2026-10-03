package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import com.orangehrm.test.pages.PimPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * "Több oldalas lista bejárása" vizsgakövetelmény tesztje: az Employee List
 * teljes lapozását végigjárja az első oldaltól az utolsóig.
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes; a PIM Employee List
 * adatai több oldalra férnek el.
 */
public class Test05_PaginationTest extends BaseTest {

    /**
     * <b>TC-05 – Többoldalas lista bejárása</b>
     * <p><b>Tesztfeltétel:</b> az Employee List lapozásával az utolsó oldalig eljutunk.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin.
     * <p><b>Tesztlépések:</b> belépés; PIM menü; Employee List; lapozás oldalról oldalra az utolsóig, az oldalak számolása.
     * <p><b>Elvárt eredmény:</b> a bejárt oldalak száma legalább 1, a bejárás hiba nélkül véget ér.
     */
    @Test
    public void testTobbOldalasListaBejarasa() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        PimPage pimPage = new PimPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");

        dashboardPage.navigateToPim();
        pimPage.navigateToEmployeeList();

        int visitedPages = pimPage.traverseAllPagesAndCount();
        System.out.println("Bejárt oldalak száma az Employee List-ben: " + visitedPages);

        Assertions.assertTrue(visitedPages >= 1,
                "A lista bejárása sikertelen volt, egyetlen oldal sem töltött be.");
    }
}
