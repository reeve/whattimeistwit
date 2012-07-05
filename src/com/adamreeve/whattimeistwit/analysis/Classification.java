package com.adamreeve.whattimeistwit.analysis;

import java.util.Map;

/**
 * Date: 7/4/12
 * Time: 10:45 PM
 */
public class Classification {

    private Map<String, Byte> certaintyMap;

    public void setCertainty(String lang, Byte value) {
        certaintyMap.put(lang, value);
    }

    public Byte getCertainty(String lang) {
        return certaintyMap.get(lang);
    }

    public String getBestMatchLang() {
        Byte best = 0;
        String bestLang = null;
        for (Map.Entry<String, Byte> entry : certaintyMap.entrySet()) {
            Byte value = entry.getValue();
            if (value > best) {
                best = value;
                bestLang = entry.getKey();
            }
        }
        return bestLang;
    }

}
