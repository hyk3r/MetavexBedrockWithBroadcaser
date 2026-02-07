# MetavexMCBroadcaster

Minecraft Bedrock szerver wrapper, ami Xbox Live-on láthatóvá teszi a szerveredet a barátok listáján (konzolosoknak is: Xbox, PlayStation, Switch).

## Mit csinál?

- 🎮 **Xbox Presence** - A szervered megjelenik mint "online barát"
- 🔄 **Auto-frissítés** - Letölti és frissíti a Bedrock szervert automatikusan
- ⚙️ **Környezeti változók** - Egyszerűen konfigurálható (ENV-ből)

## Követelmények

- **Java 21** vagy újabb
- Linux (Windows nem támogatott)

## Telepítés

### Pterodactyl (ajánlott)

1. Töltsd le: [`egg-metavex-mc-broadcaster.json`](ecosystem/pterodactyl/egg-metavex-mc-broadcaster.json)
2. Admin Panel → Nests → Import Egg
3. Hozz létre szervert az importált Egg-gel

### Manuális

```bash
# JAR letöltése
curl -LO https://github.com/hyk3r/MetavexBedrockWithBroadcaser/releases/latest/download/MetavexMCBroadcaster.jar

# Indítás
java -jar MetavexMCBroadcaster.jar
```

## Konfiguráció (ENV változók)

| Változó | Leírás | Alapérték |
|---------|--------|-----------|
| `BROADCASTER_SESSION_NAME` | Szerver neve Xbox Live-on | `Metavex Szerver` |
| `BEDROCK_VERSION` | Bedrock verzió | `latest` |
| `LEVEL_GAMEMODE` | survival / creative / adventure | `survival` |
| `LEVEL_DIFFICULTY` | peaceful / easy / normal / hard | `normal` |
| `SERVER_PORT` | UDP port | `19132` |

## Első indítás

1. Indítsd el a JAR-t
2. A konzolon megjelenik egy Microsoft hitelesítési link + kód
3. Nyisd meg a linket böngészőben és írd be a kódot
4. Sikeres auth után a szerver elindul

## Hibaelhárítás

- **Nem lehet csatlakozni**: 19132/udp port nyitva?
- **Token hiba**: Töröld az `auth_cache.json` fájlt és logolj be újra

---
© 2024 Metavex - És az ÁDI fiú nem szól bele így marad! 
