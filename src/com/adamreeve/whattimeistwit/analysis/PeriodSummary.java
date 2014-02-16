package com.adamreeve.whattimeistwit.analysis;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Date: 7/7/12 Time: 10:12 PM
 */
public class PeriodSummary {

    private Date start = new Date(Long.MAX_VALUE);
    private Date end = new Date(Long.MIN_VALUE);
    private double total;
    private boolean autoRange;
    private SortedMap<String, Long> countMap = new TreeMap<>();

    public PeriodSummary() {
        autoRange = true;
    }

    public PeriodSummary(Date start, Date end) {
        this.start = start;
        this.end = end;
        autoRange = false;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void add(Date timeStamp, String language) {
        total++;
        if (!countMap.containsKey(language)) {
            countMap.put(language, 1l);
        } else {
            countMap.put(language, countMap.get(language) + 1);
        }

        if (autoRange) {
            if (timeStamp.before(start)) {
                start = timeStamp;
            }

            if (timeStamp.after(end)) {
                end = timeStamp;
            }
        }
    }

    public String toString() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        StringBuilder sb = new StringBuilder("Summary (").append(df.format(start))
                .append("-")
                .append(df.format(end))
                .append(") ");
        for (String lang : countMap.keySet()) {
            String entry = String.format("%s=%2.1f%%", lang, countMap.get(lang) / total * 100);
            sb.append(entry).append(",");
        }
        return sb.toString();
    }

    public void merge(PeriodSummary other) {
        if (other.getStart().before(start)) {
            start = other.getStart();
        }

        if (other.getEnd().after(end)) {
            end = other.getEnd();
        }

        for (Map.Entry<String, Long> entry : other.countMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                add(start, entry.getKey());
            }
        }
    }

    protected Map<String, Long> getData() {
        return Collections.unmodifiableMap(countMap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PeriodSummary that = (PeriodSummary) o;

        if (!end.equals(that.end)) {
            return false;
        }
        if (!start.equals(that.start)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
