package hu.metavex.broadcaster.app;

import hu.metavex.broadcaster.core.AuthManager;
import hu.metavex.broadcaster.core.BroadcasterConfig;
import hu.metavex.broadcaster.core.SessionInfo;
import hu.metavex.broadcaster.core.SessionManager;
import hu.metavex.broadcaster.core.StorageManager;
import hu.metavex.broadcaster.core.TranslationManager;
import hu.metavex.broadcaster.installer.Updater;
import hu.metavex.broadcaster.wrapper.BedrockServerManager;
import hu.metavex.broadcaster.wrapper.ConsoleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path SERVER_DIR = Paths.get("server");
    private static final Path CONFIG_FILE = Paths.get("config.yml");

    public static void main(String[] args) {
        LOGGER.info("Starting MetavexMCBroadcaster...");

        // Load or Run Wizard
        BroadcasterConfig config;
        if (!Files.exists(CONFIG_FILE)) {
            // Check for Environment Variables (Pterodactyl / Docker friendly)
            String envSessionName = System.getenv("BROADCASTER_SESSION_NAME");
            if (envSessionName != null && !envSessionName.isBlank()) {
                LOGGER.info("Detected Environment Variables. Skipping Setup Wizard.");
                config = new BroadcasterConfig();
                config.setSessionName(envSessionName);
                config.setServerIp(System.getenv().getOrDefault("BROADCASTER_SERVER_IP", "127.0.0.1"));
                try {
                    config.setServerPort(Integer.parseInt(System.getenv().getOrDefault("BROADCASTER_SERVER_PORT", "19132")));
                } catch (NumberFormatException e) {
                    config.setServerPort(19132);
                }
                config.setAutoUpdateServer(parseBoolean(System.getenv().getOrDefault("BROADCASTER_AUTO_UPDATE", "true")));
                config.setAutoRestartServer(parseBoolean(System.getenv().getOrDefault("BROADCASTER_AUTO_RESTART", "true")));
                saveConfig(config);
            } else {
                config = SetupWizard.run();
                saveConfig(config);
            }
        } else {
            config = loadConfig();
        }

        // Initialize Managers
        StorageManager storageManager = new StorageManager(DATA_DIR);
        AuthManager authManager = new AuthManager(storageManager);
        
        // Environment Variables for Pterodactyl/Config
        String bedrockVersion = System.getenv().getOrDefault("BEDROCK_VERSION", "latest");
        String levelGamemode = System.getenv("LEVEL_GAMEMODE");
        String levelDifficulty = System.getenv("LEVEL_DIFFICULTY");
        String levelSeed = System.getenv("LEVEL_SEED");
        String serverPortV6 = System.getenv("SERVER_PORT_V6");
        
        // Updater
        if (config.isAutoUpdateServer()) {
            try {
                Files.createDirectories(SERVER_DIR);
                Updater updater = new Updater(SERVER_DIR);
                updater.checkForUpdates(bedrockVersion);
            } catch (Exception e) {
                LOGGER.error("Failed to run updater", e);
            }
        }
        
        // Update server.properties
        try {
            hu.metavex.broadcaster.wrapper.ServerPropertiesManager propManager = new hu.metavex.broadcaster.wrapper.ServerPropertiesManager(SERVER_DIR);
            java.util.Map<String, String> updates = new java.util.HashMap<>();
            updates.put("server-port", String.valueOf(config.getServerPort()));
            updates.put("server-name", config.getSessionName()); 
            
            if (levelGamemode != null) updates.put("gamemode", levelGamemode);
            if (levelDifficulty != null) updates.put("difficulty", levelDifficulty);
            if (levelSeed != null && !levelSeed.isBlank()) updates.put("level-seed", levelSeed);
            if (serverPortV6 != null) updates.put("server-portv6", serverPortV6);
            
            propManager.updateProperties(updates);
        } catch (Exception e) {
            LOGGER.error("Failed to configure server.properties", e);
        }

        // Wrapper
        BedrockServerManager serverManager = new BedrockServerManager(SERVER_DIR);
        serverManager.start();

        // Start Console Handler
        ConsoleHandler consoleHandler = new ConsoleHandler(serverManager);
        Thread consoleThread = new Thread(consoleHandler, "ConsoleHandler");
        consoleThread.start();

        // Broadcaster
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setHostName(config.getSessionName());
        sessionInfo.setWorldName(config.getSessionName()); 
        sessionInfo.setIp(config.getServerIp()); // This should be the IP players connect to locally, or use local loopback if redirecting
        sessionInfo.setPort(config.getServerPort());
        sessionInfo.setPlayers(0);
        sessionInfo.setMaxPlayers(10); // Should parse from server.properties really

        SessionManager sessionManager = new SessionManager(authManager, sessionInfo);
        
        // Wait for server to start?
        // Ideally we should wait, or retry if port is not open.
        // Or redirection packet handler will handle it (if it can't connect, client drops).
        
        sessionManager.start();

        // Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info(TranslationManager.get("app.shutdown"));
            consoleHandler.stop();
            sessionManager.stop();
            serverManager.stop();
        }));
    }

    private static void saveConfig(BroadcasterConfig config) {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(CONFIG_FILE).build();
        try {
            ConfigurationNode node = loader.createNode();
            node.set(BroadcasterConfig.class, config);
            loader.save(node);
        } catch (ConfigurateException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    private static BroadcasterConfig loadConfig() {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(CONFIG_FILE).build();
        try {
            ConfigurationNode node = loader.load();
            return node.get(BroadcasterConfig.class);
        } catch (ConfigurateException e) {
            LOGGER.error("Failed to load config", e);
            return new BroadcasterConfig();
        }
    }

    private static boolean parseBoolean(String value) {
        if (value == null) return false;
        String v = value.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("yes");
    }
}
