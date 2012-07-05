package com.adamreeve.whattimeistwit.twitter.download;

import twitter4j.StatusListener;

/**
 * Date: 7/4/12
 * Time: 11:56 PM
 */
public interface CloseableStatusListener extends StatusListener {
    void close();
}
