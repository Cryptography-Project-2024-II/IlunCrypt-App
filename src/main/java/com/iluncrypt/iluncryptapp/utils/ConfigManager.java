package com.iluncrypt.iluncryptapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.CipherMethodConfig;
import com.iluncrypt.iluncryptapp.models.enums.AlphabetPreset;
import com.iluncrypt.iluncryptapp.models.enums.CaseHandling;
import com.iluncrypt.iluncryptapp.models.enums.UnknownCharHandling;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages configuration settings for different cipher methods using Gson.
 */
public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Loads the configuration for a given cipher method.
     * If the configuration file does not exist, it returns the default values.
     *
     * @param cipherMethodName The name of the cipher method.
     * @return A CipherMethodConfig object with the appropriate settings.
     */
    public static CipherMethodConfig loadCipherMethodConfig(String cipherMethodName) {
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, Map<String, CipherMethodConfigJson>>>() {}.getType();
                Map<String, Map<String, CipherMethodConfigJson>> configData = GSON.fromJson(reader, type);
                reader.close();

                if (configData != null && configData.containsKey("methodciphersettings") &&
                        configData.get("methodciphersettings").containsKey(cipherMethodName)) {

                    CipherMethodConfigJson jsonConfig = configData.get("methodciphersettings").get(cipherMethodName);

                    return new CipherMethodConfig(
                            getAlphabetByName(jsonConfig.plaintextAlphabet),
                            getAlphabetByName(jsonConfig.ciphertextAlphabet),
                            getAlphabetByName(jsonConfig.keyAlphabet),
                            jsonConfig.caseHandling,
                            jsonConfig.unknownCharHandling
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading cipher method configuration: " + e.getMessage());
        }

        // Default values if no config.json exists or if the method is missing
        return new CipherMethodConfig(
                AlphabetPreset.getAlphabetByName("A-Z"),
                AlphabetPreset.getAlphabetByName("A-Z"),
                AlphabetPreset.getAlphabetByName("A-Z"),
                CaseHandling.IGNORE,
                UnknownCharHandling.REMOVE
        );
    }

    /**
     * Saves the configuration for a given cipher method.
     *
     * @param cipherMethodName The name of the cipher method.
     * @param config The CipherMethodConfig object containing the settings to save.
     */
    public static void saveCipherMethodConfig(String cipherMethodName, CipherMethodConfig config) {
        try {
            Map<String, Map<String, CipherMethodConfigJson>> configData = new HashMap<>();

            // If the config file exists, read its current content
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, Map<String, CipherMethodConfigJson>>>() {}.getType();
                configData = GSON.fromJson(reader, type);
                reader.close();
            }

            // Ensure the methodciphersettings section exists
            configData.computeIfAbsent("methodciphersettings", k -> new HashMap<>());

            // Convert CipherMethodConfig to JSON format
            CipherMethodConfigJson jsonConfig = new CipherMethodConfigJson(config);

            // Store the new configuration
            configData.get("methodciphersettings").put(cipherMethodName, jsonConfig);

            // Write updated data back to the file
            FileWriter writer = new FileWriter(CONFIG_FILE);
            GSON.toJson(configData, writer);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving cipher method configuration: " + e.getMessage());
        }
    }

    /**
     * Retrieves an alphabet by name, checking both predefined and custom alphabets.
     *
     * @param name The name of the alphabet.
     * @return The corresponding Alphabet object.
     */
    private static Alphabet getAlphabetByName(String name) {
        Alphabet alphabet = AlphabetPreset.getAlphabetByName(name);
        if (alphabet == null) {
            alphabet = loadCustomAlphabet(name);
        }
        return alphabet;
    }

    /**
     * Loads a custom alphabet from its corresponding JSON file.
     *
     * @param alphabetName The name of the custom alphabet.
     * @return The Alphabet object if found, otherwise null.
     */
    private static Alphabet loadCustomAlphabet(String alphabetName) {
        try {
            if (!Files.exists(Paths.get(CONFIG_FILE))) return null;

            FileReader reader = new FileReader(CONFIG_FILE);
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> customAlphabets = GSON.fromJson(reader, type);
            reader.close();

            if (customAlphabets != null && customAlphabets.containsKey(alphabetName)) {
                String fileName = customAlphabets.get(alphabetName);
                FileReader alphabetReader = new FileReader(fileName);
                Alphabet alphabet = GSON.fromJson(alphabetReader, Alphabet.class);
                alphabetReader.close();
                return alphabet;
            }
        } catch (IOException e) {
            System.err.println("Error loading custom alphabet: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves a custom alphabet to a JSON file and registers it in the config.
     *
     * @param alphabet The Alphabet object to save.
     */
    public static void saveCustomAlphabet(Alphabet alphabet) {
        try {
            String fileName = alphabet.getName() + ".json";

            // Save alphabet to its own file
            FileWriter writer = new FileWriter(fileName);
            GSON.toJson(alphabet, writer);
            writer.close();

            // Register in config.json
            Map<String, String> customAlphabets = new HashMap<>();

            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                customAlphabets = GSON.fromJson(reader, type);
                reader.close();
            }

            customAlphabets.put(alphabet.getName(), fileName);

            FileWriter configWriter = new FileWriter(CONFIG_FILE);
            GSON.toJson(customAlphabets, configWriter);
            configWriter.close();
        } catch (IOException e) {
            System.err.println("Error saving custom alphabet: " + e.getMessage());
        }
    }

    /**
     * Internal class to store cipher method configuration in JSON format.
     */
    private static class CipherMethodConfigJson {
        private String plaintextAlphabet;
        private String ciphertextAlphabet;
        private String keyAlphabet;
        private CaseHandling caseHandling;
        private UnknownCharHandling unknownCharHandling;

        CipherMethodConfigJson(CipherMethodConfig config) {
            this.plaintextAlphabet = config.getPlaintextAlphabet().getName();
            this.ciphertextAlphabet = config.getCiphertextAlphabet().getName();
            this.keyAlphabet = config.getKeyAlphabet().getName();
            this.caseHandling = config.getCaseHandling();
            this.unknownCharHandling = config.getUnknownCharHandling();
        }
    }
}
