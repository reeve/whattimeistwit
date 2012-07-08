package com.adamreeve.whattimeistwit.analysis.classifiers;

import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Date: 7/4/12
 * Time: 11:06 PM
 */
public class WordListLanguageClassifier implements LanguageClassifier {

    private static Logger logger = LoggerFactory.getLogger(WordListLanguageClassifier.class);

    private String language;
    private Set<String> dictionary;
    private static final String basePath = "D:\\Data\\Adam\\Temp\\dicts";

    public WordListLanguageClassifier(String language, Set<String> dictionary) {
        this.language = language;
        this.dictionary = dictionary;
    }

    public WordListLanguageClassifier(String language, String dictPath) {
        this.language = language;
        this.dictionary = loadDictionary(dictPath);
    }

    private Set<String> loadDictionary(String fileName) {
        String dictPath = String.format("%s\\%s\\%s", basePath, language.toLowerCase(), fileName);

        logger.info("Loading dictionary from " + dictPath);

        Set<String> result = new HashSet<>(1000);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dictPath));
            String line = reader.readLine();
            while (line != null) {
                String word = line.trim().split("\\s")[0];
                if (word.length() > 1) {
                    result.add(word);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            logger.error("Error reading dict file : " + dictPath, e);
        }

        logger.info(String.format("Loaded %d entries", result.size()));

        return result;
    }

    @Override
    public Float classify(Tweet tweet) {
        List<String> words = tweet.getRealWords();

        Set<String> dictionary = getDictionary();

        int matches = 0;
        for (String word : words) {
            if (dictionary.contains(word.toLowerCase())) {
                matches++;
                if (logger.isDebugEnabled()) {
                    logger.debug(word + ":match");
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(word + ":fail");
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Classifier for %s matched %d out of %d words", getLanguage(), matches, words.size()));
        }

        return (float) matches / words.size();
    }

    @Override
    public String getLanguage() {
        return language;
    }

    protected Set<String> getDictionary() {
        return dictionary;
    }

}
