package com.adamreeve.whattimeistwit.analysis.classifiers;

import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;

/**
 * Date: 7/4/12
 * Time: 10:44 PM
 */
public interface LanguageClassifier {

    public Float classify(Tweet tweet);

    public String getLanguage();
}
