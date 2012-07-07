package com.adamreeve.whattimeistwit.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 7/4/12
 * Time: 10:45 PM
 */
public class Classification {

    private Map<String, Float> certaintyMap = new HashMap<>();

    public void setCertainty(String lang, Float value) {
        certaintyMap.put(lang, value);
    }

    public Float getCertainty(String lang) {
        return certaintyMap.get(lang);
    }

    public String getBestMatchLang() {
        Float best = 0f;
        String bestLang = null;
        for (Map.Entry<String, Float> entry : certaintyMap.entrySet()) {
            Float value = entry.getValue();
            if (value > best) {
                best = value;
                bestLang = entry.getKey();
            }
        }
        return bestLang;
    }

    @Override
    public String toString() {
        return "Classification{" +
                "certaintyMap=" + certaintyMap +
                '}';
    }
}
