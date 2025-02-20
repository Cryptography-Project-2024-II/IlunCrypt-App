package com.iluncrypt.iluncryptapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.iluncrypt.iluncryptapp.models.AESConfig;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.ClassicCipherConfig;
import com.iluncrypt.iluncryptapp.models.enums.*;
import com.iluncrypt.iluncryptapp.models.enums.symmetrickey.aes.*;

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

    public static AESConfig loadAESConfig() {
        Path configPath = Paths.get(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            return getDefaultAESConfig();
        }

        try {
            String jsonContent = Files.readString(configPath).trim();

            if (jsonContent.isEmpty()) {
                return getDefaultAESConfig();
            }

            // Leer el JSON como un mapa genérico
            Map<String, Object> rawConfig = GSON.fromJson(jsonContent, new TypeToken<Map<String, Object>>() {}.getType());

            if (rawConfig == null || !rawConfig.containsKey("methodciphersettings")) {
                return getDefaultAESConfig();
            }

            // Acceder a "methodciphersettings" y luego a "AES"
            Map<String, Object> methodCipherSettings = (Map<String, Object>) rawConfig.get("methodciphersettings");
            if (!methodCipherSettings.containsKey("AES")) {
                return getDefaultAESConfig();
            }

            AESConfigJson jsonConfig = GSON.fromJson(GSON.toJson(methodCipherSettings.get("AES")), AESConfigJson.class);
            return jsonConfig.toAESConfig();

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading AES configuration: " + e.getMessage());
        }

        return getDefaultAESConfig();
    }


    public static ClassicCipherConfig loadClassicCipherConfig(String cipherMethodName) {
        Path configPath = Paths.get(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            return getDefaultCipherConfig();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            String jsonContent = Files.readString(configPath).trim();

            if (jsonContent.isEmpty()) {
                return getDefaultCipherConfig();
            }

            Map<String, Object> rawConfig = GSON.fromJson(jsonContent, new TypeToken<Map<String, Object>>() {}.getType());

            if (rawConfig == null || !rawConfig.containsKey("methodciphersettings")) {
                return getDefaultCipherConfig();
            }

            Type methodConfigType = new TypeToken<Map<String, CipherMethodConfigJson>>() {}.getType();
            Map<String, CipherMethodConfigJson> methodSettings = GSON.fromJson(GSON.toJson(rawConfig.get("methodciphersettings")), methodConfigType);

            if (methodSettings == null || !methodSettings.containsKey(cipherMethodName)) {
                return getDefaultCipherConfig();
            }

            CipherMethodConfigJson jsonConfig = methodSettings.get(cipherMethodName);
            return new ClassicCipherConfig(
                    getAlphabetByName(jsonConfig.plaintextAlphabet),
                    getAlphabetByName(jsonConfig.ciphertextAlphabet),
                    getAlphabetByName(jsonConfig.keyAlphabet),
                    jsonConfig.caseHandling,
                    jsonConfig.unknownCharHandling,
                    jsonConfig.whitespaceHandling
            );

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading classic cipher configuration: " + e.getMessage());
        }

        return getDefaultCipherConfig();
    }



    public static AESConfig getDefaultAESConfig() {
        return new AESConfig(
                KeySize.AES_256,
                AESMode.GCM,
                PaddingScheme.NO_PADDING,
                IVSize.IV_12,
                GCMTagSize.TAG_128,
                AuthenticationMethod.NONE,
                true // Store algorithm by default
        );
    }





    /**
     * Devuelve la configuración por defecto para los métodos de cifrado.
     */
    private static ClassicCipherConfig getDefaultCipherConfig() {
        return new ClassicCipherConfig(
                AlphabetPreset.getAlphabetByName("A-Z"),
                AlphabetPreset.getAlphabetByName("A-Z"),
                AlphabetPreset.getAlphabetByName("A-Z"),
                CaseHandling.IGNORE,
                UnknownCharHandling.REMOVE,
                WhitespaceHandling.REMOVE
        );
    }


    /**
     * Saves an AES cipher configuration to the config file.
     *
     * @param config The AES configuration to save.
     */
    public static void saveAESConfig(AESConfig config) {
        Map<String, Map<String, Object>> configData = loadCipherMethodConfig();

        configData.computeIfAbsent("methodciphersettings", k -> new HashMap<>());

        AESConfigJson jsonConfig = new AESConfigJson(config);

        //  Compara si la configuración es igual a la configuración por defecto
        AESConfigJson defaultConfig = new AESConfigJson(getDefaultAESConfig());
        if (config.equals(defaultConfig.toAESConfig())) {
            configData.get("methodciphersettings").remove("AES"); // Elimina "AES" si es igual a la por defecto
        } else {
            configData.get("methodciphersettings").put("AES", jsonConfig);
        }

        //  Si "methodciphersettings" queda vacío, también se elimina
        if (configData.get("methodciphersettings").isEmpty()) {
            configData.remove("methodciphersettings");
        }

        //  Si no quedan configuraciones en el JSON, elimina el archivo
        if (configData.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(CONFIG_FILE)); // Elimina el archivo si está vacío
                return; // No se guarda un archivo vacío
            } catch (IOException e) {
                System.err.println("Error deleting empty config file: " + e.getMessage());
            }
        }

        //  Guardar la configuración si aún hay datos
        saveConfigToFile(configData);
    }


    /**
     * Saves a Classic Cipher configuration to the config file.
     *
     * @param cipherMethodName The name of the cipher method (e.g., "Affine", "Caesar").
     * @param config           The Classic Cipher configuration to save.
     */
    public static void saveClassicCipherConfig(String cipherMethodName, ClassicCipherConfig config) {
        Map<String, Map<String, Object>> configData = loadCipherMethodConfig();

        configData.computeIfAbsent("methodciphersettings", k -> new HashMap<>());

        CipherMethodConfigJson jsonConfig = new CipherMethodConfigJson(config);
        configData.get("methodciphersettings").put(cipherMethodName, jsonConfig);

        saveConfigToFile(configData);
    }

    /**
     * Loads the existing cipher method configuration from the config file.
     *
     * @return A map containing the loaded configuration.
     */
    private static Map<String, Map<String, Object>> loadCipherMethodConfig() {
        Map<String, Map<String, Object>> configData = new HashMap<>();

        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                FileReader reader = new FileReader(CONFIG_FILE);
                Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
                configData = GSON.fromJson(reader, type);
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Error loading cipher method configuration: " + e.getMessage());
        }

        return configData;
    }

    /**
     * Writes the updated configuration data to the config file.
     *
     * @param configData The map containing the updated configuration data.
     */
    private static void saveConfigToFile(Map<String, Map<String, Object>> configData) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
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

        CipherMethodConfigJson(ClassicCipherConfig config) {
            this.plaintextAlphabet = config.getPlaintextAlphabet().getName();
            this.ciphertextAlphabet = config.getCiphertextAlphabet().getName();
            this.keyAlphabet = config.getKeyAlphabet().getName();
            this.caseHandling = config.getCaseHandling();
            this.unknownCharHandling = config.getUnknownCharHandling();
            this.whitespaceHandling = config.getWhitespaceHandling();
        }
    }

    /**
     * Internal class to store AES configuration in JSON format.
     */
    private static class AESConfigJson {
        private int keySize;
        private AESMode mode;
        private PaddingScheme paddingScheme;
        private int ivSize;
        private int gcmTagSize;
        private AuthenticationMethod authMethod;
        private boolean storeAlgorithm;

        AESConfigJson(AESConfig config) {
            this.keySize = config.getKeySize().getSize();
            this.mode = config.getMode();
            this.paddingScheme = config.getPaddingScheme();
            this.ivSize = config.getIvSize().getSize();
            this.gcmTagSize = config.getGcmTagSize().getSize();
            this.authMethod = config.getAuthMethod();
            this.storeAlgorithm = config.isStoreAlgorithm();
        }

        AESConfig toAESConfig() {
            return new AESConfig(
                    KeySize.fromSize(keySize),
                    mode,
                    paddingScheme,
                    IVSize.fromSize(ivSize),
                    GCMTagSize.fromSize(gcmTagSize),
                    authMethod,
                    storeAlgorithm
            );
        }
    }


}
