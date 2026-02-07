package hu.metavex.broadcaster.installer;

import hu.metavex.broadcaster.core.TranslationManager;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.HttpResponse;
import net.lenni0451.commons.httpclient.requests.impl.GetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater {
    private static final Logger LOGGER = LoggerFactory.getLogger(Updater.class);
    private static final String DOWNLOAD_PAGE = "https://www.minecraft.net/en-us/download/server/bedrock";
    private static final Pattern LINK_PATTERN = Pattern.compile("https://minecraft\\.azureedge\\.net/bin-linux/bedrock-server-[0-9.]+\\.zip");
    
    private final Path serverDir;

    public Updater(Path serverDir) {
        this.serverDir = serverDir;
    }

    public void checkForUpdates(String targetVersion) {
        LOGGER.info(TranslationManager.get("updater.checking"));
        
        if (!"latest".equalsIgnoreCase(targetVersion)) {
             LOGGER.warn("Specific version downloading is experimental. Logic defaults to latest for now as Microsoft does not provide a version archive public API easily. Target: " + targetVersion);
             // In a real scenario, we might try to construct a URL if we knew the schema, but it changes.
             // For now, we proceed with latest check but warn.
        }

        try {
            HttpClient client = new HttpClient();
            HttpResponse response = client.execute(new GetRequest(DOWNLOAD_PAGE));
            if (response.getStatusCode() != 200) {
                LOGGER.error(TranslationManager.get("updater.check_failed", response.getStatusCode()));
                return;
            }

            String html = response.getBodyAsString();
            Matcher matcher = LINK_PATTERN.matcher(html);
            if (matcher.find()) {
                String downloadUrl = matcher.group();
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
                String version = fileName.replace("bedrock-server-", "").replace(".zip", "");
                
                LOGGER.info(TranslationManager.get("updater.found_version", version));
                
                // version check logic (e.g. check if bedrock_server executable exists or version file)
                // For simplicity, we can just download if not present or force update flag is set.
                // Or check a stored version file.
                if (shouldUpdate(version)) {
                    downloadAndInstall(downloadUrl, fileName);
                    saveVersion(version);
                } else {
                    LOGGER.info(TranslationManager.get("updater.up_to_date"));
                }
            } else {
                LOGGER.warn(TranslationManager.get("updater.no_link_found"));
            }
        } catch (IOException e) {
            LOGGER.error(TranslationManager.get("updater.check_error"), e);
        }
    }

    private boolean shouldUpdate(String newVersion) {
        try {
            Path versionFile = serverDir.resolve("version.txt");
            if (!Files.exists(versionFile)) return true;
            String currentVersion = Files.readString(versionFile).trim();
            return !currentVersion.equals(newVersion);
        } catch (IOException e) {
            return true;
        }
    }

    private void saveVersion(String version) {
        try {
            Files.writeString(serverDir.resolve("version.txt"), version);
        } catch (IOException e) {
            LOGGER.error("Failed to save version file", e);
        }
    }

    private void downloadAndInstall(String urlStr, String fileName) {
        LOGGER.info(TranslationManager.get("updater.downloading", fileName));
        Path zipPath = serverDir.resolve(fileName);
        try {
            try (BufferedInputStream in = new BufferedInputStream(new URL(urlStr).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(zipPath.toFile())) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
            
            LOGGER.info(TranslationManager.get("updater.installing"));
            unzip(zipPath, serverDir);
            Files.delete(zipPath);
            
            // Fix permissions
            File executable = serverDir.resolve("bedrock_server").toFile();
            if (executable.exists()) {
                executable.setExecutable(true);
            }
            
            LOGGER.info(TranslationManager.get("updater.success"));

        } catch (IOException e) {
            LOGGER.error(TranslationManager.get("updater.download_failed"), e);
        }
    }

    private void unzip(Path zipFilePath, Path destDir) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                Path filePath = destDir.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    // Extract file
                    // Don't overwrite config files if they exist (server.properties, whitelist.json, permissions.json)
                    String name = entry.getName();
                    if (Files.exists(filePath) && (name.equals("server.properties") || name.equals("whitelist.json") || name.equals("permissions.json"))) {
                         // Skip overwriting config
                    } else {
                        Files.createDirectories(filePath.getParent());
                        Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } else {
                    Files.createDirectories(filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }
}
