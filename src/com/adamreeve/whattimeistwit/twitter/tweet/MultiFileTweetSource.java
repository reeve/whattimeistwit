package com.adamreeve.whattimeistwit.twitter.tweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Date: 9/24/12 Time: 10:26 PM
 */
public class MultiFileTweetSource implements TweetSource {

    private static Logger LOGGER = LoggerFactory.getLogger(MultiFileTweetSource.class);

    private Iterator<String> pathIter;
    private SimpleFileTweetSource current;
    private MultiFileIterator iterator;

    public MultiFileTweetSource(List<String> paths) {
        pathIter = paths.iterator();
        findNext();
        iterator = new MultiFileIterator();
    }

    private void findNext() {
        while (pathIter.hasNext()) {
            String path = pathIter.next();
            try {
                current = new SimpleFileTweetSource(path);
            } catch (IOException e) {
                LOGGER.error("Error reading file " + path, e);
                continue;
            }

            if (current.iterator().hasNext()) {
                return;
            }
        }
        current = null;
    }

    @Override
    public Iterator<Tweet> iterator() {
        return iterator;
    }

    private class MultiFileIterator implements Iterator<Tweet> {
        private Iterator<Tweet> currIter;

        private MultiFileIterator() {
            updateIter();
        }

        @Override
        public boolean hasNext() {
            if (current == null) {
                return false;
            }

            if (currIter.hasNext()) {
                return true;
            }

            findNext();
            updateIter();
            return hasNext();
        }

        @Override
        public Tweet next() {
            if (current == null) {
                throw new NoSuchElementException();
            }

            if (currIter.hasNext()) {
                return currIter.next();
            }

            findNext();
            updateIter();
            return next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() is not supported");
        }

        private void updateIter() {
            if (current != null) {
                currIter = current.iterator();
            } else {
                currIter = null;
            }
        }

    }
}
