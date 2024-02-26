package com.example.aptonia.expirationTable;

import java.util.ArrayList;

public class MonthItem {

    private final ArrayList<DateItem> dateItems;

    private final int month;
    private final int year;

    public MonthItem(int month, int year) {
        this.month = month;
        this.year = year;

        this.dateItems = new ArrayList<>();
    }

    public ArrayList<DateItem> getDateItems() {
        return dateItems;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "CollectionItem{" +
                "dateItems=" + dateItems +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
