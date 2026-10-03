# Maven + Eclipse Temurin JDK 17 alap image: ebben már benne van a Maven és a JDK is,
# így nem kell külön telepítenünk őket, csak a Chrome böngészőt kell hozzáadnunk
# (a chromedrivert a WebDriverManager tölti le automatikusan futáskor).
FROM --platform=linux/amd64 maven:3.9.6-eclipse-temurin-17

# Google Chrome (stable csatorna) telepítése, mert a Selenium ChromeDriver-nek egy
# ténylegesen telepített Chrome böngészőre van szüksége a konténeren belül.
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget gnupg unzip ca-certificates fonts-liberation \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
        > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Előbb csak a pom.xml-t másoljuk be és töltjük le a függőségeket – ez kihasználja a Docker
# réteg-cache-t: amíg a pom.xml nem változik, a függőségek nem töltődnek le újra minden build-nél,
# csak akkor, ha tényleg megváltozott valami a build-konfigurációban.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Csak ezután másoljuk be a teljes forráskódot (ez változik leggyakrabban),
# hogy a fenti függőség-letöltési réteg minél tovább cache-elve maradjon.
COPY . .

# Ez a jelző mondja meg a BaseTest-nek, hogy headless Chrome-ot indítson, mert a konténeren
# belül nincs grafikus felület (lásd BaseTest.isHeadlessEnvironment()).
ENV DOCKER_CONTAINER=true

# Alapértelmezett parancs: az összes teszt lefuttatása. Felülírható docker run végén
# más Maven paranccsal (pl. -Dtest=Test02_LoginTest), lásd a README Docker szekcióját.
CMD ["mvn", "test"]
