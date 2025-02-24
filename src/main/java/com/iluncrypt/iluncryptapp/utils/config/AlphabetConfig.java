package com.iluncrypt.iluncryptapp.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.iluncrypt.iluncryptapp.models.Alphabet;
import com.iluncrypt.iluncryptapp.models.enums.AlphabetPreset;

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
import com.google.gson.reflect.TypeToken;

/**
 * Manages alphabet configurations.
 */
public class AlphabetConfig {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Alphabet getAlphabetByName(String name) {
        Alphabet alphabet = AlphabetPreset.getAlphabetByName(name);
        return (alphabet != null) ? alphabet : loadCustomAlphabet(name);
    }

    private Alphabet loadCustomAlphabet(String name) {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> customAlphabets = GSON.fromJson(reader, type);

            if (customAlphabets != null && customAlphabets.containsKey(name)) {
                return GSON.fromJson(new FileReader(customAlphabets.get(name)), Alphabet.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading custom alphabet: " + e.getMessage());
        }
        return null;
    }

    public void saveCustomAlphabet(Alphabet alphabet) {
        try {
            String fileName = alphabet.getName() + ".json";
            FileWriter writer = new FileWriter(fileName);
            GSON.toJson(alphabet, writer);
            writer.close();

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

    public List<Alphabet> getAllAlphabets() {
        List<Alphabet> allAlphabets = new ArrayList<>();

        // Adding predefined alphabets
        allAlphabets.addAll(AlphabetPreset.getPredefinedAlphabets());

        // Adding custom alphabets
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

    public Alphabet getDefaultAlphabet() {
        return AlphabetPreset.getAlphabetByName("A-Z");
    }
}
