package com.iluncrypt.iluncryptapp.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.ApplicationSettings;
import com.iluncrypt.iluncryptapp.models.enums.Language;

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
 * Manages application-wide settings.
 */
public class ApplicationConfig {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ApplicationSettings loadApplicationSettings() {
        Path configPath = Paths.get(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return getDefaultApplicationSettings();
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            String content = Files.readString(configPath).trim();
            if (content.isEmpty()) {
                Files.deleteIfExists(configPath);
                return getDefaultApplicationSettings();
            }
            Map<String, Object> rawConfig = GSON.fromJson(content, new TypeToken<Map<String, Object>>() {}.getType());
            if (rawConfig == null || !rawConfig.containsKey("application-settings")) {
                return getDefaultApplicationSettings();
            }

            return GSON.fromJson(GSON.toJson(rawConfig.get("application-settings")), ApplicationSettings.class);


        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Warning: Invalid config.json detected. Ignoring and loading default settings: " + e.getMessage());
        }

        return getDefaultApplicationSettings();
    }

    public void saveApplicationSettings(ApplicationSettings settings) {
        Map<String, Object> configData = loadConfigFile();

        if (settings.equals(getDefaultApplicationSettings())) {
            configData.remove("application-settings");
        } else {
            configData.put("application-settings", settings);
        }

        saveConfigToFile(configData);
    }

    public Language getApplicationLanguage() {
        ApplicationSettings settings = loadApplicationSettings();
        return settings.getLanguage();
    }

    public void setApplicationLanguage(Language language) {
        ApplicationSettings settings = loadApplicationSettings();
        settings.setLanguage(language);
        saveApplicationSettings(settings);
    }

    public ApplicationSettings getDefaultApplicationSettings() {
        return new ApplicationSettings(Language.ENGLISH, true, false, true);
    }
}
