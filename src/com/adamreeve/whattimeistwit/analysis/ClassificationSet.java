package com.adamreeve.whattimeistwit.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * A collection of all the classifications for a given tweet
 * <p/>
 * Date: 7/4/12 Time: 10:45 PM
 */
public class ClassificationSet {

    public static final double MIN_CONFIDENCE = 0.2;
    public static final double MIN_DELTA = 0.1;
    private Map<String, Float> certaintyMap = new HashMap<>();

    public void setCertainty(String lang, Float value) {
        certaintyMap.put(lang, value);
    }

    public Float getCertainty(String lang) {
        return certaintyMap.get(lang);
    }

    /**
     * Returns the best match found.
     *
     * @return the language code with the highest probability, or UN (unknown) if no conclusive result was found
     */
    public Classification getBestMatch() {
        // track the best and second best
        Float best = 0f;
        Float second = 0f;
        String bestLang = null;
        for (Map.Entry<String, Float> entry : certaintyMap.entrySet()) {
            Float value = entry.getValue();
            if (value >= best) {
                second = best;
                best = value;
                bestLang = entry.getKey();
            }
        }

        // figure out the delta between first and second
        float delta = best - second;
        if (best >= MIN_CONFIDENCE && (delta >= MIN_DELTA)) {
            // best match was persuasive, return it
            return new Classification(bestLang, best);
        } else {
            // or not so much, return unknown (delta is the indication of how close we were to a match)
            return new Classification(Classification.UNKNOWN, delta);
        }
    }

    @Override
    public String toString() {
        return "ClassificationSet{" +
                "certaintyMap=" + certaintyMap +
                '}';
    }
}
