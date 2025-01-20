package com.iluncrypt.iluncryptapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.CipherMethodConfig;
import com.iluncrypt.iluncryptapp.models.enums.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Path configPath = Paths.get(CONFIG_FILE);

        // Si el archivo no existe, devolver configuración por defecto
        if (!Files.exists(configPath)) {
            return getDefaultCipherConfig();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            // Leer el contenido del archivo
            String jsonContent = Files.readString(configPath).trim();

            // Si el archivo está vacío, devolver valores por defecto
            if (jsonContent.isEmpty()) {
                System.err.println("Advertencia: config.json está vacío.");
                return getDefaultCipherConfig();
            }

            // Intentar parsear el JSON a un mapa genérico
            Map<String, Object> rawConfig = GSON.fromJson(jsonContent, new TypeToken<Map<String, Object>>() {}.getType());

            // Si el JSON no contiene "methodciphersettings", devolver valores por defecto
            if (rawConfig == null || !rawConfig.containsKey("methodciphersettings")) {
                System.err.println("Advertencia: 'methodciphersettings' no encontrado en config.json.");
                return getDefaultCipherConfig();
            }

            // Convertir solo la parte relevante a su tipo esperado
            Type methodConfigType = new TypeToken<Map<String, CipherMethodConfigJson>>() {}.getType();
            Map<String, CipherMethodConfigJson> methodSettings = GSON.fromJson(GSON.toJson(rawConfig.get("methodciphersettings")), methodConfigType);

            // Si la clave no existe para el método buscado, devolver valores por defecto
            if (methodSettings == null || !methodSettings.containsKey(cipherMethodName)) {
                System.err.println("Advertencia: Configuración para " + cipherMethodName + " no encontrada.");
                return getDefaultCipherConfig();
            }

            // Obtener la configuración del método de cifrado
            CipherMethodConfigJson jsonConfig = methodSettings.get(cipherMethodName);

            return new CipherMethodConfig(
                    getAlphabetByName(jsonConfig.plaintextAlphabet),
                    getAlphabetByName(jsonConfig.ciphertextAlphabet),
                    getAlphabetByName(jsonConfig.keyAlphabet),
                    jsonConfig.caseHandling,
                    jsonConfig.unknownCharHandling,
                    jsonConfig.whitespaceHandling
            );

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error al cargar la configuración de cifrado: " + e.getMessage());
        }

        // Si hubo un error, devolver valores por defecto
        return getDefaultCipherConfig();
    }



    /**
     * Devuelve la configuración por defecto para los métodos de cifrado.
     */
    private static CipherMethodConfig getDefaultCipherConfig() {
        return new CipherMethodConfig(
                AlphabetPreset.getAlphabetByName("A-Z"),
                AlphabetPreset.getAlphabetByName("A-Z"),
                AlphabetPreset.getAlphabetByName("A-Z"),
                CaseHandling.IGNORE,
                UnknownCharHandling.REMOVE,
                WhitespaceHandling.REMOVE
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

    public static List<Alphabet> getAllAlphabets() {
        List<Alphabet> allAlphabets = new ArrayList<>();

        // Agregar los alfabetos predefinidos
        allAlphabets.addAll(AlphabetPreset.getPredefinedAlphabets());

        // Agregar los alfabetos personalizados si existen en config.json
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> configData = GSON.fromJson(reader, type);

                if (configData != null && configData.containsKey("customAlphabets")) {
                    Type customAlphabetType = new TypeToken<Map<String, String>>() {}.getType();
                    Map<String, String> customAlphabets = GSON.fromJson(GSON.toJson(configData.get("customAlphabets")), customAlphabetType);

                    for (String alphabetName : customAlphabets.keySet()) {
                        Alphabet customAlphabet = loadCustomAlphabet(alphabetName);
                        if (customAlphabet != null) {
                            allAlphabets.add(customAlphabet);
                        }
                    }
                }
            } catch (IOException | JsonSyntaxException e) {
                System.err.println("Error loading custom alphabets: " + e.getMessage());
            }
        }

        return allAlphabets;
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
     * Retrieves the currently set application language from config.json.
     * Defaults to ENGLISH if the setting is missing.
     *
     * @return The application language as a Language enum.
     */
    public static Language getApplicationLanguage() {
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> configData = GSON.fromJson(reader, type);
                reader.close();

                if (configData != null && configData.containsKey("applicationLanguage")) {
                    return Language.fromDisplayName(configData.get("applicationLanguage").toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading application language: " + e.getMessage());
        }

        return Language.ENGLISH; // Default language
    }

    /**
     * Sets or removes the application language in config.json.
     * If the language is ENGLISH, the key is removed from the config file.
     * If no other settings exist, the file is deleted.
     *
     * @param language The new application language as a Language enum.
     */
    public static void setApplicationLanguage(Language language) {
        try {
            Map<String, Object> configData = new HashMap<>();

            // 🔹 Cargar el archivo de configuración si existe
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                configData = GSON.fromJson(reader, type);
                reader.close();
            }

            // 🔹 Si el idioma es inglés (por defecto), eliminar la clave
            if (language == Language.ENGLISH) {
                configData.remove("applicationLanguage");
            } else {
                configData.put("applicationLanguage", language.getDisplayName());
            }

            // 🔹 Si después de eliminar la clave el archivo queda vacío, eliminarlo
            if (configData.isEmpty()) {
                Files.deleteIfExists(Paths.get(CONFIG_FILE));
            } else {
                // 🔹 Si todavía hay configuraciones, escribir el archivo actualizado
                FileWriter writer = new FileWriter(CONFIG_FILE);
                GSON.toJson(configData, writer);
                writer.close();
            }

        } catch (IOException e) {
            System.err.println("Error saving application language: " + e.getMessage());
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
        private WhitespaceHandling whitespaceHandling;

        CipherMethodConfigJson(CipherMethodConfig config) {
            this.plaintextAlphabet = config.getPlaintextAlphabet().getName();
            this.ciphertextAlphabet = config.getCiphertextAlphabet().getName();
            this.keyAlphabet = config.getKeyAlphabet().getName();
            this.caseHandling = config.getCaseHandling();
            this.unknownCharHandling = config.getUnknownCharHandling();
            this.whitespaceHandling = config.getWhitespaceHandling();
        }
    }
}
