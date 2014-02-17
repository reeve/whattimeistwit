package com.adamreeve.whattimeistwit.twitter.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

/**
 *
 */
class DebugStatusListener implements CloseableStatusListener {
    static Logger logger = LoggerFactory.getLogger(DebugStatusListener.class);

    public void onStatus(Status status) {
        System.out.println(Thread.currentThread().isDaemon());
        System.out.println(Thread.currentThread().getName());
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

    @Override
    public void close() {
        // no op
    }

    @Override
    public int getCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMaxCount(int maxCount) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean atLimit() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
