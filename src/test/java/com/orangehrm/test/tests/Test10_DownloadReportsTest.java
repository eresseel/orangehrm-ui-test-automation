package com.orangehrm.test.tests;

import com.orangehrm.test.BaseTest;
import com.orangehrm.test.pages.DashboardPage;
import com.orangehrm.test.pages.LoginPage;
import com.orangehrm.test.pages.PimPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * Vizsgakövetelmény: <b>Adatok lementése felületről</b>. A PIM riport táblázatának adatait CSV
 * fájlba exportálja a {@code target/downloads} mappába.
 * <p><b>Előfeltételek:</b> az {@code Admin / admin123} belépés érvényes; a PIM / Reports alatt
 * legalább egy riport létezik.
 */
public class Test10_DownloadReportsTest extends BaseTest {

    /**
     * <b>TC-10 – Riport adatainak lementése CSV-be</b>
     * <p><b>Tesztfeltétel:</b> a felületen megjelenő riportadatok fájlba menthetők.
     * <p><b>Előfeltétel:</b> bejelentkezett Admin; a PIM / Reports oldal elérhető.
     * <p><b>Tesztlépések:</b> belépés; PIM / Reports megnyitása; az első riport megnyitása és legenerálása
     * (Show); a táblázat adatainak kiírása a {@code target/downloads/employee_report.csv} fájlba.
     * <p><b>Elvárt eredmény:</b> a CSV fájl létrejön és nem üres.
     *
     * @throws IOException ha a CSV fájl írása nem sikerül
     */
    @Test
    public void testAdatokLementeseFeluletrol() throws IOException {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        PimPage pimPage = new PimPage(driver);

        loginPage.navigateTo();
        loginPage.login("Admin", "admin123");

        // Megvárjuk a bejelentkezés végét, különben a menüre kattintás időtúllépéssel elszállhat
        Assertions.assertTrue(dashboardPage.isDashboardDisplayed(),
                "A bejelentkezés után nem a dashboard töltött be!");
        dashboardPage.navigateToPim();

        // 1. Riportok felület megnyitása
        boolean isReportAvailable = pimPage.isReportSectionAvailable();
        Assertions.assertTrue(isReportAvailable, "A riport felület nem érhető el.");

        // 2. Exportálandó riport nevének kiolvasása
        String reportName = pimPage.getFirstReportName();
        System.out.println("Exportálásra kijelölt riport neve: " + reportName);

        // 3. Riport táblázat megnyitása az ikonra kattintva
        pimPage.downloadFirstReport();

        // 4. Táblázat adatainak kigyűjtése és exportálása CSV fájlba oldalanként
        String csvPath = DOWNLOAD_DIR + "/employee_report.csv";
        pimPage.exportReportDataToCsv(csvPath);

        // 5. Ellenőrzés: Létrejött-e a CSV fájl és nem üres-e
        File downloadedFile = new File(csvPath);

        Assertions.assertTrue(downloadedFile.exists() && downloadedFile.length() > 0,
                "A(z) '" + reportName + "' riport adatai nem kerültek exportálásra a CSV fájlba!");

        System.out.println("Sikeres CSV export a target/downloads mappába: " + downloadedFile.getName() + " (Méret: " + downloadedFile.length() + " bájt)");
    }
}
