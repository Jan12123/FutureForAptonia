package com.example.aptonia.expirationTable;

import android.util.Log;

import androidx.annotation.NonNull;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public boolean isFutureFrom(Date a) {
        Log.d("this", this.toString());
        Log.d("a", a.toString());

        if(a.year > this.year) {
            return false;
        }
        if(a.year < this.year) {
            return true;
        }

        if(a.month > this.month) {
            return false;
        }
        if(a.month < this.month) {
            return true;
        }

        return a.day < this.day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @NonNull
    @Override
    public String toString() {
        return "Date{" + day + "." + month + "." + year + "}";
    }
}
