package com.iluncrypt.iluncryptapp.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.iluncrypt.iluncryptapp.utils.config.ConfigManager.loadConfigFile;
import static com.iluncrypt.iluncryptapp.utils.config.ConfigManager.saveConfigToFile;

/**
 * Manages configuration settings for symmetric key encryption.
 */
public class SymmetricKeyConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads the symmetric key encryption configuration.
     * @param algorithmName "AES" or "DES"
     * @return SymmetricKeyConfig instance.
     */
    public SymmetricKeyConfig loadSymmetricKeyConfig(String algorithmName) {
        Path configPath = Paths.get(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return getDefaultSymmetricKeyConfig(algorithmName);
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Map<String, Object> rawConfig = GSON.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
            if (rawConfig == null || !rawConfig.containsKey("symmetric-key-options")) {
                return getDefaultSymmetricKeyConfig(algorithmName);
            }

            Map<String, Object> symmetricKeySettings = (Map<String, Object>) rawConfig.get("symmetric-key-options");
            if (!symmetricKeySettings.containsKey(algorithmName)) {
                return getDefaultSymmetricKeyConfig(algorithmName);
            }

            return GSON.fromJson(GSON.toJson(symmetricKeySettings.get(algorithmName)), SymmetricKeyConfig.class);

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Warning: Invalid config.json detected. Ignoring and loading default symmetric key config: " + e.getMessage());
        }

        return getDefaultSymmetricKeyConfig(algorithmName);
    }

    public static void saveSymmetricKeyConfig(SymmetricKeyConfig config) {
        String cipherType = config.getAlgorithm().name();
        Map<String, Object> configData = loadConfigFile();
        Map<String, Object> symmetricKeySettings = (Map<String, Object>) configData.computeIfAbsent("symmetric-key-options", k -> new HashMap<>());

        if (config.equals(getDefaultSymmetricKeyConfig(cipherType))) {
            symmetricKeySettings.remove(cipherType);
        } else {
            symmetricKeySettings.put(cipherType, config);
        }

        saveConfigToFile(configData);
    }

    /**
     * Returns the default symmetric key encryption configuration based on the algorithm name.
     * @param algorithmName "AES" or "DES"
     * @return Default SymmetricKeyConfig instance.
     */
    public static SymmetricKeyConfig getDefaultSymmetricKeyConfig(String algorithmName) {
        switch (algorithmName.toUpperCase()) {
            case "AES":
                return new SymmetricKeyConfig(
                        SymmetricKeyAlgorithm.AES,
                        SymmetricKeyMode.GCM,
                        KeySize.AES_256,
                        AuthenticationMethod.NONE,
                        GCMTagSize.TAG_128,
                        PaddingScheme.NO_PADDING,
                        false, // showIV
                        true,  // generateIV
                        true,  // generateKey
                        true   // saveAlgorithm
                );

            case "DES":
                return new SymmetricKeyConfig(
                        SymmetricKeyAlgorithm.DES,
                        SymmetricKeyMode.CBC,
                        KeySize.DES_56,
                        AuthenticationMethod.NONE,
                        null,  // DES does not use GCMTagSize
                        PaddingScheme.PKCS5,
                        false, // showIV
                        true,  // generateIV
                        true,  // generateKey
                        true   // saveAlgorithm
                );

            default:
                throw new IllegalArgumentException("Unsupported symmetric key algorithm: " + algorithmName);
        }
    }
}
