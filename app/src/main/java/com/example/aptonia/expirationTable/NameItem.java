package com.example.aptonia.expirationTable;

import java.util.ArrayList;

public class NameItem extends Item {

    private final ArrayList<DateItem> dateItems;

    private final String name;
    private final String ID;

    public NameItem(String name, String ID) {
        super("", "");
        this.name = name;
        this.ID = ID;

        this.dateItems = new ArrayList<>();
    }

    public ArrayList<DateItem> getDateItems() {
        return dateItems;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "NameItem{" +
                "dateItems=" + dateItems +
                ", name=" + name +
                ", ID=" + ID +
                '}';
    }

}
