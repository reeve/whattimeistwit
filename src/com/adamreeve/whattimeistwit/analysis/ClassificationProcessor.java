package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.analysis.classifiers.CharSetLanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.LanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.WordListLanguageClassifier;
import com.adamreeve.whattimeistwit.twitter.tweet.MultiFileTweetSource;
import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;
import com.adamreeve.whattimeistwit.twitter.tweet.TweetSource;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Date: 7/4/12 Time: 11:28 PM
 */
public class ClassificationProcessor {

    private static final int BATCH_EVERY = 2000;
    private static Logger LOGGER = LoggerFactory.getLogger(ClassificationProcessor.class);

    public static void main(String[] args) {
        ClassificationProcessor processor = new ClassificationProcessor();

        LOGGER.info("Starting...");

        CliOptions opts = new CliOptions();

        try {
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

            processor.batchedRun(new MultiFileTweetSource(filenames),
                                 getClassifiers("D:\\Data\\Adam\\Temp\\dicts"),
                                 threadCount);

        } catch (ParseException e) {
            LOGGER.error("Error in command line", e);
            opts.printHelpText();
        }

        LOGGER.info("Done.");
    }

    private static FileFilter fileFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isFile() && file.canRead();
        }
    };

    private void batchedRun(TweetSource source, List<LanguageClassifier> classifiers, int threadCount) {

        LOGGER.info("Starting batched run with {} threads", threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        int count = 0;
        List<Tweet> batch = new ArrayList<>();
        Map<Date, PeriodSummary> periods = new HashMap<>();
        List<Future<Collection<PeriodSummary>>> futures = new ArrayList<>();

        for (Tweet tweet : source) {
            count++;
            batch.add(tweet);

            if (count % BATCH_EVERY == 0) {
                LOGGER.debug("Queueing batch");
                futures.add(executor.submit(new BatchProcessor(batch, classifiers)));
                LOGGER.debug("Starting new batch");
                batch = new ArrayList<>();
            }
        }

        if (batch.size() > 0) {
            LOGGER.debug("Queueing final batch");
            futures.add(executor.submit(new BatchProcessor(batch, classifiers)));
        }

        executor.shutdown();

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

        for (PeriodSummary period : periods.values()) {
            LOGGER.info(period.toString());
        }
    }

    private static List<LanguageClassifier> getClassifiers(String basePath) {
        List<LanguageClassifier> result = new ArrayList<>();

        result.add(new WordListLanguageClassifier("EN", "2of12inf.txt", basePath));
        result.add(new WordListLanguageClassifier("FR", "liste_mots.txt", basePath));
        result.add(new WordListLanguageClassifier("ES", "es.dic", basePath));
        result.add(new WordListLanguageClassifier("DE", "de_neu.dic", basePath));
//        result.add(new WordListLanguageClassifier("ES", "es_30K.txt"));
        result.add(new WordListLanguageClassifier("AF", "words.afrikaans.txt", basePath));
        result.add(new WordListLanguageClassifier("CS", "words.czech.txt", basePath));
        result.add(new WordListLanguageClassifier("DA", "words.danish.txt", basePath));
//        result.add(new WordListLanguageClassifier("FI", "words.finnish.txt"));
        result.add(new WordListLanguageClassifier("HR", "words.croatian.txt", basePath));
        result.add(new WordListLanguageClassifier("IT", "words.italian.txt", basePath));
        result.add(new WordListLanguageClassifier("NL", "words.dutch.txt", basePath));
        result.add(new WordListLanguageClassifier("NO", "words.norwegian.txt", basePath));
//        result.add(new WordListLanguageClassifier("PL", "words.polish.txt"));
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
//        result.add(new WordListLanguageClassifier("BR", "br.dic"));
        return result;
    }
}
