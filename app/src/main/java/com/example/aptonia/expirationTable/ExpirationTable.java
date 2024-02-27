package com.example.aptonia.expirationTable;

import android.util.Log;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpirationTable {

    private final List<DateItem> dateItems;
    private final List<MonthItem> monthItems;
    private final List<NameItem> nameItems;
    private final List<MonthItem> rangeItems;
    private final List<Item> namesAndIds;

    // Programs database
    public ExpirationTable(String rawItems) {
        this.dateItems = new ArrayList<>();
        this.monthItems = new ArrayList<>();
        this.nameItems = new ArrayList<>();
        this.rangeItems = new ArrayList<>();
        this.namesAndIds = new ArrayList<>();

        parseRawItemsToDateItems(rawItems);

        castItemsToMonthItems(monthItems);
        castItemsToNameItems(nameItems);

        Log.d("ids", String.valueOf(getIDs()));
    }

    // remove Item from database
    public void remove(String id, String day, String month, String year) {
        for (DateItem dateItem : dateItems) {
            if (!dateItem.getID().equals(id)) {
                continue;
            }

            if (dateItem.getDayNumber().equals(day) && dateItem.getMonthNumber().equals(month) && dateItem.getYear().equals(year)) {
                dateItems.remove(dateItem);

                refresh();

                return;
            }
        }
    }

    // parse string database from HTTP response of APPS Script to ArrayList of DateItems
    private void parseRawItemsToDateItems(String rawItems) {
        if (rawItems == null || rawItems.equals("")) {
            return;
        }

        String[] cells = rawItems.split(",");


        String itemID = cells[0];
        String itemName = cells[1];

        for (int i = 0; i < cells.length; i++) {
            Log.d(String.valueOf(i), cells[i]);

            if (cells[i].matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) {
                String[] date = cells[i].split("\\.");

                dateItems.add(new DateItem(itemName, itemID, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])));
            }
            else if (cells[i].matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                String[] date = cells[i].split("/");

                String tmp = date[0];
                date[0] = date[1];
                date[1] = tmp;

                dateItems.add(new DateItem(itemName, itemID, Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])));
            }

            else {
                itemID = cells[i++];
                itemName = cells[i];

                namesAndIds.add(new Item(itemName, itemID));
            }
        }
    }

    private void castItemsToNameItems(List<NameItem> target) {
        target.clear();

        for (DateItem dateItem : dateItems) {
            if (findNameItem((ArrayList<NameItem>) target, dateItem.getName()) == -1) {
                target.add(new NameItem(dateItem.getName(), dateItem.getID()));
            }

            target.get(findNameItem((ArrayList<NameItem>) target, dateItem.getName())).getDateItems().add(dateItem);
        }

        target.sort((ni1, ni2) -> String.CASE_INSENSITIVE_ORDER.compare(ni1.getName(), ni2.getName()));
    }

    public static int findNameItem(ArrayList<NameItem> nameItems, String name) {
        for (int i = 0; i < nameItems.size(); i++) {
            if(nameItems.get(i).getName().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    private void castItemsToMonthItems(List<MonthItem> target) {
        monthItems.clear();

        for (DateItem dateItem : dateItems) {
            int month = Integer.parseInt(dateItem.getMonthNumber());
            int year = Integer.parseInt(dateItem.getYear());

            if (findMonthItem(target, month, year) == -1) {
                monthItems.add(new MonthItem(month, year));
            }

            monthItems.get(findMonthItem(target, month, year)).getDateItems().add(dateItem);
        }

        monthItems.sort((mi1, mi2) -> {
            if (mi1.getYear() > mi2.getYear()) {
                return 1;
            }
            if (mi1.getYear() < mi2.getYear()) {
                return -1;
            }

            return Integer.compare(mi1.getMonth(), mi2.getMonth());
        });
    }

    public static int findMonthItem(List<MonthItem> monthItems, int month, int year) {
        for (int i = 0; i < monthItems.size(); i++) {
            MonthItem monthItem = monthItems.get(i);

            if (monthItem.getMonth() == month && monthItem.getYear() == year) {
                return i;
            }
        }

        return -1;
    }

    private void castMonthItemToRangeItems(DateTime start, DateTime end) {
        rangeItems.clear();

        for (MonthItem monthItem : monthItems) {
            if (monthItem.getYear() >= start.getYear() && monthItem.getYear() <= end.getYear() && monthItem.getMonth() >= start.getMonthOfYear()  && monthItem.getMonth() <= end.getMonthOfYear()) {
                rangeItems.add(monthItem);
            }
        }

        if (rangeItems.size() == 0) {
            return;
        }

        MonthItem monthItem = new MonthItem(start.getMonthOfYear(), start.getYear());

        for (DateItem dateItem : rangeItems.get(0).getDateItems()) {
            if (Integer.parseInt(dateItem.getDayNumber()) >= start.getDayOfMonth()) {
                monthItem.getDateItems().add(dateItem);
            }
        }

        rangeItems.set(0, monthItem);

        if (rangeItems.size() >= 2) {
            MonthItem monthItem1 = new MonthItem(end.getMonthOfYear(), end.getYear());

            for (DateItem dateItem : rangeItems.get(rangeItems.size() - 1).getDateItems()) {
                if (Integer.parseInt(dateItem.getDayNumber()) <= end.getDayOfMonth()) {
                    monthItem1.getDateItems().add(dateItem);
                }
            }

            rangeItems.set(rangeItems.size() - 1, monthItem1);
        }
    }

    public List<DateItem> getDateItems() {
        return dateItems;
    }

    public List<MonthItem> getMonthItems() {
        return monthItems;
    }

    public List<NameItem> getNameItems() {
        return nameItems;
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();

        for (Item item : namesAndIds) {
            names.add(item.getName());
        }

        return names;
    }

    public List<String> getIDs() {
        List<String> IDs = new ArrayList<>();

        for (Item item : namesAndIds) {
            IDs.add(item.getID());
        }

        return IDs;
    }

    public NameItem getNameItem(String id) {
        for (NameItem nameItem : nameItems) {
            if (nameItem.getID().equals(id)) {
                return nameItem;
            }
        }

        return null;
    }

    public List<Item> getNamesAndIds() {
        return namesAndIds;
    }

    public void add(DateItem dateItem) {
        dateItems.add(dateItem);

        refresh();
    }

    public void addAll(List<NameItem> itemsToInsert) {
        ArrayList<DateItem> toAdd = new ArrayList<>();
        ArrayList<DateItem> items = new ArrayList<>();

        for (NameItem nameItem : itemsToInsert) {
            items.addAll(nameItem.getDateItems());
        }

        for (DateItem dateItem : items) {
            boolean ok = true;

            for (DateItem dateItem1 : dateItems) {
                if (dateItem.equals(dateItem1)) {
                    ok = false;
                }
            }

            if (ok) {
                toAdd.add(dateItem);
            }
        }

        dateItems.addAll(toAdd);

        refresh();
    }

    public String findID(String name) {
        for (Item item : getNamesAndIds()) {
            if (item.getName().equals(name)) {
                return item.getID();
            }
        }

        return null;
    }

    public String findName(String id) {
        for (Item item : getNamesAndIds()) {
            if (item.getID().equals(id)) {
                return item.getName();
            }
        }

        return null;
    }

    private void refresh() {
        castItemsToMonthItems(monthItems);
        castItemsToNameItems(nameItems);
    }

    @NonNull
    @Override
    public String toString() {
        return dateItems.toString();
    }

    public static ArrayList<NameItem> dateItemsToNameItems(ArrayList<DateItem> dateItems) {
        ArrayList<NameItem> nameItems = new ArrayList<>();

        for (DateItem dateItem : dateItems) {
            if (findNameItem(nameItems, dateItem.getName()) == -1) {
                nameItems.add(new NameItem(dateItem.getName(), dateItem.getID()));
            }

            nameItems.get(findNameItem(nameItems, dateItem.getName())).getDateItems().add(dateItem);
        }

        nameItems.sort((ni1, ni2) -> String.CASE_INSENSITIVE_ORDER.compare(ni1.getName(), ni2.getName()));

        return nameItems;
    }

    public static List<DateItem> sortDateItemsByName(List<DateItem> dateItems) {
        dateItems.sort(Comparator.comparing(o -> o.getName().toLowerCase()));

        return dateItems;
    }

    public static List<DateItem> sortDateItemsByDate(List<DateItem> dateItems) {
        dateItems.sort((di1, di2) -> {
            if (Integer.parseInt(di1.getYear()) > Integer.parseInt(di2.getYear())) {
                return 1;
            }
            if (Integer.parseInt(di1.getYear()) < Integer.parseInt(di2.getYear())) {
                return -1;
            }

            if (Integer.parseInt(di1.getMonthNumber()) > Integer.parseInt(di2.getMonthNumber())) {
                return 1;
            }
            if (Integer.parseInt(di1.getMonthNumber()) < Integer.parseInt(di2.getMonthNumber())) {
                return -1;
            }

            return Integer.compare(Integer.parseInt(di1.getDayNumber()), Integer.parseInt(di2.getDayNumber()));
        });

        return dateItems;
    }

}
