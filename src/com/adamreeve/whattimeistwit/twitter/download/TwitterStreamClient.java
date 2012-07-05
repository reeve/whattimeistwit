package com.adamreeve.whattimeistwit.twitter.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.io.IOException;

/**
 * Date: 7/2/12
 * Time: 9:30 PM
 */
public class TwitterStreamClient {

    private static Logger logger = LoggerFactory.getLogger(TwitterStreamClient.class);

    public static void main(String[] args) throws IOException {
        logger.info("Starting...");

        TwitterStreamClient client = new TwitterStreamClient();
        client.runExtract();
    }

    private void runExtract() throws IOException {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new FileWriterStatusListener("D:\\Data\\Adam\\Temp\\out.txt");

        twitterStream.addListener(listener);
        twitterStream.sample();
        System.out.println("Whats up");

        try {
            Thread.sleep(30000l);
        } catch (InterruptedException e) {
            logger.error("Exception sleeping", e);
        }

        twitterStream.shutdown();

    }

}
