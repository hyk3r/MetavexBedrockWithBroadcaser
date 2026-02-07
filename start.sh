#!/bin/bash
# MetavexMCBroadcaster Indító Script

JAR_NAME="MetavexMCBroadcaster.jar"

if [ ! -f "$JAR_NAME" ]; then
    echo "Hiba: Nem található a $JAR_NAME fájl!"
    exit 1
fi

echo "MetavexMCBroadcaster indítása..."
java -Dfile.encoding=UTF-8 -Xms512M -Xmx2G -jar "$JAR_NAME"
