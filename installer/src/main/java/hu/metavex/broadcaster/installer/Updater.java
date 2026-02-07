package hu.metavex.broadcaster.installer;

import hu.metavex.broadcaster.core.TranslationManager;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.HttpResponse;
import net.lenni0451.commons.httpclient.requests.impl.GetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater {
    private static final Logger LOGGER = LoggerFactory.getLogger(Updater.class);
    private static final String DOWNLOAD_API = "https://net-secondary.web.minecraft-services.net/api/v1.0/download/links";
    private static final Gson GSON = new Gson();
    
    private final Path serverDir;

    public Updater(Path serverDir) {
        this.serverDir = serverDir;
    }

    public void checkForUpdates(String targetVersion) {
        LOGGER.info(TranslationManager.get("updater.checking"));
        
        try {
            HttpClient client = new HttpClient();
            HttpResponse response = client.execute(new GetRequest(DOWNLOAD_API));
            if (response.getStatusCode() != 200) {
                LOGGER.error(TranslationManager.get("updater.check_failed", response.getStatusCode()));
                return;
            }

            String json = response.getContentAsString();
            JsonObject root = GSON.fromJson(json, JsonObject.class);
            JsonObject result = root.getAsJsonObject("result");
            JsonArray links = result.getAsJsonArray("links");
            
            String downloadUrl = null;
            for (JsonElement elem : links) {
                JsonObject link = elem.getAsJsonObject();
                String type = link.get("downloadType").getAsString();
                if ("serverBedrockLinux".equals(type)) {
                    downloadUrl = link.get("downloadUrl").getAsString();
                    break;
                }
            }
            
            if (downloadUrl != null) {
                String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
                String version = fileName.replace("bedrock-server-", "").replace(".zip", "");
                
                LOGGER.info(TranslationManager.get("updater.found_version", version));
                
                // Check if we need to update
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
            Path executable = serverDir.resolve("bedrock_server");
            
            // Always update if executable doesn't exist
            if (!Files.exists(executable)) return true;
            
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
            // Create server directory if not exists
            Files.createDirectories(serverDir);
            
            try (BufferedInputStream in = new BufferedInputStream(new URL(urlStr).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(zipPath.toFile())) {
                byte dataBuffer[] = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
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
                LOGGER.info("Set executable permission on bedrock_server");
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
                    // Don't overwrite config files if they exist
                    String name = entry.getName();
                    if (Files.exists(filePath) && (name.equals("server.properties") || name.equals("whitelist.json") || name.equals("permissions.json"))) {
                         // Skip overwriting config
                         LOGGER.debug("Skipping existing config file: " + name);
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
