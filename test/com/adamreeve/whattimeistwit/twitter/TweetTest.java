package com.adamreeve.whattimeistwit.twitter;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Date: 7/7/12
 * Time: 4:09 PM
 */
public class TweetTest {

    @Test
    public void testSerialize() {
//        Date created = new Date();
        Calendar gc = new GregorianCalendar();
        gc.set(Calendar.MILLISECOND, 0);
        Tweet t = new Tweet(gc.getTime(), "TestStr", 1764826348234l);

        String s = t.toFileStr();
        assertNotNull(s);
        System.out.println("Intermediate: " + s);

        Tweet t2 = new Tweet(s);
        assertEquals(t, t2);
    }

}
