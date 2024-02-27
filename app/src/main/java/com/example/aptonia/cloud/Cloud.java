package com.example.aptonia.cloud;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Cloud {

    private final String scriptURL;
    private final RequestQueue requestQueue;
    private final ExpirationTable expirationTable;

    // Can trigger Google APPS script to return data or push data through it
    public Cloud(Context context, String scriptURL, ExpirationTable expirationTable) {
        this.scriptURL = scriptURL;
        this.expirationTable = expirationTable;

        this.requestQueue = prepareSerialRequestQueue(context);
        this.requestQueue.start();
    }

    // To make commands execute serially (eliminates Google sheet mistakes)
    private static RequestQueue prepareSerialRequestQueue(Context context) {
        File cacheFile = new File(context.getCacheDir(), "volley");

        Cache cache = new DiskBasedCache(cacheFile);

        RequestQueue queue = new RequestQueue(cache, new BasicNetwork(new HurlStack()), 1);

        queue.start();

        return queue;
    }

    public void addItem(ArrayList<NameItem> nameItems, VolleyCallBack callBack) {
        if (expirationTable != null) {
            nameItems = controlDates(nameItems);
        }

        StringRequest request = new StringRequest(Request.Method.POST, new URLBuilder(scriptURL)
                .setAction(URLBuilder.ScriptAction.ADD_DATA)
                .setData(nameItems)
                .getURL(), callBack::onSuccess, callBack::onFailure);

        request.setRetryPolicy(new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(600), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    private ArrayList<NameItem> controlDates(ArrayList<NameItem> nameItems) {
        ArrayList<DateItem> dateItems = new ArrayList<>();

        for (NameItem nameItem : nameItems) {
            dateItems.addAll(nameItem.getDateItems());
        }

        ArrayList<DateItem> clear = new ArrayList<>();

        for (DateItem dateItem : dateItems) {
            boolean control = true;

            for (DateItem dateItem1 : expirationTable.getDateItems()) {

                if (dateItem.toString().equals(dateItem1.toString())) {

                    control = false;

                    break;
                }
            }

            if (control) {
                clear.add(dateItem);
            }
        }

        return ExpirationTable.dateItemsToNameItems(clear);
    }

    public void removeDate(DateItem dateItem, VolleyCallBack callBack) {
        ArrayList<DateItem> list = new ArrayList<>();

        list.add(dateItem);

        requestQueue.add(new StringRequest(Request.Method.POST, new URLBuilder(scriptURL)
                .setAction(URLBuilder.ScriptAction.REMOVE_DATA)
                .setData(ExpirationTable.dateItemsToNameItems(list))
                .getURL(), callBack::onSuccess , callBack::onFailure));
    }

    // Not working yet
    public void setAptoniaDecathlonDataClasses(VolleyCallBack callBack, String barcodeRFIDClass, String idQRClass, String notFoundClass) {
        requestQueue.add(new StringRequest(Request.Method.POST, new URLBuilder(scriptURL)
                .setAction(URLBuilder.ScriptAction.SET_APTONIA_DECATHLON_DATA_CLASSES)
                .setClasses(barcodeRFIDClass, idQRClass, notFoundClass)
                .getURL(), callBack::onSuccess, callBack::onFailure));
    }

    // Deprecated
    // Data are send using Security script to make the whole process of initializing and communication with Script way more faster
    public void getItems(VolleyCallBack callBack) {
        requestQueue.add(new StringRequest(Request.Method.GET, new URLBuilder(scriptURL)
                .setAction(URLBuilder.ScriptAction.GET_DATA)
                .getURL(), callBack::onSuccess , callBack::onFailure));
    }

    public static String stringWithoutSpaces(String string) {
        return string.replace(" ", "_");
    }

}
