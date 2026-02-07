# MetavexMCBroadcaster

Ez a projekt egy **Minecraft Bedrock Dedicated Server** wrapper alkalmazás, amely lehetővé teszi a szerver megjelenését az **Xbox Live barátok listáján** (LAN / Friends tab) konzolos játékosok számára (Xbox, PlayStation, Switch).

A szoftver automatikusan kezeli a Bedrock szerver letöltését, frissítését, konfigurálását, és biztosítja a szükséges "Advertisement" csomagok küldését az Xbox Live hálózat felé.

## Funkciók

*   🚀 **Xbox Live Presence**: A szerver megjelenik a barátoknál, mintha egy barát játszana.
*   🔄 **Automatikus Frissítés**:
    *   **Bedrock Szerver**: Indításkor ellenőrzi és letölti a legújabb (vagy a kért) verziót.
    *   **Broadcaster**: Képes önmagát frissíteni GitHub Release-ből.
*   ⚙️ **Dinamikus Konfiguráció**: Környezeti változókból (ENV) állítja be a `server.properties`-t (pl. Port, Játékmód, Nehézség, Seed).
*   🐳 **Docker & Pterodactyl Support**: Hivatalos Docker image és Pterodactyl Egg támogatás.
*   🇭🇺 **Magyar Nyelvű**: A telepítő és a konzol üzenetek magyarul kommunikálnak.

## Telepítés

### 1. Pterodactyl (Ajánlott)

A projekt tartalmaz egy előre elkészített Pterodactyl Egg-et, amely mindent automatikusan elvégez.

1.  Töltsd le az `ecosystem/pterodactyl/egg-metavex-mc-broadcaster.json` fájlt.
2.  Importáld a Pterodactyl Admin panelen a **Nests** menüpontban.
3.  Hozz létre egy új szervert ezzel az Egg-el.
4.  A telepítés után a szerver automatikusan elindul, letölti a Bedrock szervert és beállítja magát.

### 2. Docker

Futtatható Docker konténerként is:

```bash
docker run -d \
  -p 19132:19132/udp \
  -e SERVER_PORT=19132 \
  -e LEVEL_GAMEMODE=survival \
  -e BROADCASTER_SESSION_NAME="Sajat Szerver" \
  ghcr.io/metavex/metavex-mc-broadcaster:latest
```

### 3. Manuális Telepítés (Linux)

Követelmények:
*   Java 17 vagy újabb
*   `curl`, `unzip`

**Telepítés:**
```bash
curl -sL https://raw.githubusercontent.com/hyk3r/MetavexBedrockWithBroadcaser/main/install.sh | bash
```

**Indítás:**
```bash
./start.sh
```

## Konfiguráció

A szerver elsősorban **Környezeti Változókkal (Environment Variables)** konfigurálható. Ha Pterodactylt használsz, ezeket a "Startup" fülön találod.

| Változó | Leírás | Alapérték |
| :--- | :--- | :--- |
| `SERVER_JARFILE` | A futtatandó JAR fájl neve. | `MetavexMCBroadcaster.jar` |
| `BROADCASTER_SESSION_NAME` | A szerver neve, ami megjelenik Xbox Live-on. | `Metavex Szerver` |
| `BEDROCK_VERSION` | A letöltendő Bedrock szerver verziója. | `latest` |
| `LEVEL_GAMEMODE` | Játékmód (`survival`, `creative`, `adventure`). | `survival` |
| `LEVEL_DIFFICULTY` | Nehézség (`peaceful`, `easy`, `normal`, `hard`). | `normal` |
| `SERVER_PORT` | IPv4 Port (UDP). | `19132` |
| `SERVER_PORT_V6` | IPv6 Port (UDP). | `19133` |
| `LEVEL_SEED` | Világgenerálási seed. | (üres = véletlenszerű) |
| `BROADCASTER_AUTO_UPDATE` | Szerver automatikus frissítése (`true`/`false`). | `true` |

## Hibaelhárítás

*   **Nem  lehet csatlakozni**: Ellenőrizd, hogy a 19132-es UDP port nyitva van-e a tűzfalon.
*   **Token hiba**: A Broadcaster első indításkor kérhet egy Microsoft hitelesítést (Device Code Flow). A konzolon megjelenő linket nyisd meg és írd be a kódot.
*   **Pterodactyl Import Hiba**: Győződj meg róla, hogy a legfrissebb `egg-metavex-mc-broadcaster.json` fájlt használod. Ne módosítsd a JSON szerkezetét kézzel.

## Fejlesztőknek

**Buildelés:**
```bash
./gradlew build
```
A kész JAR fájl a `app/build/libs` mappában lesz.

---
© 2024 Metavex - Minden jog fenntartva.
