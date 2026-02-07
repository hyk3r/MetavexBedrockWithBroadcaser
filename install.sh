#!/bin/bash

# MetavexMCBroadcaster Telepítő Script
# Ez a script letölti és telepíti a MetavexMCBroadcaster-t.

INSTALL_DIR="MetavexMCBroadcaster"
JAR_NAME="MetavexMCBroadcaster.jar"
GITHUB_REPO="Metavex/MetavexMCBroadcaster" # A felhasználó GitHub repója

echo "Started installing MetavexMCBroadcaster..."

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

# Könyvtár létrehozása
if [ -d "$INSTALL_DIR" ]; then
    echo "A mappa már létezik. Frissítés..."
else
    mkdir "$INSTALL_DIR"
fi
cd "$INSTALL_DIR" || exit

# Jar letöltése (GitHub Releases - Placeholder, amíg nincs release)
# echo "Letöltés innen: https://github.com/$GITHUB_REPO/releases/latest/download/$JAR_NAME"
# curl -L -o "$JAR_NAME" "https://github.com/$GITHUB_REPO/releases/latest/download/$JAR_NAME"

# Mivel még fejlesztés alatt áll, és a user buildeli, ezért feltételezzük, hogy a JAR-t manuálisan másolták ide vagy buildelték.
# De a script később használható lesz.
# Ideiglenes üzenet:
echo "Mivel ez egy fejlesztői verzió, kérlek másold a 'app/build/libs/app-all.jar'-t ide '$INSTALL_DIR/$JAR_NAME' néven."
echo "Ezután futtasd a './start.sh' parancsot."

# Start script létrehozása
cat <<EOF > start.sh
#!/bin/bash
java -Xms512M -Xmx2G -jar $JAR_NAME
EOF

chmod +x start.sh

echo "Telepítés kész (a JAR fájl kivételével)!"
echo "Indításhoz: ./start.sh"
