package com.example.aptonia.cloud;

import android.util.Log;

import com.example.aptonia.StartLoadingActivity;
import com.example.aptonia.expirationTable.Date;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.NameItem;

import java.util.ArrayList;
import java.util.function.Consumer;

public class URLBuilder {

    private final String scriptURL;

    private ScriptAction action;
    private ArrayList<NameItem> data;
    private String[] classes;

    /**
     * @param scriptURL must include "/exec" and exclude "?"
     */
    public URLBuilder(String scriptURL) {
        this.scriptURL = scriptURL + "?";

        this.action = null;
        this.data = null;
    }

    public URLBuilder setAction(ScriptAction action) {
        this.action = action;

        return this;
    }

    public URLBuilder setData(ArrayList<NameItem> data) {
        this.data = data;

        return this;
    }

    public URLBuilder setClasses(String barcodeRFIDClass, String idQRClass, String notFoundClass) {
        this.classes = new String[]{ barcodeRFIDClass, idQRClass, notFoundClass };

        return this;
    }

    public String getURL() {
        StringBuilder URL = new StringBuilder(scriptURL);

        if (action == null) {
            throw new IllegalArgumentException("action is null");
        }

        switch (action) {
            case GET_APTONIA_CLOUD_SCRIPT:
                URL.append("action=getAptoniaCloudScript")
                        .append("&password=").append(StartLoadingActivity.mobileID);
                break;
            case SET_APTONIA_DECATHLON_DATA_CLASSES:
                URL.append("action=getAptoniaDecathlonDataClasses")
                        .append("&data=").append(classes[0]).append(";")
                        .append(classes[1]).append(";").append(classes[2]);
                break;
            case ADD_DATA:
                URL.append("action=addData")
                        .append("&data=").append(generateDataCommand());

                URL.deleteCharAt(URL.toString().length() - 1);
                break;
            case REMOVE_DATA:
                URL.append("action=removeData")
                        .append("&data=").append(generateDataCommand());
                break;
            case GET_DATA:
                URL.append("action=getData");
                break;
            case GET_UPDATE:
                URL.append("action=getUpdate");
                break;
        }

        Log.d("URLBuilder", URL.toString());

        return URL.toString();
    }

    String separator = "*";

    private String generateDataCommand() {
        StringBuilder result = new StringBuilder();

        for (NameItem nameItem : data) {
            result.append(nameItem.getID())
                    .append(separator).append(Cloud.stringWithoutSpaces(nameItem.getName()));

            for (DateItem item : nameItem.getDateItems()) {
                result.append(separator)
                        .append(item.getDayNumber())
                        .append(".")
                        .append(item.getMonthNumber())
                        .append(".")
                        .append(item.getYear());
            }

            result.append(";");
        }

        return result.toString();
    }

    public enum ScriptAction {
        // req: action, password
        GET_APTONIA_CLOUD_SCRIPT,

        // req: action
        SET_APTONIA_DECATHLON_DATA_CLASSES,

        // req: action, data
        ADD_DATA,

        // req: action, data
        REMOVE_DATA,

        // req: action
        GET_DATA,

        // req: action
        GET_UPDATE
    }
}
