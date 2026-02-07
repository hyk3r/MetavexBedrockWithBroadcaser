package hu.metavex.broadcaster.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageManager.class);
    private final Path cacheDir;
    private final Path authCacheFile;

    public StorageManager(Path dataDirectory) {
        this.cacheDir = dataDirectory;
        this.authCacheFile = dataDirectory.resolve("auth_cache.json");
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            LOGGER.error("Failed to create cache directory", e);
        }
    }

    public void saveAuthCache(String json) {
        try {
            Files.writeString(authCacheFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to save auth cache", e);
        }
    }

    public String loadAuthCache() {
        if (!Files.exists(authCacheFile)) {
            return "";
        }
        try {
            return Files.readString(authCacheFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to load auth cache", e);
            return "";
        }
    }
}
