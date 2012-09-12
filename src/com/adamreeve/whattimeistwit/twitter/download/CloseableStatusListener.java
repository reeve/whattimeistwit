package com.adamreeve.whattimeistwit.twitter.download;

import twitter4j.StatusListener;

/**
 * Date: 7/4/12 Time: 11:56 PM
 */
public interface CloseableStatusListener extends StatusListener {
    void close();

    int getCount();

    void setMaxCount(int maxCount);

    boolean atLimit();
}
