package hu.metavex.broadcaster.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerPropertiesManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPropertiesManager.class);
    private final Path propertiesFile;

    public ServerPropertiesManager(Path serverDir) {
        this.propertiesFile = serverDir.resolve("server.properties");
    }

    public void updateProperties(Map<String, String> updates) {
        if (!Files.exists(propertiesFile)) {
            // If it doesn't exist, we might be running before the server is installed.
            // But usually, we install/update first, then configure.
            // If it doesn't exist, we can create a basic one or wait.
            // For now, let's create it if missing, or just return.
            // Better to let the server generate default? No, we need to set values.
            // Let's create it.
            try {
                Files.createFile(propertiesFile);
            } catch (IOException e) {
                LOGGER.error("Failed to create server.properties", e);
                return;
            }
        }

        try {
            List<String> lines = Files.readAllLines(propertiesFile, StandardCharsets.UTF_8);
            Map<String, String> currentProps = new HashMap<>();
            
            // Parse existing
            for (String line : lines) {
                if (!line.trim().startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    currentProps.put(parts[0].trim(), parts.length > 1 ? parts[1].trim() : "");
                }
            }

            // Apply updates
            updates.forEach(currentProps::put);

            // Reconstruct file content
            // We lose comments this way unless we are careful. 
            // Simple approach: Iterate lines, replace if key matches, append if new.
            
            Map<String, String> pendingUpdates = new HashMap<>(updates);
            List<String> newLines = lines.stream().map(line -> {
                if (line.trim().startsWith("#") || !line.contains("=")) {
                    return line;
                }
                String key = line.split("=", 2)[0].trim();
                if (pendingUpdates.containsKey(key)) {
                    String val = pendingUpdates.remove(key);
                    return key + "=" + val;
                }
                return line;
            }).collect(Collectors.toList());

            // Add remaining new keys
            pendingUpdates.forEach((k, v) -> newLines.add(k + "=" + v));

            Files.write(propertiesFile, newLines, StandardCharsets.UTF_8);
            LOGGER.info("Updated server.properties");

        } catch (IOException e) {
            LOGGER.error("Failed to update server.properties", e);
        }
    }
}
