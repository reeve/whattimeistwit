package com.adamreeve.whattimeistwit.twitter.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Date: 7/4/12
 * Time: 6:48 PM
 */
public class FileWriterStatusListener implements StatusListener {
    static Logger logger = LoggerFactory.getLogger(FileWriterStatusListener.class);

    private File outputFile;
    private FileWriter writer;
    private int writeCount;
    private static final int REPORT_EVERY = 100;
    private static final int LATENCY_LIMIT_MS = 5000;

    public FileWriterStatusListener(String filePath) throws IOException {
        outputFile = new File(filePath);
        logger.info("Output file is " + outputFile.getCanonicalPath());

        if (outputFile.createNewFile()) {
            logger.info("Created new output file");
        } else {
            logger.info("Overwriting output file");
        }

        if (!outputFile.canWrite()) {
            throw new IOException("Cannot write to output file: " + outputFile.getCanonicalPath());
        }

        writer = new FileWriter(outputFile);
    }

    @Override
    public void onStatus(Status status) {
        Date createdAt = status.getCreatedAt();
        String text = null;
        text = status.getText().replace('\n', ' ');

        String record = String.format("%tD %tT|%d|%s%n", createdAt, createdAt, status.getId(), text);

        try {
            writer.write(record);
            writer.flush();
        } catch (IOException e) {
            logger.error("Exception writing output record", e);
        }

        writeCount++;

        if (writeCount % REPORT_EVERY == 0) {
            long latency = createdAt.getTime() - System.currentTimeMillis();
            logger.info(String.format("Wrote %d records - latency is currently %d ms", writeCount, latency));
            if (latency > LATENCY_LIMIT_MS) {
                throw new RuntimeException(String.format("Latency limit exceeded: %d", latency));
            }
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onException(Exception ex) {
        logger.error("Exception while reading stream", ex);
    }
}
