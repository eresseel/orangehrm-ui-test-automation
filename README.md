# OrangeHRM UI tesztautomatizálás — Junior automata tesztelői vizsgaremek

Selenium WebDriver + JUnit 5 + Maven alapú UI tesztprojekt az OrangeHRM nyilvános demo
alkalmazásához: <https://opensource-demo.orangehrmlive.com> (belépés: `Admin` / `admin123`).

A projekt **Page Object Model** mintát követ: az oldalak lokátorai és műveletei a `pages`
csomagban vannak (Javadoc kommentekkel dokumentálva), a tesztek csak ezeket hívják és
ellenőriznek.

A részletes dokumentáció a [`docs/`](docs/) mappában, fejezetekre bontva található.

## Tesztelt alkalmazás

- **URL:** <https://opensource-demo.orangehrmlive.com> (publikus, hostolt demo, saját telepítés nem kell)
- **Belépés:** `Admin` / `admin123`

## Gyors használati útmutató

Követelmények: JDK 17+, Google Chrome, internetkapcsolat. Maven telepítése nem kötelező, a
projekthez mellékelt Maven Wrapper (`mvnw`) használható.

### Manuális futtatás

- **IDE-ből:** `src/test/java` mappán jobb klikk → *Run As / JUnit Test* (Eclipse), illetve *Run* (IntelliJ).
- **IDE-ből Mavennel:** jobb klikk → *Run As / Maven Test*; a jelentés a `target/surefire-reports` mappába kerül.
- **Parancssorból, telepített Maven-nel:** `mvn clean test`
- **Parancssorból, Maven nélkül (wrapper):** `./mvnw clean test` (macOS / Linux), `mvnw.cmd clean test` (Windows)
- **Docker:** `docker compose up --build` (lásd [docs/07](docs/07-futtatas-docker.md))
- **GitHub-on kézzel:** *Actions → maven tests → Run workflow*

### Automata futtatás

A GitHub Actions a `.github/workflows/maven-tests.yml` workflow szerint minden `push` és
`pull request` esetén (`main` / `master` ág) automatikusan lefuttatja a teszteket headless
Chrome-mal, és a `surefire-reports` mappát artifactként feltölti.

A teszteredmények a `target/surefire-reports` mappában találhatók. A vezetői összefoglaló a
[`documentation/Vezetoi_Tesztjelentes.docx`](documentation/Vezetoi_Tesztjelentes.docx) fájlban van.

## Tartalomjegyzék

1. [A tesztelt alkalmazás](docs/01-tesztelt-alkalmazas.md)
2. [Technológiák](docs/02-technologiak.md)
3. [Követelmények](docs/03-kovetelmenyek.md)
4. [Projektstruktúra](docs/04-projektstruktura.md)
5. [Vizsgakövetelmények lefedettsége](docs/05-vizsgakovetelmenyek-lefedettsege.md)
6. [Tesztek futtatása — manuális mód](docs/06-futtatas-manualis.md)
7. [Tesztek futtatása — Docker](docs/07-futtatas-docker.md)
8. [Tesztek futtatása — automatizált mód (GitHub Actions)](docs/08-futtatas-github-actions.md)
9. [Eredmények](docs/09-eredmenyek.md)
10. [Vezetői tesztjelentés](docs/10-vezetoi-tesztjelentes.md)
11. [Megjegyzések](docs/11-megjegyzesek.md)
