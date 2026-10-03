# Tesztek futtatása — automatizált mód (GitHub Actions)

A `.github/workflows/maven-tests.yml` workflow a következő eseményekre indul: `push` és
`pull_request` a `main` / `master` ágra, illetve kézzel (**Actions → Run workflow**,
`workflow_dispatch`).

Lépései:

1. Kódletöltés (`actions/checkout`)
2. JDK 17 (Temurin) beállítása Maven cache-sel
3. A `mvnw` futtatási jogosultságának beállítása
4. `./mvnw clean test` futtatása headless módban (a `GITHUB_ACTIONS` változó miatt a
   `BaseTest` automatikusan headless Chrome-ot indít)
5. A `target/surefire-reports/` mappa feltöltése `surefire-reports` néven artifactként,
   a tesztek eredményétől függetlenül (`if: always()`)

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
