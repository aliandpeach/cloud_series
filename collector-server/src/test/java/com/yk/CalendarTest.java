package com.yk;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * CanlandarTest
 */

public class CalendarTest
{
    @Test
    public void test() throws ParseException
    {
        LocalDate localDate = LocalDate.now();
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Date date = Date.from(zonedDateTime.toInstant());
        Calendar calendar = Calendar.getInstance();
        long currentTime1 = calendar.getTimeInMillis();
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime1)));
        calendar.setTime(date);
        
        long currentTime = calendar.getTimeInMillis();
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentTime)));
        
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 2);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
    
    
        SimpleDateFormat startFormat2 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat endFormat2 = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = "04/07/2021";
        Date dateStart = startFormat2.parse(dateString);
        System.out.println(dateStart);
    }
}
