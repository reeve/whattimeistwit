package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.analysis.classifiers.CharSetLanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.LanguageClassifier;
import com.adamreeve.whattimeistwit.analysis.classifiers.WordListLanguageClassifier;
import com.adamreeve.whattimeistwit.twitter.tweet.SimpleFileTweetSource;
import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;
import com.adamreeve.whattimeistwit.twitter.tweet.TweetSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Date: 7/4/12
 * Time: 11:28 PM
 */
public class ClassificationProcessor {

    private static Logger logger = LoggerFactory.getLogger(ClassificationProcessor.class);

    public static void main(String[] args) {
        ClassificationProcessor processor = new ClassificationProcessor();

        if (args.length > 0 && args[0].equals("Single")) {
            processor.Test();
        } else {
            try {
                processor.naiveRun(new SimpleFileTweetSource("D:\\Data\\Adam\\Temp\\out.txt"), processor.getClassifiers());
            } catch (IOException e) {
                logger.error("Error starting up", e);
            }
        }
    }

    private void Test() {


        Tweet tweet = new Tweet(new Date(), " RT @jennagingerelli: I miss hannah montana so much. #bringitback", 1l);

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
        PeriodSummary ps = new PeriodSummary();
        for (Tweet tweet : source) {
            ClassificationSet tc = new ClassificationSet();
            for (LanguageClassifier classifier : classifiers) {
                tc.setCertainty(classifier.getLanguage(), classifier.classify(tweet));
            }
            logger.info(String.format("%s : %s", tc.getBestMatch().toSimpleString(), tweet.getText()));
            ps.add(tweet.getCreated(), tc.getBestMatch().getLanguage());
        }
        logger.info(ps.toString());
    }

    private List<LanguageClassifier> getClassifiers() {
        List<LanguageClassifier> result = new ArrayList<>();

        result.add(new WordListLanguageClassifier("EN", "2of12inf.txt"));
        result.add(new WordListLanguageClassifier("FR", "liste_mots.txt"));
        result.add(new WordListLanguageClassifier("ES", "words.spanish.txt"));
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
        result.add(new CharSetLanguageClassifier("JP", new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(0x3040, 0x309F)}));
        result.add(new CharSetLanguageClassifier("CN", new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(0x4E00, 0x9FFF)}, new CharSetLanguageClassifier.Range[]{new CharSetLanguageClassifier.Range(0x3040, 0x309F)}));

        return result;
    }

}
