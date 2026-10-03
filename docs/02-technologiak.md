# Technológiák

| Eszköz | Szerepe |
|---|---|
| Java 17 | a projekt 17-es targetre fordul |
| Maven + Surefire 3.2.5 | build és tesztfuttatás |
| Maven Wrapper (`mvnw`) | Maven telepítés nélküli futtatás |
| Selenium WebDriver 4.18.1 | böngészővezérlés (Chrome) |
| JUnit 5 (Jupiter, Params) | tesztek, paraméterezett (adatvezérelt) tesztek |
| WebDriverManager | a chromedriver automatikus beállítása |
| Docker | izolált, reprodukálható tesztfuttatási környezet |

A tesztek explicit várakozásokat (`WebDriverWait` / `ExpectedConditions`) használnak,
implicit wait nincs.

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
