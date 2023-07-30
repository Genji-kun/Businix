package com.example.businix.utils;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date changeStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.parse(dateString);
    }

    public static Date changeStringToDate(String dateString, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateString);
    }

    public static boolean checkIsGreater(Date dateA, Date dateB) {
        long a = dateA.getTime() - dateB.getTime();
        return a > 0;
    }

    public static double getDiffHours(Date dateA, Date dateB) {
        long diffInMilliseconds = Math.abs(dateA.getTime() - dateB.getTime());
        double hours = (diffInMilliseconds * 1.0 / 60000) / 60;
        hours = Math.round(hours * 100.0) / 100.0;
        return hours;
    }

    public static Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date);
    }

}
