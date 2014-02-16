package com.adamreeve.whattimeistwit.analysis;

import com.adamreeve.whattimeistwit.analysis.classifiers.LanguageClassifier;
import com.adamreeve.whattimeistwit.twitter.tweet.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Author: Adam Reeve Date: 2/16/14 Time: 3:44 PM
 */
class BatchProcessor implements Callable<Collection<PeriodSummary>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessor.class);

    private final List<Tweet> batch;
    private final List<LanguageClassifier> classifiers;
    private final Calendar cal;

    public BatchProcessor(List<Tweet> batch, List<LanguageClassifier> classifiers) {
        this.batch = batch;
        this.classifiers = classifiers;
        cal = GregorianCalendar.getInstance();
    }

    @Override
    public Collection<PeriodSummary> call() throws Exception {
        return processBatch(batch, classifiers);
    }

    private Collection<PeriodSummary> processBatch(List<Tweet> batch, Collection<LanguageClassifier> classifiers) {
        LOGGER.info("Processing batch of {} tweets", batch.size());

        Map<Date, PeriodSummary> periods = new HashMap<>();

        for (Tweet tweet : batch) {
            ClassificationSet tc = new ClassificationSet();
            for (LanguageClassifier classifier : classifiers) {
                tc.setCertainty(classifier.getLanguage(), classifier.classify(tweet));
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} : {}", tc.getBestMatch().toSimpleString(), tweet.getText());
            }

            getPeriod(tweet.getCreated(), periods).add(tweet.getCreated(), tc.getBestMatch().getLanguage());
        }

        LOGGER.info("Done, returning {} period summaries", periods.size());
        return periods.values();
    }

    private PeriodSummary getPeriod(Date datestamp, Map<Date, PeriodSummary> periods) {
        cal.setTime(datestamp);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date pStart = cal.getTime();

        PeriodSummary periodSummary = periods.get(pStart);

        if (periodSummary == null) {
            cal.add(Calendar.MINUTE, 1);
            cal.add(Calendar.MILLISECOND, -1);
            Date pEnd = cal.getTime();
            periodSummary = new PeriodSummary(pStart, pEnd);
            periods.put(pStart, periodSummary);
        }

        return periodSummary;
    }

}
