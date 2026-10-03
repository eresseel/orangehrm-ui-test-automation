# Tesztek futtatása — Docker

A projekt gyökerében lévő `Dockerfile` egy önálló, JDK 17 + Maven + Google Chrome tartalmú
image-et épít, amiben a tesztek a helyi gépen telepített Java/Maven/Chrome nélkül is
lefuttathatók — ez ugyanazt a reprodukálható környezetet adja, mint a GitHub Actions CI.

A konténer a `DOCKER_CONTAINER=true` környezeti változót állítja be, amit a `BaseTest`
headless Chrome indítására használ (lásd az előző fejezetet), mert a konténerben nincs
grafikus felület.

## a) `docker compose` (ajánlott, egy paranccsal)

```bash
docker compose up --build
```

A `docker-compose.yml` a `target/surefire-reports` és a `target/downloads` mappákat
kimountolja a host gépre, így a tesztjelentések és a letöltött/exportált fájlok a konténer
leállása után is megmaradnak és a szokásos helyükön (`target/...`) elérhetők.

## b) Sima `docker build` / `docker run`

```bash
docker build -t orangehrm-ui-tests .
docker run --rm \
  -v "$(pwd)/target/surefire-reports:/app/target/surefire-reports" \
  -v "$(pwd)/target/downloads:/app/target/downloads" \
  orangehrm-ui-tests
```

Egy adott tesztosztály futtatása a konténerben (a Dockerfile alap `CMD`-jét felülírva):

```bash
docker run --rm \
  -v "$(pwd)/target/surefire-reports:/app/target/surefire-reports" \
  orangehrm-ui-tests mvn test -Dtest=Test02_LoginTest
```

> Megjegyzés: a konténernek internetkapcsolatra van szüksége, mert a tesztek a publikus
> `opensource-demo.orangehrmlive.com` oldalt hívják.

---
[⬅ Vissza a tartalomjegyzékhez](../README.md)
