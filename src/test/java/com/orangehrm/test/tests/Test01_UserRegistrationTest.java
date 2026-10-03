package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.AdminPage;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Vizsgakövetelmény: <b>Regisztráció</b>. Az OrangeHRM demo nem kínál nyilvános önregisztrációt,
 * ezért ennek megfelelője az, hogy bejelentkezett Admin új rendszerfelhasználót hoz létre.
 * <p><b>Előfeltételek:</b> a demo alkalmazás elérhető; az {@code Admin / admin123} belépés érvényes;
 * legalább egy alkalmazott létezik (az employee autocomplete-hez).
 */
public class Test01_UserRegistrationTest extends BaseTest {
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
    }

    /**
     * <b>TC-01 – Új felhasználó létrehozása és visszakeresése</b>
     * <p><b>Tesztfeltétel:</b> Admin új, egyedi nevű ESS / Enabled rendszerfelhasználót tud létrehozni.
     * <p><b>Előfeltétel:</b> a login oldal elérhető, az Admin fiók érvényes.
     * <p><b>Tesztlépések:</b>
     * <ol>
     *   <li>Bejelentkezés Adminként, a dashboard betöltésének megvárása.</li>
     *   <li>Az Add User oldal megnyitása.</li>
     *   <li>ESS szerepkör, Enabled státusz, az employee autocomplete első találata, egyedi
     *       felhasználónév ({@code Teszt_Elek_} + 4 karakter) és jelszó megadása, mentés.</li>
     *   <li>Navigálás a felhasználólistára és keresés az új felhasználónévre.</li>
     * </ol>
     * <p><b>Elvárt eredmény:</b> megjelenik a sikeres mentést jelző üzenet, és az új felhasználó
     * megtalálható a felhasználólistában.
     */
    @Test
    public void testUjFelhasznaloRegisztraciojaEsEllenorzese() {
        loginPage.login("Admin", "admin123");

        // Megvárjuk, hogy a bejelentkezés tényleg lefusson, mielőtt máshova navigálunk
        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");

        adminPage.openAddUserPage();

        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
        String uniqueUsername = "Teszt_Elek_" + uniqueSuffix;
        String defaultPassword = "Password123!";

        System.out.println("----------------------------------------");
        System.out.println("Létrehozott teszt felhasználó: " + uniqueUsername);
        System.out.println("----------------------------------------");

        adminPage.registerNewUser("a", uniqueUsername, defaultPassword);

        Assertions.assertTrue(adminPage.isUserCreatedSuccessfully(),
                "A sikeres mentést jelző Toast üzenet nem jelent meg!");

        driver.get(USER_LIST_URL);
        boolean isFound = adminPage.isUserPresentInList(uniqueUsername);
        Assertions.assertTrue(isFound,
                "A létrehozott felhasználó (" + uniqueUsername + ") nem található a felhasználói listában!");
    }
}
