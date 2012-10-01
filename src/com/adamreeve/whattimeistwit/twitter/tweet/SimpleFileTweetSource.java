package com.adamreeve.whattimeistwit.twitter.tweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Date: 7/7/12 Time: 3:41 PM
 */
public class SimpleFileTweetSource implements TweetSource {

    private static Logger logger = LoggerFactory.getLogger(SimpleFileTweetSource.class);

    private List<Tweet> tweets = new ArrayList<>(100);

    public SimpleFileTweetSource(String path) throws IOException {
        File file = new File(path);

        if (!file.canRead()) {
            throw new IOException(String.format("Can't open input file for reading: %s", path));
        }

        logger.info("Opening file at " + file.getCanonicalPath());

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        while (line != null) {
            tweets.add(new Tweet(line));
            line = reader.readLine();
        }

        logger.info(String.format("Read %d tweets", tweets.size()));
    }

    @Override
    public Iterator<Tweet> iterator() {
        return tweets.iterator();
    }
}
