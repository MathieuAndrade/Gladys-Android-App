package com.gladysinc.gladys.Utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    public static String parseDateTime(String dateString){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyyHH:mm", Locale.FRANCE);
        Date date;
        try {
            date = formatter.parse(dateString);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("FRENCH"));

            return dateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRelativeTimeSpan(String dateString){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRENCH);
        formatter.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));
        Date date;
        try {
            long now = System.currentTimeMillis();
            date = formatter.parse(dateString);

            return DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentTimeZone(){
        TimeZone tz = Calendar.getInstance().getTimeZone();
        return tz.getDisplayName();
    }

    public static String getDay (String dayofweek){

        String date;
        String[] days = {"Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
        date = Arrays.asList(days).get(Integer.parseInt(dayofweek));

        return date;
    }

    public static String getIdDay (String dayofweek){

        String date;
        String[] days = {"Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
        Integer index = Arrays.asList(days).indexOf(dayofweek);
        date = index.toString();

        return date;
    }
}
