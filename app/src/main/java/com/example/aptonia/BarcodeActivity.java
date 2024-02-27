package com.example.aptonia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.manualSearch.ManualSearch;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

// Activity for recognizing RFID, QR or simple barcode
public class BarcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private MaterialButton backButton;
    private ZXingScannerView scannerView;
    private ProgressBar progressBar;
    private String barcodeData;
    private LinearLayout manualSearchLayout;
    private MaterialButton manualSearchButton;

    private static ExpirationTable expirationTable;
    private static VolleyCallBack volleyCallBack;

    // Stores boolean if the activity can offer manual search option
    private static boolean showManualSearchOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        scannerView = findViewById(R.id.barcode_activity_zx_scanner);
        progressBar = findViewById(R.id.barcode_progress_bar);
        manualSearchLayout = findViewById(R.id.barcode_activity_manual_search_layout);
        manualSearchButton = findViewById(R.id.barcode_activity_manual_search_button);

        // Open manual search activity in case it is not possible to detect code or the product is not in database
        manualSearchButton.setOnClickListener(v1 -> ManualSearch.getItem(BarcodeActivity.this, barcodeData, expirationTable, new VolleyCallBack() {
            @Override
            public void onSuccess(Object... o) {
                Builder.DIALOG.addItemDialog(BarcodeActivity.this, o[0].toString(), o[1].toString(), "", new VolleyCallBack() {
                    @Override
                    public void onSuccess(Object... o) {
                        end(o);
                    }

                    @Override
                    public void onFailure(Object... o) {
                        progressBar.setVisibility(View.INVISIBLE);

                        scannerView.resumeCameraPreview(BarcodeActivity.this);

                        called = false;
                    }
                }).show();
            }

            @Override
            public void onFailure(Object... o) {
                scannerView.resumeCameraPreview(BarcodeActivity.this);
            }
        }));

        backButton = findViewById(R.id.barcode_fragment_back_button);
        backButton.setOnClickListener(v -> {
            WebLoader.queue.cancelAll(request -> true);

            finish();

            Animatoo.INSTANCE.animateZoom(BarcodeActivity.this);
        });

        scannerView.startCamera();

        Dexter.withActivity(BarcodeActivity.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(BarcodeActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(() -> {
            barcodeData = "";

            manualSearchLayout.setVisibility(View.VISIBLE);
        }, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        scannerView.stopCamera();
    }

    private boolean called = false;

    // After barcode detection this function is called
    private synchronized void process() {
        Log.d("detections", barcodeData);

        if (barcodeData.equals("")) {
            scannerView.resumeCameraPreview(this);

            return;
        }

        called = true;

        if (showManualSearchOption) {
            manualSearchLayout.setVisibility(View.INVISIBLE);
        }

        Log.d("barcodeActivity", barcodeData);

        progressBar.setVisibility(View.VISIBLE);

        // Get data from Decathlon web
        WebLoader.using(BarcodeActivity.this).find(barcodeData).withResponse(new VolleyCallBack() {
            @Override
            public void onSuccess(Object... o) {
                progressBar.setVisibility(View.INVISIBLE);

                Log.d("BarcodeActivity-addDialog", Arrays.toString(o));

                Builder.DIALOG.addItemDialog(BarcodeActivity.this, o[0].toString(), o[1].toString(), "", new VolleyCallBack() {
                    @Override
                    public void onSuccess(Object... o) {
                        Log.d("BarcodeActivity-addDialog", o[0].toString());

                        end(o);
                    }

                    @Override
                    public void onFailure(Object... o) {
                        progressBar.setVisibility(View.INVISIBLE);

                        scannerView.resumeCameraPreview(BarcodeActivity.this);

                        called = false;

                        onResume();
                    }
                }).show();

                called = false;
            }

            @Override
            public void onFailure(Object... o) {
                progressBar.setVisibility(View.INVISIBLE);

                scannerView.resumeCameraPreview(BarcodeActivity.this);

                called = false;

                if (showManualSearchOption) {
                    if (barcodeData.contains("(")) {
                        barcodeData = barcodeData.split("\\(")[1].substring(4);
                    }

                    manualSearchLayout.setVisibility(View.VISIBLE);
                }
            }
        }).execute();
    }

    private void end(Object... o) {
        BarcodeActivity.volleyCallBack.onSuccess(o);

        Log.d("endBarcode", Arrays.toString(o));

        finish();

        Animatoo.INSTANCE.animateZoom(BarcodeActivity.this);
    }

    public static void getItem(Context context, ExpirationTable expirationTable, boolean showManualSearchOption, VolleyCallBack volleyCallBack) {
        Intent intent = new Intent(context, BarcodeActivity.class);

        BarcodeActivity.expirationTable = expirationTable;
        BarcodeActivity.volleyCallBack = volleyCallBack;
        BarcodeActivity.showManualSearchOption = showManualSearchOption;

        context.startActivity(intent);

        Animatoo.INSTANCE.animateZoom(context);
    }

    // Process Barcode detector result
    @Override
    public void handleResult(Result rawResult) {
        if (showManualSearchOption) {
            manualSearchLayout.setVisibility(View.INVISIBLE);
        }

        barcodeData = rawResult.getText();

        if (called) {
            return;
        }

        process();
    }
}
