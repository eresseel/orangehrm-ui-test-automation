# Projektstruktúra

```
.
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/                 Maven Wrapper
├── Dockerfile, .dockerignore             konténeres futtatás
├── docker-compose.yml                    kényelmi wrapper a docker parancsokhoz
├── README.md                             tartalomjegyzék, lásd docs/
├── docs/                                 részletes dokumentáció fejezetenként
├── documentation/
│   └── Vezetoi_Tesztjelentes.docx        vezetői tesztjelentés
├── .github/workflows/
│   └── maven-tests.yml                   GitHub Actions workflow
└── src
    ├── main/java/com/orangehrm/test/pages/
    │   ├── LoginPage.java                bejelentkezés, hibaüzenet, adatkezelési link
    │   ├── DashboardPage.java            menü (PIM, Admin), kijelentkezés
    │   ├── AdminPage.java                felhasználók létrehozása, keresése, listázása
    │   ├── PimPage.java                  alkalmazottak, lapozás, szerkesztés, törlés, riportok
    │   └── PrivacyPolicyPage.java        adatkezelési nyilatkozat, ablakváltás
    └── test
        ├── java/com/orangehrm/test/
        │   ├── BaseTest.java             driver létrehozása/lezárása, letöltési mappa, headless-detektálás (CI/Docker)
        │   └── tests/                    a tesztesetek (lásd lent)
        └── resources/testdata.csv        az adatvezérelt teszt bemenete
```

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
