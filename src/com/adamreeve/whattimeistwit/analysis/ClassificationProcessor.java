package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.twitter.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Date: 7/4/12
 * Time: 11:28 PM
 */
public class ClassificationProcessor {

    private static Logger logger = LoggerFactory.getLogger(ClassificationProcessor.class);

    public static void main(String[] args) {
        ClassificationProcessor processor = new ClassificationProcessor();
        processor.Test();
    }

    private void Test() {
        Tweet tweet = new Tweet(new Date(), "This is a test fsdf @fsdfs @crash RT!yes", 1);

        LanguageClassifier lc1 = new GeneralLanguageClassifier("ENG", "D:\\Data\\Adam\\Temp\\dicts\\eng\\2of12.txt");
        LanguageClassifier lc2 = new GeneralLanguageClassifier("FR", "D:\\Data\\Adam\\Temp\\dicts\\fr\\basewrd2_f.txt");

        logger.info(String.format("Tweet had a score of %f for language %s", lc1.classify(tweet), lc1.getLanguage()));
        logger.info(String.format("Tweet had a score of %f for language %s", lc2.classify(tweet), lc2.getLanguage()));
    }


}
