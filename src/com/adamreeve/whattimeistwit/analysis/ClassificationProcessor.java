package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.twitter.Tweet;
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


        Tweet tweet = new Tweet(new Date(), "@tis_beth_x just wanted to let u know that I'm not to shy to talk to u haha :) how are u?", 1l);

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
        for (Tweet tweet : source) {
            ClassificationSet tc = new ClassificationSet();
            for (LanguageClassifier classifier : classifiers) {
                tc.setCertainty(classifier.getLanguage(), classifier.classify(tweet));
            }
            logger.info(String.format("%s : %s", tc.getBestMatch().toSimpleString(), tweet.getText()));
        }

    }

    private List<LanguageClassifier> getClassifiers() {
        List<LanguageClassifier> result = new ArrayList<>();

        result.add(new GeneralLanguageClassifier("EN", "2of12inf.txt"));
        result.add(new GeneralLanguageClassifier("FR", "liste_mots.txt"));
        result.add(new GeneralLanguageClassifier("ES", "words.spanish.txt"));
        result.add(new GeneralLanguageClassifier("AF", "words.afrikaans.txt"));
        result.add(new GeneralLanguageClassifier("CS", "words.czech.txt"));
        result.add(new GeneralLanguageClassifier("DA", "words.danish.txt"));
//        result.add(new GeneralLanguageClassifier("FI", "words.finnish.txt"));
        result.add(new GeneralLanguageClassifier("HR", "words.croatian.txt"));
        result.add(new GeneralLanguageClassifier("IT", "words.italian.txt"));
        result.add(new GeneralLanguageClassifier("NL", "words.dutch.txt"));
        result.add(new GeneralLanguageClassifier("NO", "words.norwegian.txt"));
//        result.add(new GeneralLanguageClassifier("PL", "words.polish.txt"));
        result.add(new GeneralLanguageClassifier("SV", "words.swedish.txt"));

        return result;
    }

}
