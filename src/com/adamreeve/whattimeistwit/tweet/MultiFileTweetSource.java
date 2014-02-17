package com.adamreeve.whattimeistwit.tweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Loads tweets from a series of files in no specific order, making use of the SimpleFileTweetSource.
 * <p/>
 *
 */
public class MultiFileTweetSource implements TweetSource {

    private static Logger LOGGER = LoggerFactory.getLogger(MultiFileTweetSource.class);

    private Iterator<String> pathIter;
    private SimpleFileTweetSource current;
    private MultiFileIterator iterator;

    /**
     * Build an instance with a specified list of file paths
     *
     * @param paths the paths to load
     */
    public MultiFileTweetSource(List<String> paths) {
        pathIter = paths.iterator();
        findNext();
        iterator = new MultiFileIterator();
    }

    private void findNext() {
        // find the next path which is readable and set it as current

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

    /**
     * Iterator implementation which will switch to the next available file when each one is exhausted
     */
    private class MultiFileIterator implements Iterator<Tweet> {
        private Iterator<Tweet> currIter;

        private MultiFileIterator() {
            updateIter();
        }

        @Override
        public boolean hasNext() {
            // check we have a current file
            if (current == null) {
                return false;
            }

            // if there's an element available return true
            if (currIter.hasNext()) {
                return true;
            }

            // there isn't - so look for a new file
            findNext();
            // and get it's iterator
            updateIter();
            // recurse
            return hasNext();
        }

        @Override
        public Tweet next() {
            // check we have a current file
            if (current == null) {
                throw new NoSuchElementException();
            }

            // if there's an element available return it
            if (currIter.hasNext()) {
                return currIter.next();
            }

            // there isn't - so look for a new file
            findNext();
            // and get it's iterator
            updateIter();
            // recurse
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
