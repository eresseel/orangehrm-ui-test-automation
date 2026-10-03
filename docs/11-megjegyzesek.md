# Megjegyzések

- A demo alkalmazás közös és nyilvános: az adatai (alkalmazottak, felhasználók) mások
  által is módosulnak, időnként visszaáll az alapállapot. Ezért a tesztek nem függenek
  konkrét meglévő adatoktól, a létrehozott adatok egyediek (pl. UUID-alapú felhasználónév).
- A `Test08` és a `Test09` a demo adatait módosítja / törli.
- A `testdata.csv` `EmpId` oszlopát a jelenlegi implementáció nem viszi be külön mezőbe,
  mert az Employee Id-t az alkalmazás automatikusan generálja; az oszlop jelenleg csak a
  paraméterezett teszt bemenetének része.
- A `PimPage.downloadFirstReport()` a riport megnyitása után megkeresi és megnyomja a
  kritérium-űrlap "Show" gombját is (ha van), mert e nélkül csak a szűrőmezők jelennek meg,
  nem a tényleges riporttábla — ez volt a korábbi hiba oka, amikor a `target/downloads`
  mappába kerülő CSV üres vagy hibás adatokat tartalmazott.

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
