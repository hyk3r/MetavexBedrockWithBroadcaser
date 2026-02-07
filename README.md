# MetavexMCBroadcaster

A **MetavexMCBroadcaster** egy modern, Java-alapú "Wrapper" alkalmazás Minecraft Bedrock szerverekhez. Célja, hogy lehetővé tegye a konzolos (Xbox, PlayStation, Switch) játékosok csatlakozását dedikált szerverekhez az Xbox Live "Barátok" (Friends) listáján keresztül, anélkül, hogy DNS trükközésre lenne szükség.

## Funkciók

*   **Xbox Live Presence**: A szerver megjelenik a barátok listáján mint "Joinable Game".
*   **Magyar Lokalizáció**: Minden konzolüzenet és beállítás varázsló magyar nyelvű.
*   **Wrapper Architektúra**: Kezeli a `bedrock_server` folyamatot, újraindítja ha összeomlik.
*   **Auto-Update**:
    *   Automatikusan letölti és frissíti a hivatalos Bedrock szervert indításkor.
*   **Könnyű Telepítés**: Interaktív Setup Wizard az első indításkor.
*   **Pterodactyl Támogatás**: Kész Egg fájl és Docker támogatás.

## Követelmények

*   Java 17 vagy újabb
*   Linux (ajánlott) vagy Windows
*   Microsoft Fiók (kizárólag erre a célra, Game Pass nem szükséges)

## Telepítés

### Linux (Native)

1.  Másold a `MetavexMCBroadcaster.jar`-t egy mappába.
2.  Futtasd a telepítő scriptet (opcionális segédlet):
    ```bash
    chmod +x install.sh
    ./install.sh
    ```
3.  Indítsd el:
    ```bash
    ./start.sh
    ```
4.  Kövessd a Setup Wizard utasításait a konzolon.

### Pterodactyl

1.  Importáld az `ecosystem/pterodactyl/egg-metavex-mc-broadcaster.json` tojást.
2.  Hozz létre egy szervert ezzel a tojással.
3.  Az első indításnál a konzolon kövesd a Microsoft Auth linket és írd be a kódot.

## Konfiguráció (`config.yml`)

A `config.yml` automatikusan létrejön az első indításkor.

```yaml
sessionName: "Saját Minecraft Szerver"
serverIp: "127.0.0.1"
serverPort: 19132
autoUpdateServer: true
autoRestartServer: true
```

## Microsoft Hitelesítés

Az első indításkor a konzol kiír egy linket (`microsoft.com/link`) és egy kódot.
1.  Nyisd meg a linket a böngésződben.
2.  Jelentkezz be azzal a Microsoft fiókkal, ami a "Broadcaster" (Szerver) fiók lesz.
3.  Írd be a kódot.
4.  A szerver automatikusan tovább lép.

**Fontos**: Ezt a fiókot kell "Barátnak" jelölniük a játékosoknak ahhoz, hogy lássák a szervert.

## Fejlesztőknek

A projekt Gradle-t használ.

Buildelés:
```bash
./gradlew shadowJar
```

A kész JAR fájl az `app/build/libs/app-all.jar` helyen lesz.
