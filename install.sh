#!/bin/bash

# MetavexMCBroadcaster Telepítő Script
# Ez a script letölti és telepíti a MetavexMCBroadcaster-t.

INSTALL_DIR="MetavexMCBroadcaster"
JAR_NAME="MetavexMCBroadcaster.jar"
GITHUB_REPO="hyk3r/MetavexBedrockWithBroadcaser"

echo "MetavexMCBroadcaster Telepítése..."

# Java ellenőrzése
if ! command -v java &> /dev/null; then
    echo "Hiba: Java nincs telepítve. Kérlek telepítsd a Java 17-et vagy újabbat."
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | awk -F '.' '{print $1}')
if [[ "$JAVA_VER" -lt 17 ]]; then
    echo "Hiba: Java 17 vagy újabb szükséges. Jelenlegi verzió: $JAVA_VER"
    exit 1
fi

if [ -d "$INSTALL_DIR" ]; then
    echo "A mappa már létezik. Belépés..."
else
    mkdir "$INSTALL_DIR"
fi
cd "$INSTALL_DIR" || exit

echo "Legújabb verzió keresése a $GITHUB_REPO repóban..."
LATEST_URL=$(curl --silent "https://api.github.com/repos/$GITHUB_REPO/releases/latest" | grep "browser_download_url" | grep ".jar" | cut -d : -f 2,3 | tr -d \")

if [ ! -z "$LATEST_URL" ]; then
    echo "Letöltés innen: $LATEST_URL"
    curl -L -o "$JAR_NAME" "$LATEST_URL"
else
    echo "Nem található Release. Kérlek buildeld a projektet manuálisan, vagy töltsd le a forrást:"
    echo "git clone https://github.com/$GITHUB_REPO.git"
fi

# Start script létrehozása
cat <<EOF > start.sh
#!/bin/bash
java -Xms512M -Xmx2G -jar $JAR_NAME
EOF

chmod +x start.sh

echo "Telepítés kész (a JAR fájl kivételével)!"
echo "Indításhoz: ./start.sh"
