package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.analysis.classifiers.CharSetLanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.LanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.WordListLanguageClassifier;
import com.adamreeve.whattimeistwit.twitter.tweet.SimpleFileTweetSource;
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
import java.util.Date;
import java.util.List;

/**
 * Date: 7/4/12 Time: 11:28 PM
 */
public class ClassificationProcessor {

    private static Logger logger = LoggerFactory.getLogger(ClassificationProcessor.class);
    private static final int BATCH_EVERY = 10000;
    public static final int DEFAULT_PERIOD_SIZE = 60 * 5;

    private static FileFilter fileFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.isFile() && file.canRead();
        }
    };

    public static void main(String[] args) {
        ClassificationProcessor processor = new ClassificationProcessor();

        logger.info("Starting...");

        CliOptions opts = new CliOptions();

        try {
            CommandLine commandLine = opts.parseCommandLine(args);

            List<String> filenames = new ArrayList<>();

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
                            logger.error(String.format("Error processing file %s", file.getName()));
                        }
                    }
                } else {
                    throw new ParseException(String.format("%s is not a valid directory", dirName));
                }
            }

            int periodSecs = DEFAULT_PERIOD_SIZE;
            if (commandLine.hasOption(CliOptions.OPT_PERIOD_SIZE)) {
                periodSecs = ((Number) commandLine.getParsedOptionValue(CliOptions.OPT_PERIOD_SIZE)).intValue();
            }

            processor.naiveRun(new SimpleFileTweetSource("D:\\Data\\Adam\\Temp\\out-2.txt"),
                               processor.getClassifiers());

        } catch (ParseException e) {
            logger.error("Error in command line", e);
            opts.printHelpText();
        } catch (IOException e) {
            logger.error("Error running analysis", e);
        }

    }

    private void Test() {
        Tweet tweet = new Tweet(new Date(), "Lol Cornelius is funny a f haha seriously !", 1l);

        ClassificationSet scores = new ClassificationSet();

        for (LanguageClassifier lc : getClassifiers()) {
            Float score = lc.classify(tweet);
            logger.info(String.format("Tweet had a score of %f for language %s", score, lc.getLanguage()));
            scores.setCertainty(lc.getLanguage(), score);
        }

        logger.info(scores.toString());
        logger.info("Best match is: " + scores.getBestMatch());
    }

    private void naiveRun(TweetSource source, List<LanguageClassifier> classifiers) {
        List<PeriodSummary> periods = new ArrayList<>();

        PeriodSummary ps = new PeriodSummary();
        periods.add(ps);

        int count = 0;
        for (Tweet tweet : source) {
            count++;

            ClassificationSet tc = new ClassificationSet();
            for (LanguageClassifier classifier : classifiers) {
                tc.setCertainty(classifier.getLanguage(), classifier.classify(tweet));
            }

            logger.debug(String.format("%s : %s", tc.getBestMatch().toSimpleString(), tweet.getText()));

            ps.add(tweet.getCreated(), tc.getBestMatch().getLanguage());

            if (count % BATCH_EVERY == 0) {
                logger.info("Starting new period");
                ps = new PeriodSummary();
                periods.add(ps);
            }
        }

        for (PeriodSummary period : periods) {
            logger.info(period.toString());
        }
    }

    private List<LanguageClassifier> getClassifiers() {
        List<LanguageClassifier> result = new ArrayList<>();

        result.add(new WordListLanguageClassifier("EN", "2of12inf.txt"));
        result.add(new WordListLanguageClassifier("FR", "liste_mots.txt"));
        result.add(new WordListLanguageClassifier("ES", "es.dic"));
        result.add(new WordListLanguageClassifier("DE", "de_neu.dic"));
//        result.add(new WordListLanguageClassifier("ES", "es_30K.txt"));
        result.add(new WordListLanguageClassifier("AF", "words.afrikaans.txt"));
        result.add(new WordListLanguageClassifier("CS", "words.czech.txt"));
        result.add(new WordListLanguageClassifier("DA", "words.danish.txt"));
//        result.add(new WordListLanguageClassifier("FI", "words.finnish.txt"));
        result.add(new WordListLanguageClassifier("HR", "words.croatian.txt"));
        result.add(new WordListLanguageClassifier("IT", "words.italian.txt"));
        result.add(new WordListLanguageClassifier("NL", "words.dutch.txt"));
        result.add(new WordListLanguageClassifier("NO", "words.norwegian.txt"));
//        result.add(new WordListLanguageClassifier("PL", "words.polish.txt"));
        result.add(new WordListLanguageClassifier("SV", "words.swedish.txt"));
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
        result.add(new WordListLanguageClassifier("PT", "portugueseU.dic"));
        result.add(new WordListLanguageClassifier("ID", "00-indonesian-wordlist.lst"));
//        result.add(new WordListLanguageClassifier("BR", "br.dic"));
        return result;
    }

}
