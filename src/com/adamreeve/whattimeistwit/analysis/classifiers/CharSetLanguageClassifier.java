package com.adamreeve.whattimeistwit.analysis.classifiers;

import com.adamreeve.whattimeistwit.tweet.Tweet;

/**
 * Tests for a language by looking for characters in a language specific character set (e.g. Japanese).
 * <p/>
 * Date: 7/7/12 Time: 10:57 PM
 */
public class CharSetLanguageClassifier implements LanguageClassifier {

    private String language;
    private Range[] ranges;
    private Range[] negRanges;

    /**
     * Specify the language and a set of ranges which make up the character set for this language.
     *
     * @param language an ISO language code
     * @param ranges   a set of ranges within the the unicode space
     */
    public CharSetLanguageClassifier(String language, Range[] ranges) {
        this(language, ranges, new Range[0]);
    }

    /**
     * Specify the language and a set of ranges which make up the character set for this language. Also specify a set of
     * character ranges which prove that the tweet is NOT in this language.
     *
     * @param language  an ISO language code
     * @param ranges    a set of ranges within the the unicode space
     * @param negRanges a set of ranges within the the unicode space - any matches in this space will cause a 0% match
     */
    public CharSetLanguageClassifier(String language, Range[] ranges, Range[] negRanges) {
        this.language = language;
        this.ranges = ranges;
        this.negRanges = negRanges;
    }

    @Override
    public Float classify(Tweet tweet) {
        int matches = 0;
        int total = 0;
        for (String word : tweet.getRealWordsInLowerCase()) {
            for (int i = 0; i < word.length(); i++) {
                total++;
                char c = word.charAt(i);

                for (int j = 0; j < ranges.length; j++) {
                    if (c >= ranges[j].min && c <= ranges[j].max) {
                        matches++;
                    }
                }
                for (int j = 0; j < negRanges.length; j++) {
                    if (c >= negRanges[j].min && c <= negRanges[j].max) {
                        return 0f;
                    }
                }
            }
        }

        return (float) matches / total;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    public static class Range {
        private int min;
        private int max;

        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

}
