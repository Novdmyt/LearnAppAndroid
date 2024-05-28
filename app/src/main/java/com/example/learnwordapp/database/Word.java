package com.example.learnwordapp.database;

public class Word {
    private String word;
    private String translation;

    public Word(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }
}
