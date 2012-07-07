package com.adamreeve.whattimeistwit.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Date: 7/4/12
 * Time: 9:01 PM
 */
public class Tweet {
    static Logger logger = LoggerFactory.getLogger(Tweet.class);

    private static final String FORMAT = "{0,date,yyyyMMdd HH:mm:ss}|{1,number,###}|{2}";
    private static final MessageFormat formatter = new MessageFormat(FORMAT);
    private Date created;
    private String text;
    private Long id;

    public Tweet(String fileStr) {
        try {
            Object[] args = formatter.parse(fileStr);
            this.created = (Date) args[0];
            this.text = (String) args[2];
            this.id = (Long) args[1];
        } catch (ParseException e) {
            logger.error("Error parsing record : " + fileStr, e);
        }
    }

    public Tweet(Date created, String text, Long id) {
        this.created = created;
        this.text = text;
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public List<String> getWords() {
        return new ArrayList<>(Arrays.asList(text.split("[ \\.\\-\\?!\\(\\)\\|\\]\\[\\{\\}\\,]")));
    }

    public List<String> getRealWords() {
        List<String> remove = new ArrayList<String>();
        List<String> words = getWords();
        for (String s : words) {
            if (NotReal(s)) {
                remove.add(s);
            }
        }

        words.removeAll(remove);
        return words;
    }

    private boolean NotReal(String s) {
        return s.startsWith("@") || s.equals("RT") || s.startsWith("http") || s.contains("8") || s.length() < 2;
    }

    public String toFileStr() {
        Object[] args = new Object[]{created, id, text.replace('\n', ' ')};
        return formatter.format(args);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "created=" + created +
                ", text='" + text + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (created != null ? !created.equals(tweet.created) : tweet.created != null) return false;
        if (id != null ? !id.equals(tweet.id) : tweet.id != null) return false;
        if (text != null ? !text.equals(tweet.text) : tweet.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = created != null ? created.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
