package com.adamreeve.whattimeistwit.analysis.classifiers;

import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 7/4/12
 * Time: 11:06 PM
 */
public class WordListLanguageClassifier implements LanguageClassifier {

    private static Logger LOGGER = LoggerFactory.getLogger(WordListLanguageClassifier.class);

    private String language;
    private Set<String> dictionary;

    public WordListLanguageClassifier(String language, String dictName, String basePath) {
        this.language = language;
        this.dictionary = loadDictionary(dictName, basePath);
    }

    private Set<String> loadDictionary(String fileName, String basePath) {
        String dictPath = String.format("%s\\%s\\%s", basePath, language.toLowerCase(), fileName);

        LOGGER.info("Loading dictionary from " + dictPath);

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
            LOGGER.error("Error reading dict file : " + dictPath, e);
        }

        LOGGER.info(String.format("Loaded %d entries", result.size()));

        return result;
    }

    @Override
    public Float classify(Tweet tweet) {
        Set<String> words = tweet.getRealWordsInLowerCase();
        Set<String> dictionary = getDictionary();

        int matches = 0;
        for (String word : words) {
            if (dictionary.contains(word)) {
                matches++;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(word + ":match");
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(word + ":fail");
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Classifier for %s matched %d out of %d words",
                                       getLanguage(),
                                       matches,
                                       words.size()));
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
