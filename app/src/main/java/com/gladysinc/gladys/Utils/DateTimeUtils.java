package com.gladysinc.gladys.Utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static String parseDateTime(String dateString, String originalFormat, String outputFromat){

        SimpleDateFormat formatter = new SimpleDateFormat(originalFormat, Locale.FRANCE);
        Date date;
        try {
            date = formatter.parse(dateString);

            SimpleDateFormat dateFormat = new SimpleDateFormat(outputFromat, new Locale("FRENCH"));

            return dateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRelativeTimeSpan(String dateString, String originalFormat){

        SimpleDateFormat formatter = new SimpleDateFormat(originalFormat, Locale.FRANCE);
        Date date;
        try {
            date = formatter.parse(dateString);

            return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDay (String dayofweek){

        String date = null;

        switch (dayofweek){
            case "0":
                date = "Dimanche";
                break;
            case "1":
                date = "Lundi";
                break;
            case "2":
                date = "Mardi";
                break;
            case "3":
                date = "Mercredi";
                break;
            case "4":
                date = "Jeudi";
                break;
            case "5":
                date = "Vendredi";
                break;
            case "6":
                date = "Samedi";
                break;
        }
        return date;
    }

    public static String getIdDay (String dayofweek){

        String date = null;

        switch (dayofweek){
            case "Dimanche":
                date = "0";
                break;
            case "Lundi":
                date = "1";
                break;
            case "Mardi":
                date = "2";
                break;
            case "Mercredi":
                date = "3";
                break;
            case "Jeudi":
                date = "4";
                break;
            case "Vendredi":
                date = "5";
                break;
            case "Samedi":
                date = "6";
                break;
        }
        return date;
    }
}
