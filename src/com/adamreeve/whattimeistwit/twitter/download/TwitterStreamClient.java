package com.adamreeve.whattimeistwit.twitter.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.io.IOException;

/**
 * Date: 7/2/12 Time: 9:30 PM
 */
public class TwitterStreamClient {

    private static Logger logger = LoggerFactory.getLogger(TwitterStreamClient.class);

    private static final int RUNTIME_SECS = 300;

    public static void main(String[] args) {
        logger.info("Starting...");

        TwitterStreamClient client = new TwitterStreamClient();
        try {
            client.runExtract(new FileWriterStatusListener("data\\out-12.txt"));
        } catch (IOException e) {
            logger.error("Exception running extract", e);
        }
    }

    private void runExtract(CloseableStatusListener listener) throws IOException {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        twitterStream.addListener(listener);
        twitterStream.sample();

        try {
            Thread.sleep(1000 * RUNTIME_SECS);
        } catch (InterruptedException e) {
            logger.error("Exception sleeping", e);
        }

        listener.close();

        twitterStream.shutdown();
    }

}
