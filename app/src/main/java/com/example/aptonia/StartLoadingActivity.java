package com.example.aptonia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.aptonia.cloud.Security;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;


import java.io.IOException;
import java.util.Arrays;


public class StartLoadingActivity extends AppCompatActivity {

    // String appToken = "eLtDxqLTT4GdYe103USZ3F:APA91bGez3Kks9-K2S1q4cOT6TLmdFzk5cg2wfgrxXvTppxJq48cdtdkzUBIUix13v4zcbw6TM428iuLolh2ekA2doYP_nla3S9hSrfsYIc3lWj2CxRZgCfUIbewWdzcKGjNpCkbYeT6";

    public static String appVersionName = "Farewell";
    public static String appVersionID = "1.4.0"; // month - year - index (from 0)
    public static String mobileID = "263b7f7ef76e08eb"; // aka password

    String securityURL = "https://script.google.com/macros/s/AKfycbzQr5SsqLS2OdiPMidd5Au33f7ACSf0Fra53VeWtpruD5bYJOKbDcOwLMDD3MGRSOZGNg/exec";
    String cloudURL;

    String items;
    String itemsToInsertFileName = "productsToUpload.txt";
    Security security;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);

        //Toast.makeText(this, mobileID, Toast.LENGTH_SHORT).show();

        ((TextView) findViewById(R.id.version_name)).setText(appVersionName);
        ((TextView) findViewById(R.id.version_id)).setText(appVersionID);

        new Security(this, securityURL).getAptoniaData(new VolleyCallBack() {
            @Override
            public void onSuccess(Object... o) {
                Log.d("getAptoniaSuccess", Arrays.toString(o));

                String[] data = Arrays.toString(o).substring(1, Arrays.toString(o).length() - 1).split(";");

                Log.d("split", Arrays.toString(data));

                cloudURL = data[0];

                WebLoader.barcodeRFIDClass = data[1];
                WebLoader.idQRClass = data[2];
                WebLoader.notFoundClass = data[3];

                WebLoader.testBarcodeRFIDClass = data[4];
                WebLoader.testIdQRClass = data[5];
                WebLoader.testNotFoundClass = data[6];

                items = data[7];

                Log.d("data", items);

                endActivity();
            }

            @Override
            public void onFailure(Object... o) {
                Log.d("getAptoniaError", Arrays.toString(o));

                System.exit(0);
            }
        });
    }

    private void endActivity() {
        Intent intent = new Intent(StartLoadingActivity.this, MainActivity.class);

        intent.putExtra("items", items);
        intent.putExtra("cloudURL", cloudURL);
        intent.putExtra("itemsToInsertFileName", itemsToInsertFileName);

        startActivity(intent);

        Animatoo.INSTANCE.animateZoom(this);

        Log.d("endActivity", "end");

        finish();
    }
}
