import json

egg = {
    "_comment": "DO NOT EDIT: FILE GENERATED AUTOMATICALLY BY PTERODACTYL PANEL - PTERODACTYL.IO",
    "meta": {
        "version": "PTDL_v2",
        "update_url": None
    },
    "exported_at": "2024-05-20T12:00:00+00:00",
    "name": "MetavexMCBroadcaster",
    "author": "hello@metavex.hu",
    "description": "Minecraft Bedrock Szerver Xbox Live Presence támogatással.",
    "features": None,
    "docker_images": {
        "Java 17": "ghcr.io/pterodactyl/yolks:java_17"
    },
    "file_denylist": [],
    "startup": "java -Xms128M -Xmx{{SERVER_MEMORY}}M -jar MetavexMCBroadcaster.jar",
    "config": {
        "files": "{}",
        "startup": "{\r\n    \"done\": \"Server started.\"\r\n}",
        "logs": "{\r\n    \"custom\": false,\r\n    \"location\": \"logs/latest.log\"\r\n}",
        "stop": "stop"
    },
    "scripts": {
        "installation": {
            "script": "#!/bin/bash\r\n# MetavexMCBroadcaster Phone Home\r\napt update\r\napt install -y curl jq git\r\n\r\ncd /mnt/server\r\n\r\nGITHUB_PACKAGE=\"hyk3r/MetavexBedrockWithBroadcaser\"\r\n\r\necho \"Checking releases for $GITHUB_PACKAGE...\"\r\n\r\nLATEST_JSON=$(curl --silent \"https://api.github.com/repos/$GITHUB_PACKAGE/releases/latest\")\r\nDOWNLOAD_URL=$(echo $LATEST_JSON | jq -r '.assets[] | select(.name | endswith(\".jar\")) | .browser_download_url' | head -n 1)\r\n\r\nmkdir -p server\r\n\r\nif [ ! -z \"$DOWNLOAD_URL\" ] && [ \"$DOWNLOAD_URL\" != \"null\" ]; then\r\n    echo \"Release found, downloading: $DOWNLOAD_URL\"\r\n    curl -L -o MetavexMCBroadcaster.jar \"$DOWNLOAD_URL\"\r\nelse\r\n    echo \"No release JAR found. Cloning source...\"\r\n    if [ \"$(ls -A)\" ]; then\r\n         git pull origin main || git pull origin master\r\n    else\r\n         git clone https://github.com/$GITHUB_PACKAGE.git .\r\n    fi\r\n    echo \"Source cloned. You must build the JAR manually.\"\r\nfi\r\n\r\njava -version\r\n",
            "container": "ghcr.io/pterodactyl/installers:debian",
            "entrypoint": "bash"
        }
    },
    "variables": [
        {
            "name": "Szerver Neve",
            "description": "A szerver neve (Xbox Live).",
            "env_variable": "BROADCASTER_SESSION_NAME",
            "default_value": "Metavex Szerver",
            "user_viewable": True,
            "user_editable": True,
            "rules": "required|string",
            "field_type": "text"
        },
        {
            "name": "Bedrock Verzio",
            "description": "Bedrock szerver verzioja (pl. 'latest').",
            "env_variable": "BEDROCK_VERSION",
            "default_value": "latest",
            "user_viewable": True,
            "user_editable": True,
            "rules": "required|string",
            "field_type": "text"
        },
        {
            "name": "Jatekmod",
            "description": "Alapertelmezett jatekmod.",
            "env_variable": "LEVEL_GAMEMODE",
            "default_value": "survival",
            "user_viewable": True,
            "user_editable": True,
            "rules": "required|string",
            "field_type": "text"
        },
        {
            "name": "Nehesseg",
            "description": "Jatek nehessege.",
            "env_variable": "LEVEL_DIFFICULTY",
            "default_value": "normal",
            "user_viewable": True,
            "user_editable": True,
            "rules": "required|string",
            "field_type": "text"
        },
        {
            "name": "Bedrock Port (IPv4)",
            "description": "Primary port.",
            "env_variable": "SERVER_PORT",
            "default_value": "19132",
            "user_viewable": True,
            "user_editable": False,
            "rules": "required|integer",
            "field_type": "text"
        },
        {
            "name": "Bedrock Port (IPv6)",
            "description": "IPv6 port.",
            "env_variable": "SERVER_PORT_V6",
            "default_value": "19133",
            "user_viewable": True,
            "user_editable": True,
            "rules": "required|integer",
            "field_type": "text"
        },
        {
            "name": "Level Seed",
            "description": "World generation seed.",
            "env_variable": "LEVEL_SEED",
            "default_value": "",
            "user_viewable": True,
            "user_editable": True,
            "rules": "nullable|string",
            "field_type": "text"
        },
        {
            "name": "Auto Update",
            "description": "Szerver automatikus frissitese.",
            "env_variable": "BROADCASTER_AUTO_UPDATE",
            "default_value": "true",
            "user_viewable": True,
            "user_editable": True,
            "rules": "required|boolean",
            "field_type": "text"
        }
    ]
}

print(json.dumps(egg, indent=4))
