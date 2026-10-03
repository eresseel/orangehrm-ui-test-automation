# Tesztek futtatása — manuális mód

Minden parancsot a projekt gyökeréből kell kiadni.

## a) Fejlesztői környezetben (IDE, pl. Eclipse)

- Az `src/test/java` mappán jobb klikk → **Run As / JUnit Test**: a beépített JUnit
  elindítja a teszteket.
- Alternatívaként (ha a gépen nincs telepítve Maven): jobb klikk → **Run As / Maven Test**:
  az Eclipse-be integrált Maven futtatja a teszteket, és tesztjelentést generál a
  `target/surefire-reports` mappába.

## b) Parancssorból, telepített Maven-nel

```bash
mvn clean test
```

Egy adott tesztosztály:

```bash
mvn test -Dtest=Test01_UserRegistrationTest
```

Egy adott tesztmetódus:

```bash
mvn test -Dtest=Test02_LoginTest#testSikeresBejelentkezes
```

Több teszt egyszerre:

```bash
mvn test -Dtest=Test01_UserRegistrationTest,Test02_LoginTest
mvn test -Dtest='Test10*'
```

## c) Parancssorból, Maven telepítés nélkül (Maven Wrapper)

Ha a futtató gépen nincs telepítve Maven, a mellékelt wrapper használható:

```bash
./mvnw clean test        # macOS / Linux
mvnw.cmd clean test      # Windows
```

> A wrapper fájlokat (`mvnw`, `mvnw.cmd`, `.mvn/`) egy Maven-nel rendelkező gépen a
> `mvn wrapper:wrapper` paranccsal kell egyszer legenerálni és a repóba feltölteni; ezután
> bármely gépen, Maven telepítése nélkül is futtatható a `mvnw`.

## d) Böngésző nélkül (headless) helyi gépen

A `BaseTest` akkor indít headless Chrome-ot, ha a `GITHUB_ACTIONS`, a `DOCKER_CONTAINER`,
vagy a `HEADLESS=true` környezeti változó valamelyike be van állítva. Ez utóbbi helyben is
kikényszeríthető:

```bash
HEADLESS=true mvn clean test
```

## Instabil futás esetén

A tesztek a publikus demo szervert használják, ami néha lassú vagy megosztott állapotú.
Ilyenkor a bukott teszteket egyszer újrafuttathatod:

```bash
mvn test -Dsurefire.rerunFailingTestsCount=1
```

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
