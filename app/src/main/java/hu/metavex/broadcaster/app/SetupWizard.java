package hu.metavex.broadcaster.app;

import hu.metavex.broadcaster.core.BroadcasterConfig;
import hu.metavex.broadcaster.core.TranslationManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SetupWizard {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetupWizard.class);

    public static BroadcasterConfig run() {
        BroadcasterConfig config = new BroadcasterConfig();
        LOGGER.info(TranslationManager.get("wizard.welcome"));

        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            // Session Name
            String name = readLine(reader, "wizard.session_name", "Minecraft Server");
            config.setSessionName(name);

            // Server IP
            String ip = readLine(reader, "wizard.server_ip", "127.0.0.1");
            config.setServerIp(ip);

            // Server Port
            while (true) {
                String portStr = readLine(reader, "wizard.server_port", "19132");
                try {
                    int port = Integer.parseInt(portStr);
                    if (port > 0 && port < 65536) {
                        config.setServerPort(port);
                        break;
                    }
                } catch (NumberFormatException ignored) {}
                LOGGER.warn(TranslationManager.get("wizard.invalid_port"));
            }

            // Auto Update
            String autoUpdate = readLine(reader, "wizard.auto_update", "yes");
            config.setAutoUpdateServer(isYes(autoUpdate));

            LOGGER.info(TranslationManager.get("wizard.complete"));

        } catch (IOException e) {
            LOGGER.error("Failed to run wizard", e);
        }
        return config;
    }

    private static String readLine(LineReader reader, String key, String defaultValue) {
        String prompt = TranslationManager.get(key) + " [" + defaultValue + "]: ";
        String line = reader.readLine(prompt);
        return line.trim().isEmpty() ? defaultValue : line.trim();
    }

    private static boolean isYes(String input) {
        return input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y") || input.equalsIgnoreCase("igen") || input.equalsIgnoreCase("i");
    }
}
