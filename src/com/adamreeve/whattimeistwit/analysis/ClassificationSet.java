package com.adamreeve.whattimeistwit.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 7/4/12
 * Time: 10:45 PM
 */
public class ClassificationSet {

    private Map<String, Float> certaintyMap = new HashMap<>();

    public void setCertainty(String lang, Float value) {
        certaintyMap.put(lang, value);
    }

    public Float getCertainty(String lang) {
        return certaintyMap.get(lang);
    }

    public Classification getBestMatch() {
        Float best = 0f;
        String bestLang = null;
        for (Map.Entry<String, Float> entry : certaintyMap.entrySet()) {
            Float value = entry.getValue();
            if (value > best) {
                best = value;
                bestLang = entry.getKey();
            }
        }
        if (best >= 0.5) {
            return new Classification(bestLang, best);
        } else {
            return new Classification(Classification.UNKNOWN, 1 - best);
        }
    }

    @Override
    public String toString() {
        return "ClassificationSet{" +
                "certaintyMap=" + certaintyMap +
                '}';
    }
}
