package com.adamreeve.whattimeistwit.twitter.download;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date: 7/2/12 Time: 9:30 PM
 */
public class TwitterStreamClient {

    private static Logger logger = LoggerFactory.getLogger(TwitterStreamClient.class);

    private static final int DEFAULT_RUNTIME_SECS = 30;

    public static void main(String[] args) {
        logger.info("Starting...");
        CliOptions opts = new CliOptions();

        try {
            CommandLine commandLine = opts.parseCommandLine(args);

            String filename = commandLine.hasOption(CliOptions.OPT_AUTONAME)
                    ? generateFileName(commandLine.getOptionValue(CliOptions.OPT_AUTONAME))
                    : commandLine.getOptionValue(CliOptions.OPT_FILENAME);

            int maxTimeSecs = DEFAULT_RUNTIME_SECS;
            if (commandLine.hasOption(CliOptions.OPT_TIME)) {
                maxTimeSecs = ((Number) commandLine.getParsedOptionValue(CliOptions.OPT_TIME)).intValue();
            }

            int maxTweets = Integer.MAX_VALUE;
            if (commandLine.hasOption(CliOptions.OPT_MAXTWEET)) {
                maxTweets = ((Number) commandLine.getParsedOptionValue(CliOptions.OPT_MAXTWEET)).intValue();
            }

            TwitterStreamClient client = new TwitterStreamClient();
            try {
                client.runExtract(new FileWriterStatusListener(filename, maxTweets), maxTimeSecs);
            } catch (IOException e) {
                logger.error("Exception running extract", e);
            }

        } catch (ParseException e) {
            logger.error("Error in command line", e);
            opts.printHelpText();
        }
    }

    private static String generateFileName(String baseDir) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        return String.format("%s\\twitter-%s-EST.txt", baseDir, df.format(new Date()));
    }

    private void runExtract(CloseableStatusListener listener, int maxTimeSecs) throws IOException {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        logger.info(String.format("Running extract for at most %d seconds", maxTimeSecs));

        twitterStream.addListener(listener);
        twitterStream.sample();

        try {
            for (int i = 0; i < maxTimeSecs; i++) {
                Thread.sleep(1000);
                if (listener.atLimit()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            logger.error("Exception sleeping", e);
        }

        twitterStream.shutdown();
        listener.close();

    }

}
