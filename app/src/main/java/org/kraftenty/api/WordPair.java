package org.kraftenty.api;

public class WordPair {
    private String korean;
    private String english;

    public WordPair(String korean, String english) {
        this.korean = korean;
        this.english = english;
    }

    public String getKorean() {
        return korean;
    }

    public String getEnglish() {
        return english;
    }
} 