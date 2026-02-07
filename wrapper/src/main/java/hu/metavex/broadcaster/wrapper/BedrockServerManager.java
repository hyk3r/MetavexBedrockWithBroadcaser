package hu.metavex.broadcaster.wrapper;

import hu.metavex.broadcaster.core.TranslationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BedrockServerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BedrockServerManager.class);
    private final Path serverDir;
    private final String serverExecutable;
    
    private Process serverProcess;
    private Thread outputThread;
    private Thread errorThread;
    
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isRestarting = new AtomicBoolean(false);

    public BedrockServerManager(Path serverDir) {
        this.serverDir = serverDir;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            this.serverExecutable = "bedrock_server.exe";
        } else {
            this.serverExecutable = "./bedrock_server";
        }
    }

    public void start() {
        if (isRunning.get()) return;
        
        File executable = serverDir.resolve(serverExecutable.replace("./", "")).toFile();
        if (!executable.exists()) {
            LOGGER.error(TranslationManager.get("wrapper.server_not_found", executable.getAbsolutePath()));
            return;
        }
        
        if (!executable.canExecute()) {
            if (!executable.setExecutable(true)) {
                 LOGGER.error(TranslationManager.get("wrapper.chmod_failed"));
            }
        }

        try {
            LOGGER.info(TranslationManager.get("wrapper.starting_server"));
            ProcessBuilder pb = new ProcessBuilder(serverExecutable);
            pb.directory(serverDir.toFile());
            // pb.inheritIO(); // We want to capture IO to redirect to logger or handle Pterodactyl quirks if needed.
            // Pterodactyl usually grabs stdout of the entrypoint. 
            // If we inherit, it goes to stdout of this java process, which is fine.
            // But we might want to log it with [Bedrock] prefix.
            
            // For Pterodactyl compatibility, ensuring output isn't buffered is key.
            // Using inheritIO is simplest for Pterodactyl as it connects streams directly.
            // However, we want to know when it stops.
            
            pb.redirectErrorStream(true); // Merge stderr into stdout
            
            serverProcess = pb.start();
            isRunning.set(true);

            outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Log directly to stdout for Pterodactyl to pick up, or use Logger?
                        // If we use logger, it might add prefixes. 
                        // Typically Pterodactyl users want raw server output.
                        // But we also want to intercept "Server started" or similar if we want logic.
                        System.out.println(line); 
                    }
                } catch (IOException e) {
                    if (isRunning.get()) LOGGER.error("Error reading server output", e);
                }
            }, "Bedrock-Output");
            outputThread.start();

            // Monitor process exit
            new Thread(() -> {
                try {
                    int exitCode = serverProcess.waitFor();
                    isRunning.set(false);
                    LOGGER.info(TranslationManager.get("wrapper.server_stopped", exitCode));
                    
                    if (exitCode != 0 && !isRestarting.get()) {
                        // Crash detection?
                        LOGGER.warn(TranslationManager.get("wrapper.server_crashed"));
                        // autoRestart(); 
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Server monitor interrupted", e);
                }
            }, "Bedrock-Monitor").start();

        } catch (IOException e) {
            LOGGER.error(TranslationManager.get("wrapper.start_failed"), e);
            isRunning.set(false);
        }
    }

    public void stop() {
        if (!isRunning.get() || serverProcess == null) return;
        
        LOGGER.info(TranslationManager.get("wrapper.stopping_server"));
        sendCommand("stop");
        
        try {
            if (!serverProcess.waitFor(10, TimeUnit.SECONDS)) {
                LOGGER.warn(TranslationManager.get("wrapper.force_killing"));
                serverProcess.destroyForcibly();
            }
        } catch (InterruptedException e) {
            serverProcess.destroyForcibly();
        }
        isRunning.set(false);
    }

    public void sendCommand(String command) {
        if (!isRunning.get() || serverProcess == null) return;
        try {
            OutputStream os = serverProcess.getOutputStream();
            os.write((command + "\n").getBytes(StandardCharsets.UTF_8));
            os.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to send command to server", e);
        }
    }
    
    public boolean isRunning() {
        return isRunning.get();
    }
}
