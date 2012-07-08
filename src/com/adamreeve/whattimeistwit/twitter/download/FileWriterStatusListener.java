package com.adamreeve.whattimeistwit.twitter.download;

import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Date: 7/4/12
 * Time: 6:48 PM
 */
public class FileWriterStatusListener implements CloseableStatusListener {
    static Logger logger = LoggerFactory.getLogger(FileWriterStatusListener.class);

    private BufferedWriter writer;
    private int writeCount;
    private static final int REPORT_EVERY = 100;
    private static final int LATENCY_LIMIT_MS = 5000;

    public FileWriterStatusListener(String filePath) throws IOException {
        File outputFile = new File(filePath);
        logger.info("Output file is " + outputFile.getCanonicalPath());

        if (outputFile.createNewFile()) {
            logger.info("Created new output file");
        } else {
            logger.info("Overwriting output file");
        }

        if (!outputFile.canWrite()) {
            throw new IOException("Cannot write to output file: " + outputFile.getCanonicalPath());
        }

        writer = new BufferedWriter(new FileWriter(outputFile));
    }

    @Override
    public void onStatus(Status status) {

        Tweet tweet = new Tweet(status.getCreatedAt(), status.getText(), status.getId());

        try {
            writer.write(tweet.toFileStr());
            writer.newLine();
        } catch (IOException e) {
            logger.error("Exception writing output record", e);
        }

        writeCount++;

        if (writeCount % REPORT_EVERY == 0) {
            long latency = Math.abs(status.getCreatedAt().getTime() - System.currentTimeMillis());
            logger.info(String.format("Wrote %d records - latency is currently %d ms", writeCount, latency));
            if (latency > LATENCY_LIMIT_MS) {
                throw new RuntimeException(String.format("Latency limit exceeded: %d", latency));
            }
            try {
                writer.flush();
            } catch (IOException e) {
                logger.error("Exception flushing output stream", e);
            }
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        // don't care
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        logger.warn("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        // don't care
    }

    @Override
    public void onException(Exception ex) {
        logger.error("Exception while reading stream", ex);
    }

    @Override
    public void close() {
        try {
            logger.info(String.format("Done - wrote %d records", writeCount));
            writer.close();
        } catch (IOException e) {
            logger.error("Exception closing output file", e);
        }
    }
}
