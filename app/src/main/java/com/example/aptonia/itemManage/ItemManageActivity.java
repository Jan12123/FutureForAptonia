package com.example.aptonia.itemManage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.AsyncTaskLoader;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.aptonia.R;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;
import com.squareup.picasso.Picasso;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ItemManageActivity extends AppCompatActivity {

    private static NameItem nameItem;

    private static ExpirationTable expirationTable;

    private static VolleyCallBack callBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);

        ((Toolbar) findViewById(R.id.item_manage_activity_toolbar)).setTitle(nameItem.getName());

        getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());

        getWindowManager().getDefaultDisplay().getWidth();

        Log.d("ItemManageActivity", String.valueOf(getWindowManager().getDefaultDisplay().getWidth()));

        WebLoader.loadImageFromDecathlon(this, nameItem.getID(), (ImageView) findViewById(R.id.item_image_item_manage_activity));

    }

    public static void show(Context context, NameItem nameItem, ExpirationTable expirationTable, VolleyCallBack callBack) {
        Intent intent = new Intent(context, ItemManageActivity.class);

        ItemManageActivity.nameItem = nameItem;
        ItemManageActivity.expirationTable = expirationTable;
        ItemManageActivity.callBack = callBack;

        context.startActivity(intent);

        Animatoo.INSTANCE.animateSlideLeft(context);
    }

}
