package com.example.aptonia.cloud;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aptonia.StartLoadingActivity;

public class Security {

    private final Context context;
    private final String scriptURL;
    private final RequestQueue requestQueue;

    public Security(Context context, String scriptURL) {
        this.context = context;
        this.scriptURL = scriptURL;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getAptoniaData(VolleyCallBack callBack) {
        requestQueue.add(new StringRequest(Request.Method.GET, new URLBuilder(scriptURL)
                .setAction(URLBuilder.ScriptAction.GET_APTONIA_CLOUD_SCRIPT)
                .getURL(), callBack::onSuccess, callBack::onFailure));
    }

}
