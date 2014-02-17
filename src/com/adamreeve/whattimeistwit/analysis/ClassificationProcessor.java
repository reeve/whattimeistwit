package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.analysis.classifiers.CharSetLanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.LanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.WordListLanguageClassifier;
import com.adamreeve.whattimeistwit.tweet.MultiFileTweetSource;
import com.adamreeve.whattimeistwit.tweet.Tweet;
import com.adamreeve.whattimeistwit.tweet.TweetSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Main class for processing a set of tweets and generating language splits for each 1 minute period.
 * <p/>
 * Date: 7/4/12 Time: 11:28 PM
 */
public class ClassificationProcessor {

    private static final int BATCH_SIZE = 2000;
    private static Logger LOGGER = LoggerFactory.getLogger(ClassificationProcessor.class);

    public static void main(String[] args) {
        ClassificationProcessor processor = new ClassificationProcessor();

        LOGGER.info("Starting...");

        CliOptions opts = new CliOptions();

        try {
            // figure out the options
            CommandLine commandLine = opts.parseCommandLine(args);

            List<String> filenames = new ArrayList<>();
            int threadCount = 1;

            if (commandLine.hasOption(CliOptions.OPT_FILENAME)) {
                filenames.add(commandLine.getOptionValue(CliOptions.OPT_FILENAME));
            } else {
                String dirName = commandLine.getOptionValue(CliOptions.OPT_DIRNAME);
                File dir = new File(dirName);
                if (dir.isDirectory()) {
                    for (File file : dir.listFiles(fileFilter)) {
                        try {
                            filenames.add(file.getCanonicalPath());
                        } catch (IOException e) {
                            LOGGER.error(String.format("Error processing file %s", file.getName()));
                        }
                    }
                } else {
                    throw new ParseException(String.format("%s is not a valid directory", dirName));
                }
            }

            if (commandLine.hasOption(CliOptions.OPT_THREADS)) {
                threadCount = Integer.parseInt(commandLine.getOptionValue(CliOptions.OPT_THREADS));
            }

            String dictBasePath = commandLine.getOptionValue(CliOptions.OPT_DICTBASE);

            // start processing
            processor.batchedRun(new MultiFileTweetSource(filenames),
                                 getClassifiers(dictBasePath),
                                 threadCount);

        } catch (ParseException e) {
            LOGGER.error("Error in command line", e);
            opts.printHelpText();
        }

        LOGGER.info("Done.");
    }

    // filters a set of files into those we can actually read
    private static FileFilter fileFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isFile() && file.canRead();
        }
    };

    /**
     * Do the work - split the incoming data into batches and run through the classifiers
     *
     * @param source      data source for the tweets
     * @param classifiers the classifiers to use
     * @param threadCount max thread count for scheduling
     */
    private void batchedRun(TweetSource source, List<LanguageClassifier> classifiers, int threadCount) {

        LOGGER.info("Starting batched run with {} threads", threadCount);

        // executor
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // count of incoming tweets
        int count = 0;

        // map of start date -> period summaries
        Map<Date, PeriodSummary> periods = new HashMap<>();

        // list of futures from the scheduled tasks
        List<Future<Collection<PeriodSummary>>> futures = new ArrayList<>();

        // holder for the current batch
        List<Tweet> batch = new ArrayList<>();

        // iterate over all the tweets
        for (Tweet tweet : source) {
            // count and add to the current batch
            count++;
            batch.add(tweet);

            if (count % BATCH_SIZE == 0) {
                // schedule for execution and start a new one

                LOGGER.debug("Queueing batch");
                futures.add(executor.submit(new BatchProcessor(batch, classifiers)));
                LOGGER.debug("Starting new batch");
                batch = new ArrayList<>();
            }
        }

        if (batch.size() > 0) {
            // clean up the last few tweets
            LOGGER.debug("Queueing final batch");
            futures.add(executor.submit(new BatchProcessor(batch, classifiers)));
        }

        // wait for execution to end
        executor.shutdown();

        // collect all the results and merge equivalent periods together
        for (Future<Collection<PeriodSummary>> future : futures) {
            try {
                Collection<PeriodSummary> summaries = future.get();

                for (PeriodSummary summary : summaries) {
                    PeriodSummary existing = periods.get(summary.getStart());

                    if (existing != null) {
                        existing.merge(summary);
                    } else {
                        periods.put(summary.getStart(), summary);
                    }
                }

            } catch (InterruptedException e) {
                LOGGER.error("Interrupted executing batch", e);
            } catch (ExecutionException e) {
                LOGGER.error("Error executing batch", e);
            }
        }

        // sort the output
        List<Date> startDates = new ArrayList<>(periods.keySet());
        Collections.sort(startDates);

        // and print it out
        for (Date startDate : startDates) {
            LOGGER.info(periods.get(startDate).toString());
        }

    }

    private static List<LanguageClassifier> getClassifiers(String basePath) {
        List<LanguageClassifier> result = new ArrayList<>();

        result.add(new WordListLanguageClassifier("EN", "2of12inf.txt", basePath));
        result.add(new WordListLanguageClassifier("FR", "liste_mots.txt", basePath));
        result.add(new WordListLanguageClassifier("ES", "es.dic", basePath));
        result.add(new WordListLanguageClassifier("DE", "de_neu.dic", basePath));
        result.add(new WordListLanguageClassifier("AF", "words.afrikaans.txt", basePath));
        result.add(new WordListLanguageClassifier("CS", "words.czech.txt", basePath));
        result.add(new WordListLanguageClassifier("DA", "words.danish.txt", basePath));
        result.add(new WordListLanguageClassifier("HR", "words.croatian.txt", basePath));
        result.add(new WordListLanguageClassifier("IT", "words.italian.txt", basePath));
        result.add(new WordListLanguageClassifier("NL", "words.dutch.txt", basePath));
        result.add(new WordListLanguageClassifier("NO", "words.norwegian.txt", basePath));
        result.add(new WordListLanguageClassifier("SV", "words.swedish.txt", basePath));
        result.add(new CharSetLanguageClassifier("JP",
                                                 new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(
                                                         0x3040,
                                                         0x309F)}));
        result.add(new CharSetLanguageClassifier("CN",
                                                 new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(
                                                         0x4E00,
                                                         0x9FFF)},
                                                 new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(
                                                         0x3040,
                                                         0x309F)}));
        result.add(new CharSetLanguageClassifier("TH",
                                                 new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(
                                                         0x0E00,
                                                         0x0E7F)}));
        result.add(new CharSetLanguageClassifier("AR",
                                                 new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(
                                                         0x0600,
                                                         0x06FF)}));
        result.add(new CharSetLanguageClassifier("KO",
                                                 new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(
                                                         0xAC00,
                                                         0xD7AF)}));
        result.add(new WordListLanguageClassifier("PT", "portugueseU.dic", basePath));
        result.add(new WordListLanguageClassifier("ID", "00-indonesian-wordlist.lst", basePath));
        return result;
    }
}
