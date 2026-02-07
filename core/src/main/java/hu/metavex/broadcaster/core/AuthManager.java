package hu.metavex.broadcaster.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lenni0451.commons.httpclient.HttpClient;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.bedrock.BedrockAuthManager;
import net.raphimc.minecraftauth.msa.model.MsaDeviceCode;
import net.raphimc.minecraftauth.msa.service.impl.DeviceCodeMsaAuthService;

import net.raphimc.minecraftauth.util.holder.listener.BasicChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class AuthManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthManager.class);
    private final StorageManager storageManager;
    private BedrockAuthManager authManager;

    public AuthManager(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public BedrockAuthManager getManager() {
        if (authManager == null) {
            initialize();
        }
        try {
            refreshTokens();
        } catch (Exception e) {
            LOGGER.error("Failed to refresh tokens, re-initializing...", e);
            initialize(); // Force re-login
        }
        return authManager;
    }

    private void initialize() {
        HttpClient httpClient = MinecraftAuth.createHttpClient();

        // 1. Try to load from cache
        if (authManager == null) {
            try {
                String cacheData = storageManager.loadAuthCache();
                if (!cacheData.isBlank()) {
                    JsonObject json = JsonParser.parseString(cacheData).getAsJsonObject();
                    


                    if (json != null) {
                        authManager = BedrockAuthManager.fromJson(httpClient, Constants.BEDROCK_CODEC.getMinecraftVersion(), json);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load auth cache", e);
            }
        }

        // 2. Perform Login if needed
        try {
            if (authManager == null) {
                Consumer<MsaDeviceCode> deviceCodeCallback = msaDeviceCode -> {
                    String url = msaDeviceCode.getVerificationUri();
                    String code = msaDeviceCode.getUserCode();
                    
                    LOGGER.info(TranslationManager.get("setup.auth_start"));
                    LOGGER.info(TranslationManager.get("setup.auth_code", url));
                    LOGGER.info(TranslationManager.get("setup.auth_code_display", code));
                };

                authManager = BedrockAuthManager.create(httpClient, Constants.BEDROCK_CODEC.getMinecraftVersion())
                        .login(DeviceCodeMsaAuthService::new, deviceCodeCallback);
                
                LOGGER.info(TranslationManager.get("setup.auth_success", getGamertag()));
            }

            // Save on change
            authManager.getChangeListeners().add((BasicChangeListener) this::saveToCache);
            saveToCache();

        } catch (Exception e) {
            LOGGER.error(TranslationManager.get("setup.auth_failed", e.getMessage()), e);
            throw new RuntimeException("Authentication failed", e);
        }
    }

    private void refreshTokens() throws IOException {
        authManager.getXboxLiveXstsToken().getUpToDate();
        updateProfileInfo();
    }

    private void updateProfileInfo() {
        HttpClient httpClient = MinecraftAuth.createHttpClient();
        try {
            hu.metavex.broadcaster.core.models.auth.XblUsersMeProfileRequest.Response response = httpClient.executeAndHandle(new hu.metavex.broadcaster.core.models.auth.XblUsersMeProfileRequest(authManager.getXboxLiveXstsToken().getUpToDate()));
            if (!response.profileUsers().isEmpty()) {
                hu.metavex.broadcaster.core.models.auth.XblUsersMeProfileRequest.Response.ProfileUser profileUser = response.profileUsers().get(0);
                this.gamertag = profileUser.settings().get("Gamertag");
                this.xuid = profileUser.id();
                LOGGER.info("Authenticated as: {} ({})", gamertag, xuid);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get Xbox profile info", e);
        }
    }

    public String getGamertag() {
        return gamertag;
    }
    
    public String getXuid() {
        return xuid;
    }

    private String gamertag;
    private String xuid;
    private void saveToCache() {
        if (authManager != null) {
            storageManager.saveAuthCache(BedrockAuthManager.toJson(authManager).toString());
        }
    }
}
