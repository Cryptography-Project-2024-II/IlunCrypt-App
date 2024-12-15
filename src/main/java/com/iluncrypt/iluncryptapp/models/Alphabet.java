package com.iluncrypt.iluncryptapp.models;

import java.util.Set;

public class Alphabet {
    private Set<Character> characters;

    public Alphabet(Set<Character> characters) {
        this.characters = characters;
    }

    public boolean isValidChar(char c) {
        return characters.contains(c);
    }

    public Set<Character> getCharacters() {
        return characters;
    }
}
