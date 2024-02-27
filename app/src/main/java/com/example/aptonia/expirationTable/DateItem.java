package com.example.aptonia.expirationTable;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

// Data class
public class DateItem extends Item {

    private String dayName;
    private String dayNumber;
    private String monthName;
    private String monthNumber;
    private String year;

    public DateItem(String name, String ID, int day, int month, int year) {
        super(name, ID);

        this.dayNumber = String.valueOf(day);
        this.dayName = getDayShortName(day);

        this.monthNumber = String.valueOf(month);
        this.monthName = getMonthShortName(month);

        this.year = String.valueOf(year);
    }

    public DateItem(String name, String ID, String day, String month, String year) {
        super(name, ID);

        this.dayNumber = day;
        this.dayName = (day.equals("")) ? "" : getDayShortName(Integer.parseInt(day));

        this.monthNumber = month;
        this.monthName = (month.equals("")) ? "" : getMonthShortName(Integer.parseInt(month));

        this.year = year;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public void setDayNumber(String dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setMonthNumber(String monthNumber) {
        this.monthNumber = monthNumber;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDayName() {
        return dayName;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getMonthName() {
        return monthName;
    }

    public String getMonthNumber() {
        return monthNumber;
    }

    public String getYear() {
        return year;
    }

    public static boolean isDay(String day) {
        switch(day) {
            case "Mon":
            case "Tue":
            case "Wed":
            case "Thu":
            case "Fri":
            case "Sat":
            case "Sun":
                return true;
            default: return false;
        }
    }

    public static int getDayNumber(String day) {
        switch(day.substring(0, 3)) {
            case "Mon": return 1;
            case "Tue": return 2;
            case "Wed": return 3;
            case "Thu": return 4;
            case "Fri": return 5;
            case "Sat": return 6;
            case "Sun": return 7;
            default: return -1;
        }
    }

    public static String getDayShortName(int day) {
        switch(day) {
            case 1: return "Mon";
            case 2: return "Tue";
            case 3: return "Wed";
            case 4: return "Thu";
            case 5: return "Fri";
            case 6: return "Sat";
            case 7: return "Sun";
            default: return null;
        }
    }

    public static String getDayFullName(String day) {
        switch(day.substring(0, 3)) {
            case "Mon": return "Monday";
            case "Tue": return "Tuesday";
            case "Wed": return "Wednesday";
            case "Thu": return "Thursday";
            case "Fri": return "Friday";
            case "Sat": return "Saturday";
            case "Sun": return "Sunday";
            default: return "";
        }
    }

    public static String getMonthShortName(int month) {
        switch(month) {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7: return "Jul";
            case 8: return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
            default: return null;
        }
    }

    public static int getMonthNumber(String month) {
        switch(month.substring(0, 3)) {
            case "Jan": return 1;
            case "Feb": return 2;
            case "Mar": return 3;
            case "Apr": return 4;
            case "May": return 5;
            case "Jun": return 6;
            case "Jul": return 7;
            case "Aug": return 8;
            case "Sep": return 9;
            case "Oct": return 10;
            case "Nov": return 11;
            case "Dec": return 12;
            default: return -1;
        }
    }

    public static String getMonthFullName(String month) {
        switch(month.substring(0, 3)) {
            case "Jan": return "January";
            case "Feb": return "February";
            case "Mar": return "March";
            case "Apr": return "April";
            case "May": return "May";
            case "Jun": return "June";
            case "Jul": return "July";
            case "Aug": return "August";
            case "Sep": return "September";
            case "Oct": return "October";
            case "Nov": return "November";
            case "Dec": return "December";
            default: return "";
        }
    }

    public static String getDayName(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        return getDayShortName(calendar.get(Calendar.DAY_OF_WEEK));
    }

    @Override
    public String toString() {
        return getID() + ":" + getDayNumber() + "." + getMonthNumber() + ":" + getYear();
    }

    public boolean equals(@Nullable DateItem dateItem) {
        assert dateItem != null;

        return dateItem.getID().equals(this.getID()) && dateItem.getDayNumber().equals(this.getDayNumber()) &&
                dateItem.getMonthNumber().equals(this.getMonthNumber()) && dateItem.getYear().equals(this.getYear());
    }
}
