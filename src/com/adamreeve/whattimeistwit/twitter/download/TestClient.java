package com.adamreeve.whattimeistwit.twitter.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

/**
 * Date: 7/2/12
 * Time: 9:30 PM
 */
public class TestClient {

    static Logger logger = LoggerFactory.getLogger(TestClient.class);

    public static void main(String[] args) {
        logger.info("Starting...");

        TestClient client = new TestClient();
        client.dumpToLog();
    }

    private void dumpToLog() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                logger.info("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                logger.info("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                logger.warn("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
                logger.info("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            public void onException(Exception ex) {
                logger.error("Exception returned", ex);
            }
        };

        twitterStream.addListener(listener);
        twitterStream.sample();
    }

}
