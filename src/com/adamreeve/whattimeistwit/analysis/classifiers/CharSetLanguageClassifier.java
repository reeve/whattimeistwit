package com.adamreeve.whattimeistwit.analysis.classifiers;

import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;

/**
 * Date: 7/7/12
 * Time: 10:57 PM
 */
public class CharSetLanguageClassifier implements LanguageClassifier {

    private String language;
    private Range[] ranges;
    private Range[] negRanges;

    public CharSetLanguageClassifier(String language, Range[] ranges) {
        this(language, ranges, new Range[0]);
    }

    public CharSetLanguageClassifier(String language, Range[] ranges, Range[] negRanges) {
        this.language = language;
        this.ranges = ranges;
        this.negRanges = negRanges;
    }

    @Override
    public Float classify(Tweet tweet) {
        int matches = 0;
        int total = 0;
        for (String word : tweet.getRealWords()) {
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
