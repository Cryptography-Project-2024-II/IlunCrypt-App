package com.iluncrypt.iluncryptapp.models.enums;

import com.iluncrypt.iluncryptapp.models.Alphabet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enum representing predefined alphabets in the system.
 * Each preset includes a name, description, and a list of characters.
 */
public enum AlphabetPreset {

    /** Uppercase English alphabet (A-Z). */
    A_Z("A-Z", "Uppercase English Alphabet (A-Z)", Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    )),

    /** Uppercase and lowercase English alphabet (A-Z, a-z). */
    A_Z_a_z("A-Z a-z", "Upper and Lowercase English Alphabet (A-Z, a-z)", Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    )),

    /** Uppercase English alphabet with numbers (A-Z, 0-9). */
    A_Z_0_9("A-Z 0-9", "Uppercase English Alphabet with Numbers (A-Z, 0-9)", Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )),

    /** Full English alphanumeric set (A-Z, a-z, 0-9). */
    A_Z_a_z_0_9("A-Z a-z 0-9", "Full English Alphanumeric (A-Z, a-z, 0-9)", Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )),

    /** Uppercase with common punctuation. */
    A_Z_WITH_PUNCTUATION("A-Z with Punctuation", "Uppercase with common punctuation",
            appendCharacters(A_Z.characters, ". , ; : ' \" ! ? - ( )")),

    /** Upper and lowercase with punctuation. */
    A_Z_a_z_WITH_PUNCTUATION("A-Z a-z with Punctuation", "Upper and Lowercase with punctuation",
            appendCharacters(A_Z_a_z.characters, ". , ; : ' \" ! ? - ( )")),

    /** Uppercase, numbers, and punctuation. */
    A_Z_0_9_WITH_PUNCTUATION("A-Z 0-9 with Punctuation", "Uppercase, numbers, and punctuation",
            appendCharacters(A_Z_0_9.characters, ". , ; : ' \" ! ? - ( )")),

    /** Full alphanumeric with punctuation. */
    A_Z_a_z_0_9_WITH_PUNCTUATION("A-Z a-z 0-9 with Punctuation", "Full Alphanumeric with punctuation",
            appendCharacters(A_Z_a_z_0_9.characters, ". , ; : ' \" ! ? - ( )")),

    /** Expanded alphabet covering multiple languages. */
    A_Z_a_z_0_9_WITH_DIACRITICS("A-Z a-z 0-9 with Diacritics", "Expanded Alphabet covering multiple languages",
            appendCharacters(A_Z_a_z_0_9_WITH_PUNCTUATION.characters,
                    "Á É Í Ó Ú Ü Ñ à â æ ç é è ê ë î ï ô œ ù û ü ÿ " +
                            "Å Ä Ö Ø æ å ä ö ø ß Ā Ă Ą Ć Ĉ Ċ Č Ď Đ Ē Ĕ Ė Ę Ě Ĝ Ğ Ġ Ģ Ĥ Ħ Ĩ Ī Ĭ Į İ Ĳ Ĵ Ķ Ĺ Ļ Ľ Ł " +
                            "Ń Ņ Ň Ŋ Ō Ŏ Ő Ŕ Ř Ś Ŝ Ş Š Ţ Ť Ŧ Ũ Ū Ŭ Ů Ű Ų Ŵ Ŷ Ÿ Ź Ż Ž ẞ"
            ));

    /** Name of the alphabet preset. */
    private final String name;

    /** Description of the alphabet preset. */
    private final String description;

    /** List of characters contained in the preset. */
    private final List<Character> characters;

    /**
     * Constructor for initializing an AlphabetPreset.
     *
     * @param name        Name of the preset.
     * @param description Description of the preset.
     * @param characters  List of characters in the preset.
     */
    AlphabetPreset(String name, String description, List<Character> characters) {
        this.name = name;
        this.description = description;
        this.characters = characters;
    }

    /**
     * Gets the name of the alphabet preset.
     *
     * @return The name of the preset.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the alphabet preset.
     *
     * @return The description of the preset.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the size of the alphabet.
     *
     * @return The number of characters in the preset.
     */
    public int size() {
        return characters.size();
    }

    /**
     * Gets the list of characters in the preset.
     *
     * @return The list of characters.
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * Appends additional unique characters to an existing list.
     *
     * @param base       The base character list.
     * @param extraChars The additional characters to be appended.
     * @return A new list containing the base characters and the unique additional characters.
     */
    private static List<Character> appendCharacters(List<Character> base, String extraChars) {
        List<Character> result = new ArrayList<>(base);
        for (char c : extraChars.toCharArray()) {
            if (!result.contains(c)) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Returns a list of all predefined alphabets as Alphabet objects.
     *
     * @return A list of predefined Alphabet objects.
     */
    public static List<Alphabet> getPredefinedAlphabets() {
        List<Alphabet> predefined = new ArrayList<>();
        for (AlphabetPreset preset : values()) {
            predefined.add(new Alphabet(preset.getName(), preset.getDescription(), preset.getCharacters()));
        }
        return predefined;
    }

    /**
     * Retrieves an alphabet preset by its name.
     *
     * @param name The name of the preset.
     * @return The corresponding Alphabet object, or null if not found.
     */
    public static Alphabet getAlphabetByName(String name) {
        for (AlphabetPreset preset : values()) {
            if (preset.getName().equalsIgnoreCase(name)) {
                return new Alphabet(preset.getName(), preset.getDescription(), preset.getCharacters());
            }
        }
        return null;
    }
}
