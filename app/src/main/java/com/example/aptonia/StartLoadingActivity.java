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

    public static String appVersionName = "Farewell";
    public static String appVersionID = "1.4.0"; // month - year - index (from 0)
    public static String mobileID = "263b7f7ef76e08eb"; // aka password

    String securityURL = "XXX.XXX";
    String cloudURL;

    String items;
    String itemsToInsertFileName = "productsToUpload.txt";
    Security security;

    // Begin of program
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Suppress NIGHT_MODE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);

        // Set Version texts
        ((TextView) findViewById(R.id.version_name)).setText(appVersionName);
        ((TextView) findViewById(R.id.version_id)).setText(appVersionID);

        // Trigger security script on Google to get necessary data for continue
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

    // Ends activity start activity and starts MainActivity
    private void endActivity() {
        // Intent is class where data during activities change can be stored
        // Allows us not to create separate data class and use it as simple data bridge between two activities
        Intent intent = new Intent(StartLoadingActivity.this, MainActivity.class);

        intent.putExtra("items", items);
        intent.putExtra("cloudURL", cloudURL);
        intent.putExtra("itemsToInsertFileName", itemsToInsertFileName);

        startActivity(intent);

        // Animates activities change
        Animatoo.INSTANCE.animateZoom(this);

        Log.d("endActivity", "end");

        finish();
    }
}
