package com.iluncrypt.iluncryptapp.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.ApplicationSettings;
import com.iluncrypt.iluncryptapp.models.ClassicCipherConfig;
import com.iluncrypt.iluncryptapp.models.SymmetricKeyConfig;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.enums.Language;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all configurations of the application by delegating to specific managers.
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ApplicationConfig appConfig = new ApplicationConfig();
    private static final ClassicCipherConfigManager classicCipherConfig = new ClassicCipherConfigManager();
    private static final SymmetricKeyConfigManager symmetricKeyConfig = new SymmetricKeyConfigManager();
    private static final AlphabetConfig alphabetConfig = new AlphabetConfig();

    // ======== Application Settings ========

    /**
     * Loads application settings.
     * @return ApplicationSettings instance.
     */
    public static ApplicationSettings loadApplicationSettings() {
        return appConfig.loadApplicationSettings();
    }

    /**
     * Saves application settings.
     * @param settings The ApplicationSettings object to save.
     */
    public static void saveApplicationSettings(ApplicationSettings settings) {
        appConfig.saveApplicationSettings(settings);
    }

    /**
     * Retrieves the current application language.
     * @return Language enum representing the app language.
     */
    public static Language getApplicationLanguage() {
        return appConfig.getApplicationLanguage();
    }

    /**
     * Sets the application language.
     * @param language The language to set.
     */
    public static void setApplicationLanguage(Language language) {
        appConfig.setApplicationLanguage(language);
    }

    /**
     * Gets the default application settings.
     * @return Default ApplicationSettings instance.
     */
    public static ApplicationSettings getDefaultApplicationSettings() {
        return appConfig.getDefaultApplicationSettings();
    }

    // ======== Classic Cipher Configuration ========

    /**
     * Loads configuration for a classic cipher method.
     * @param cipherMethodName The name of the cipher method.
     * @return ClassicCipherConfig instance.
     */
    public static ClassicCipherConfig loadClassicCipherConfig(String cipherMethodName) {
        return classicCipherConfig.loadClassicCipherConfig(cipherMethodName);
    }

    /**
     * Saves configuration for a classic cipher method.
     * @param cipherMethodName The name of the cipher method.
     * @param config The ClassicCipherConfig object to save.
     */
    public static void saveClassicCipherConfig(String cipherMethodName, ClassicCipherConfig config) {
        classicCipherConfig.saveClassicCipherConfig(cipherMethodName, config);
    }

    /**
     * Gets the default configuration for classic cipher methods.
     * @return Default ClassicCipherConfig instance.
     */
    public static ClassicCipherConfig getDefaultClassicCipherConfig() {
        return classicCipherConfig.getDefaultCipherConfig();
    }

    // ======== Symmetric Key Encryption Configuration ========

    /**
     * Loads the symmetric key encryption configuration.
     * @return SymmetricKeyConfig instance.
     */
    public static SymmetricKeyConfig loadSymmetricKeyConfig(String algorithmName) {
        return symmetricKeyConfig.loadSymmetricKeyConfig(algorithmName);
    }

    /**
     * Saves the symmetric key encryption configuration.
     * @param config The SymmetricKeyConfig object to save.
     */
    public static void saveSymmetricKeyConfig(SymmetricKeyConfig config) {
        symmetricKeyConfig.saveSymmetricKeyConfig(config);
    }

    /**
     * Gets the default configuration for symmetric key encryption.
     * @return Default SymmetricKeyConfig instance.
     */
    public static SymmetricKeyConfig getDefaultSymmetricKeyConfig(String algorithmName) {
        return symmetricKeyConfig.getDefaultSymmetricKeyConfig(algorithmName);
    }

    // ======== Alphabet Management ========

    /**
     * Retrieves an alphabet by name.
     * @param name The name of the alphabet.
     * @return The corresponding Alphabet object.
     */
    public static Alphabet getAlphabetByName(String name) {
        return alphabetConfig.getAlphabetByName(name);
    }

    /**
     * Saves a custom alphabet.
     * @param alphabet The Alphabet object to save.
     */
    public static void saveCustomAlphabet(Alphabet alphabet) {
        alphabetConfig.saveCustomAlphabet(alphabet);
    }

    /**
     * Retrieves all available alphabets (predefined and custom).
     * @return A list of Alphabet objects.
     */
    public static List<Alphabet> getAllAlphabets() {
        return alphabetConfig.getAllAlphabets();
    }

    /**
     * Gets the default alphabet.
     * @return Default Alphabet instance.
     */
    public static Alphabet getDefaultAlphabet() {
        return alphabetConfig.getDefaultAlphabet();
    }

    /**
     * Deletes the corrupted config file and restores default settings.
     */
    public static void resetConfigFile() {
        try {
            Files.deleteIfExists(Paths.get(CONFIG_FILE));
            System.out.println("Configuration reset to default values.");
        } catch (IOException e) {
            System.err.println("Failed to delete config.json: " + e.getMessage());
        }
    }

    static Map<String, Object> loadConfigFile() {
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                return GSON.fromJson(reader, type);
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Warning: config.json is corrupted. Ignoring file: " + e.getMessage());
        }
        return new HashMap<>();
    }

    static void saveConfigToFile(Map<String, Object> configData) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
        } catch (IOException e) {
            System.err.println("Error: Unable to save configuration: " + e.getMessage());
        }
    }
}
