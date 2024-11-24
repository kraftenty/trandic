package org.kraftenty.api;

public class WordPair {
    private String english;
    private String korean;

    public WordPair(String english, String korean) {
        this.english = english;
        this.korean = korean;
    }

    public String getEnglish() {
        return english;
    }

    public String getKorean() {
        return korean;
    }
} 