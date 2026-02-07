#!/bin/bash
cd /home/container

# Ensure Java is available
if ! command -v java &> /dev/null; then
    echo "Java not found!"
    exit 1
fi

# Pterodactyl specific environment variables usually set by the panel
# We just need to run the startup command.
# For Docker standalone, we assume we need to run the jar.

echo "Starting MetavexMCBroadcaster..."
# The actual startup command in Pterodactyl is often defined in the Egg.
# But for standalone Docker usage, we can default to:
exec java -Xms128M -Xmx2G -jar MetavexMCBroadcaster.jar
