package com.adamreeve.whattimeistwit.analysis;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents the aggregate percentages of tweets identified into each language over a time span.
 * <p/>
 * Date: 7/7/12 Time: 10:12 PM
 */
public class PeriodSummary {

    private Date start = new Date(Long.MAX_VALUE);
    private Date end = new Date(Long.MIN_VALUE);
    private double total;
    private boolean autoRange;
    private SortedMap<String, Long> countMap = new TreeMap<>();

    /**
     * Creates an instance which sets it's start and end automatically
     */
    public PeriodSummary() {
        autoRange = true;
    }

    /**
     * Creates an instance with preset start and end
     *
     * @param start
     * @param end
     */
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

    /**
     * Add a record for an individual tweet
     *
     * @param timeStamp the time stamp of the tweet
     * @param language  the language
     */
    public void add(Date timeStamp, String language) {
        total++;
        if (!countMap.containsKey(language)) {
            countMap.put(language, 1l);
        } else {
            countMap.put(language, countMap.get(language) + 1);
        }

        if (autoRange) {
            // check whether we need to reset start or end

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

    /**
     * Merge the data from another period object into this one. Start and end date will be adjusted if needed.
     *
     * @param other the period to merge
     */
    public void merge(PeriodSummary other) {
        if (other.getStart().before(start)) {
            start = other.getStart();
        }

        if (other.getEnd().after(end)) {
            end = other.getEnd();
        }

        for (Map.Entry<String, Long> entry : other.countMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                // the date stamp used here is irrelevant as long as it's between start and end
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
