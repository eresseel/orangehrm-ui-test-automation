package com.orangehrm.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Az összes UI teszt közös ősosztálya (keretrendszer-funkciók).
 * <ul>
 *   <li>Minden teszt előtt létrehozza a Chrome {@link WebDriver}-t (WebDriverManager gondoskodik a driverről).</li>
 *   <li>Beállítja és kiüríti a letöltési mappát ({@code target/downloads}).</li>
 *   <li>Headless módba kapcsol, ha a {@code GITHUB_ACTIONS}, a {@code DOCKER_CONTAINER} vagy a
 *       {@code HEADLESS=true} környezeti változó be van állítva (CI és Docker futtatás).</li>
 *   <li>Minden teszt után bezárja a böngészőt.</li>
 * </ul>
 */
public class BaseTest {
    protected WebDriver driver;
    protected static final String DOWNLOAD_DIR = System.getProperty("user.dir") + File.separator + "target" + File.separator + "downloads";

    /** Teszt előtti előkészítés: letöltési mappa tisztítása, Chrome indítása a környezetnek megfelelő beállításokkal. */
    @BeforeEach
    public void setupDriver() {
        File downloadFolder = new File(DOWNLOAD_DIR);
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        } else {
            File[] files = downloadFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_DIR);
        prefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", prefs);

        if (isHeadlessEnvironment()) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");

            // Fix ablakméret kényszerítése headless módban
            options.addArguments("--window-size=1920,1080");

            // Normál böngésző User-Agent szimulálása a headless megkülönböztetés elkerülésére
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            options.addArguments("--lang=en-US");
        }

        driver = new ChromeDriver(options);

        // FONTOS: A maximize() csak akkor fusson, ha NEM headless módban vagyunk!
        // Headless módban a maximize() elronthatja a --window-size=1920,1080 beállítást.
        if (!isHeadlessEnvironment()) {
            driver.manage().window().maximize();
        }
    }

    /** @return {@code true}, ha a teszt CI-ben, Dockerben vagy kézzel kért headless módban fut */
    private boolean isHeadlessEnvironment() {
        boolean isGithubActions = System.getenv("GITHUB_ACTIONS") != null;
        boolean isDocker = System.getenv("DOCKER_CONTAINER") != null;
        boolean isManualHeadless = "true".equalsIgnoreCase(System.getenv("HEADLESS"));
        return isGithubActions || isDocker || isManualHeadless;
    }

    /** Teszt utáni takarítás: a böngésző bezárása. */
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
