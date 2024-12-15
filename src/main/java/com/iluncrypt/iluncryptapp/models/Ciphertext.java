package com.iluncrypt.iluncryptapp.models;

public class Ciphertext {
    private String text;

    public Ciphertext(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
