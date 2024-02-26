package com.example.aptonia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.aptonia.addFragment.AddFragment;
import com.example.aptonia.cloud.Cloud;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;
import com.example.aptonia.manageFragment.ManageFragment;
import com.example.aptonia.scheduleFragment.ScheduleFragment;
import com.example.aptonia.settingFragment.SettingsFragment;
import com.example.aptonia.storage.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //TODO: dodelat dark mode

    MeowBottomNavigation meowBottomNavigationView;

    Cloud cloud;

    ExpirationTable expirationTable;

    int lastFragmentId = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        String cloudURL = intent.getStringExtra("cloudURL");
        String rawItems = intent.getStringExtra("items");
        String itemsToInsertFileName = intent.getStringExtra("itemsToInsertFileName");

        this.expirationTable = new ExpirationTable(rawItems);

        Log.d("dateItems", expirationTable.getDateItems().toString());

        Log.d("MainActivityItems", String.valueOf(expirationTable));

        cloud = new Cloud(getApplicationContext(), cloudURL, expirationTable);

        WebLoader.queue = Volley.newRequestQueue(this);

        meowBottomNavigationView = findViewById(R.id.bottom_meow_navigation);

        AddFragment addFragment = new AddFragment(cloud, expirationTable, new FileManager(this, itemsToInsertFileName));
        ScheduleFragment scheduleFragment = new ScheduleFragment(cloud, expirationTable);
        ManageFragment manageFragment = new ManageFragment(cloud, expirationTable);
        //SettingsFragment settingsFragment = new SettingsFragment();

        meowBottomNavigationView.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_add_24));
        meowBottomNavigationView.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_calendar_month_24));
        meowBottomNavigationView.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_storage_24));
        //meowBottomNavigationView.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_settings_24));

        meowBottomNavigationView.setOnShowListener(model -> {
            Fragment fragment = null;

            if (model.getId() != lastFragmentId) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                View currentFocusedView = getCurrentFocus();

                if (currentFocusedView != null) {
                    inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

            switch(model.getId()) {
                case 1: fragment = addFragment; break;
                case 2: fragment = scheduleFragment; break;
                case 3: fragment = manageFragment; break;
                //case 4: fragment = settingsFragment; break;
            }

            assert fragment != null;

            if (model.getId() > lastFragmentId) {
                changeFragmentToRight(fragment);
            }
            else if (model.getId() < lastFragmentId) {
                changeFragmentToLeft(fragment);
            }
            else {
                setFragment(fragment);
            }

            lastFragmentId = model.getId();

            return null;
        });

        meowBottomNavigationView.show(2, true);
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    private void changeFragmentToRight(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    private void changeFragmentToLeft(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    public static void hideKeyboard(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}