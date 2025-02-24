package com.iluncrypt.iluncryptapp.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.ClassicCipherConfig;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.enums.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.iluncrypt.iluncryptapp.utils.config.ConfigManager.loadConfigFile;
import static com.iluncrypt.iluncryptapp.utils.config.ConfigManager.saveConfigToFile;

/**
 * Manages configuration settings for classic cipher methods.
 */
public class ClassicCipherConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final AlphabetConfig alphabetConfig = new AlphabetConfig();

    public ClassicCipherConfig loadClassicCipherConfig(String cipherMethodName) {
        Path configPath = Paths.get(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return getDefaultCipherConfig();
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Map<String, Object> rawConfig = GSON.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
            if (rawConfig == null || !rawConfig.containsKey("classical-ciphers-options")) {
                return getDefaultCipherConfig();
            }

            Map<String, Object> classicalCipherSettings = (Map<String, Object>) rawConfig.get("classical-ciphers-options");
            if (!classicalCipherSettings.containsKey(cipherMethodName)) {
                return getDefaultCipherConfig();
            }

            return GSON.fromJson(GSON.toJson(classicalCipherSettings.get(cipherMethodName)), ClassicCipherConfig.class);

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Warning: Invalid config.json detected. Ignoring and loading default cipher config: " + e.getMessage());
        }

        return getDefaultCipherConfig();
    }

    public static void saveClassicCipherConfig(String cipherMethodName, ClassicCipherConfig config) {
        Map<String, Object> configData = loadConfigFile();
        Map<String, Object> methodCipherSettings = (Map<String, Object>) configData.computeIfAbsent("classical-ciphers-options", k -> new HashMap<>());

        if (config.equals(getDefaultCipherConfig())) {
            methodCipherSettings.remove(cipherMethodName);
        } else {
            methodCipherSettings.put(cipherMethodName, config);
        }

        saveConfigToFile(configData);
    }

    static ClassicCipherConfig getDefaultCipherConfig() {
        return new ClassicCipherConfig(
                alphabetConfig.getAlphabetByName("A-Z"),
                alphabetConfig.getAlphabetByName("A-Z"),
                alphabetConfig.getAlphabetByName("A-Z"),
                CaseHandling.IGNORE,
                UnknownCharHandling.REMOVE,
                WhitespaceHandling.REMOVE
        );
    }
}
