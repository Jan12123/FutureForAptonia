package com.example.aptonia.storage;

import android.content.Context;

import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileManager {

    Context context;
    String fileName;

    public FileManager(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public void saveData(ArrayList<NameItem> itemsToSave) {
        FileOutputStream outputStream = null;

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            writeData(outputStream, itemsToSave);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<NameItem> loadData() {
        ArrayList<NameItem> itemsToInsert = new ArrayList<>();

        FileInputStream inputStream = null;

        try {
            inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            itemsToInsert = extractData(reader);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return itemsToInsert;
    }

    public void clearData() {
        FileOutputStream outputStream = null;

        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            outputStream.write("".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeData(OutputStream stream, ArrayList<NameItem> items) throws IOException {
        StringBuilder parsedData = new StringBuilder();

        for (NameItem item : items) {
            String id = item.getID();
            String name = item.getName();

            for (DateItem dateItem : item.getDateItems()) {
                parsedData.append(id).append(",").append(name).append(",").append(dateItem.getDayNumber()).append(".").append(dateItem.getMonthNumber()).append(".").append(dateItem.getYear()).append(",");
            }
        }

        if (parsedData.length() != 0) {
            parsedData.substring(0, parsedData.length() - 1);
        }

        stream.write(parsedData.toString().getBytes());
    }

    private ArrayList<NameItem> extractData(BufferedReader reader) throws IOException {
        return new ArrayList<>(new ExpirationTable(reader.readLine()).getNameItems());
    }

}
