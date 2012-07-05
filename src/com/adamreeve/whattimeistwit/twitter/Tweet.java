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

    private static final String FORMAT = "{0,date,short} {0,time,short}|{1,number,integer}|{2}";
    private static final MessageFormat formatter = new MessageFormat(FORMAT);
    private Date created;
    private String text;
    private long id;

    public Tweet(String fileStr) {
        try {
            Object[] args = formatter.parse(fileStr);
        } catch (ParseException e) {
            logger.error("Error parsing record : " + fileStr, e);
        }
    }

    public Tweet(Date created, String text, long id) {
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
        return new ArrayList<>(Arrays.asList(text.split("[ \\.\\-\\?!\\(\\)\\|\\]\\[\\{\\}]")));
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
        return s.startsWith("@") || s.equals("RT") || s.startsWith("http");
    }

    public String toFileStr() {
        Object[] args = new Object[]{created, created, id, text.replace('\n', ' ')};
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
}
