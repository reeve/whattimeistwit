package com.adamreeve.whattimeistwit.twitter.download;

import twitter4j.StatusListener;

/**
 *
 */
public interface CloseableStatusListener extends StatusListener {
    void close();

    int getCount();

    void setMaxCount(int maxCount);

    boolean atLimit();
}
