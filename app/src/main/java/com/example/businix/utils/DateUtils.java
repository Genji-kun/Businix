package com.example.businix.utils;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static boolean compareWithoutTime(Date dateA, Date dateB) {
        Calendar a = Calendar.getInstance();
        a.setTime(dateA);
        Calendar b = Calendar.getInstance();
        b.setTime(dateB);
        if (a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static boolean isAtLeast18YearsOld(Date birthDate) {
        Calendar currentDate = Calendar.getInstance();
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);
        int age = currentDate.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age >= 18;
    }

    public static Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date);
    }

}
