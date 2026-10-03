# Vizsgakövetelmények lefedettsége

| # | Vizsgakövetelmény | Teszt | Megvalósítás |
|---|---|---|---|
| 1 | Regisztráció | `Test01_UserRegistrationTest` | Bejelentkezés Adminként, az Add User oldal megnyitása (`admin/saveSystemUser`). Kiválasztja az `ESS` szerepkört és az `Enabled` státuszt, az employee autocomplete első találatát, majd megadja az egyedi felhasználónevet (`Teszt_Elek_` + 4 karakter) és a jelszót. Ellenőrzi a sikeres mentést jelző üzenetet, majd a felhasználólistában rákeres az új felhasználóra. |
| 2 | Bejelentkezés | `Test02_LoginTest` | Sikeres belépés (dashboard betölt), hibás jelszó és hibás felhasználónév (mindkettőnél `Invalid credentials` hibaüzenet). |
| 3 | Adatkezelési nyilatkozat használata | `Test03_PrivacyPolicyTest` | A login oldal láblécében lévő orangehrm.com linkre kattint, átvált az új ablakra, elfogadja a sütiket, megnyitja a Privacy Policy oldalt, és ellenőrzi az URL-t. |
| 4 | Adatok listázása | `Test04_ListEmployeesTest` | Az Admin / System Users oldalon ellenőrzi, hogy van adatsor a táblázatban, kiolvassa és kiírja az első sor felhasználónevét. |
| 5 | Több oldalas lista bejárása | `Test05_PaginationTest` | A PIM / Employee List teljes lapozását bejárja az első oldaltól az utolsóig (`PimPage.traverseAllPagesAndCount()`), és naplózza a bejárt oldalak számát. |
| 6 | Új adat bevitel | `Test06_07_DataDrivenAddEmployeeTest` | Új alkalmazott felvétele a PIM modulban, majd a mentés ellenőrzése. |
| 7 | Ismételt és sorozatos adatbevitel adatforrásból | `Test06_07_DataDrivenAddEmployeeTest` | `@CsvFileSource` a `testdata.csv` minden sorára (John Doe, Jane Smith, Robert Johnson) lefuttatja ugyanazt a felviteli és ellenőrzési logikát. |
| 8 | Meglévő adat módosítása | `Test08_EditEmployeeTest` | Az Employee Listben az első alkalmazott szerkesztése: a keresztnevet `ModifiedName`-re cseréli, menti, és ellenőrzi a sikeres mentést. A régi nevet naplózza. |
| 9 | Adat vagy adatok törlése | `Test09_DeleteFirstEmployeeTest` | Az első alkalmazott törlése a megerősítő ablakkal (`Yes, Delete`), majd a sikeres törlést jelző üzenet ellenőrzése. |
| 10 | Adatok lementése felületről | `Test10_DownloadReportsTest` | PIM / Reports, az első riport megnyitása, a kritérium-űrlap "Show" gombjával a riport tényleges legenerálása, majd a megjelenő táblázat adatainak kiírása CSV-be: `target/downloads/employee_report.csv`. Ellenőrzi, hogy a fájl létrejött és nem üres. |
| 11 | Kijelentkezés | `Test11_LogoutTest` | A felhasználó menüből a Logout, majd ellenőrzi, hogy az URL a login oldalra mutat. |

A tesztek dokumentációja a forráskódban Javadoc (`/** ... */`) kommentekkel készült:

- **Tesztosztályok** (kötelező): minden osztályon szerepel, mit tesztel, és mik az előfeltételei.
- **Tesztmetódusok** (kötelező): minden metóduson teszteset-leírás van: TC azonosító
  (`TC-01` … `TC-11`), tesztfeltétel, előfeltétel, tesztlépések, elvárt eredmény.
- **Page osztályok és metódusaik** (ajánlott): mit modellez az osztály, mit tud a metódus.
- **`BaseTest`** (ajánlott): a keretrendszer-funkciók (driver, letöltési mappa, headless mód).

Ezt kiegészítik a soronkénti/blokkonkénti `//` kommentek, amelyek nemcsak azt írják le, mi történik, hanem azt is, miért az
adott megoldás született (pl. miért van tartalék JS-kattintás, miért nem elég egy sima wait).

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
