package com.adamreeve.whattimeistwit.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 7/4/12
 * Time: 10:45 PM
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

    public Classification getBestMatch() {
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
        float delta = best - second;
        if (best >= MIN_CONFIDENCE && (delta >= MIN_DELTA)) {
            return new Classification(bestLang, best);
        } else {
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
