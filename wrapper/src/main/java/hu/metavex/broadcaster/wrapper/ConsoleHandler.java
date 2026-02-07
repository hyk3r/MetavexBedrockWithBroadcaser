package hu.metavex.broadcaster.wrapper;

import hu.metavex.broadcaster.core.TranslationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleHandler.class);
    private final BedrockServerManager serverManager;
    private boolean running = true;

    public ConsoleHandler(BedrockServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while (running && (line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equalsIgnoreCase("end") || line.equalsIgnoreCase("stop")) {
                    LOGGER.info(TranslationManager.get("wrapper.stopping"));
                    serverManager.stop();
                    System.exit(0); // Broadcaster should exit too
                } else {
                    // Pass to server
                    serverManager.sendCommand(line);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error reading console input", e);
        }
    }
    
    public void stop() {
        running = false;
    }
}
