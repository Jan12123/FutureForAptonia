package com.example.aptonia.expirationTable;

// Data class
public class Item {
    protected String name;
    protected String ID;

    public Item(String name, String ID) {
        this.name = name;
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Item{" + name + '.' + ID + '}';
    }
}
