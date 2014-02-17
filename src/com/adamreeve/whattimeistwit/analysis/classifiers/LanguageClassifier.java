package com.adamreeve.whattimeistwit.analysis.classifiers;

import com.adamreeve.whattimeistwit.tweet.Tweet;

/**
 * Responsible for calculating the likelihood of a given tweet being in a specific language.
 * <p/>
 *
 */
public interface LanguageClassifier {

    /**
     * Calculate the likelihood of the tweet being in the language represented by this classifier.
     *
     * @param tweet the tweet to test
     * @return a float 0<=n<=1 representing a percentage probability
     */
    public Float classify(Tweet tweet);

    /**
     * The language this classifier tests for
     *
     * @return an ISO language code
     */
    public String getLanguage();
}
