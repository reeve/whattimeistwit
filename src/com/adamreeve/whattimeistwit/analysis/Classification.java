package com.adamreeve.whattimeistwit.analysis;

/**
 * Represents a single score for a tweet/language pair
 * <p/>
 */
public class Classification {
    private String language;
    private Float certainty;

    public static final String UNKNOWN = "UN";

    public Classification(String language, Float certainty) {
        this.language = language;
        this.certainty = certainty;
    }

    public String getLanguage() {
        return language;
    }

    public Float getCertainty() {
        return certainty;
    }

    public String toSimpleString() {
        return String.format("%s|%3.0f%%", language, certainty * 100);
    }

    @Override
    public String toString() {
        return "Classification{" +
                "language='" + language + '\'' +
                ", certainty=" + certainty +
                '}';
    }
}
