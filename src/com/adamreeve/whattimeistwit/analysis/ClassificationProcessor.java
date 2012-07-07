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


        Tweet tweet = new Tweet(new Date(), "silence, the most celebrated.", 1l);

        for (LanguageClassifier lc : getClassifiers()) {
            logger.info(String.format("Tweet had a score of %f for language %s", lc.classify(tweet), lc.getLanguage()));
        }


    }

    private void naiveRun(TweetSource source, List<LanguageClassifier> classifiers) {
        for (Tweet tweet : source) {
            Classification tc = new Classification();
            for (LanguageClassifier classifier : classifiers) {
                tc.setCertainty(classifier.getLanguage(), classifier.classify(tweet));
            }
            logger.info(tc.toString() + tweet.getText());
        }

    }

    private List<LanguageClassifier> getClassifiers() {
        List<LanguageClassifier> result = new ArrayList<>();

        result.add(new GeneralLanguageClassifier("ENG", "D:\\Data\\Adam\\Temp\\dicts\\eng\\2of12inf.txt"));
        result.add(new GeneralLanguageClassifier("FR", "D:\\Data\\Adam\\Temp\\dicts\\fr\\liste_mots.txt"));
        result.add(new GeneralLanguageClassifier("ES", "D:\\Data\\Adam\\Temp\\dicts\\es\\words.spanish.txt"));

        return result;
    }

}
