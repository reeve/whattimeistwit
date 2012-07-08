package com.adamreeve.whattimeistwit.analysis;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 7/7/12
 * Time: 10:12 PM
 */
public class PeriodSummary {

    private Date start = new Date(Long.MAX_VALUE);
    private Date end = new Date(Long.MIN_VALUE);
    private double total;
    private Map<String, Double> countMap = new HashMap<>();

    public void add(Date timeStamp, String language) {
        total++;
        if (!countMap.containsKey(language)) {
            countMap.put(language, 1d);
        } else {
            countMap.put(language, countMap.get(language) + 1);
        }

        if (timeStamp.before(start)) {
            start = timeStamp;
        }

        if (timeStamp.after(end)) {
            end = timeStamp;
        }
    }

    public String toString() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        StringBuilder sb = new StringBuilder("Summary (").append(df.format(start)).append("-").append(df.format(end)).append(")");
        for (String lang : countMap.keySet()) {
            String entry = String.format("%s=%2.1f%%", lang, countMap.get(lang) / total * 100);
            sb.append(entry).append(",");
        }
        return sb.toString();
    }

}
