package com.example.aptonia.scheduleFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;
import com.example.aptonia.addFragment.AddFragmentViewHolder;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.cloud.Cloud;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class ScheduleFragment extends Fragment {

    ExpirationTable expirationTable;

    Cloud cloud;

    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;

    ScheduleFragmentAdapter adapter;

    Context context;

    public ScheduleFragment(Cloud cloud, ExpirationTable expirationTable) {
        this.cloud = cloud;
        this.expirationTable = expirationTable;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        this.adapter = new ScheduleFragmentAdapter(context, cloud, expirationTable);

        linearLayoutManager = new LinearLayoutManager(view.getContext());

        recyclerView = view.findViewById(R.id.schedule_item_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);

        adapter.orderByDate();

        setButtonGroup(view);

        return view;
    }

    AppCompatButton buttonDate;
    AppCompatButton buttonName;

    private void setButtonGroup(View view) {
        buttonDate =  view.findViewById(R.id.radio_button_date);
        buttonName = view.findViewById(R.id.radio_button_name);

        setButtonPressed(sortType);

        buttonDate.setOnClickListener(view1 -> setButtonPressed(0));

        buttonName.setOnClickListener(view12 -> setButtonPressed(1));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    int sortType = 0;

    private void setButtonPressed(int button) {
        buttonDate.setBackground(AppCompatResources.getDrawable(context, R.drawable.select_button_unpressed_drawable));
        buttonName.setBackground(AppCompatResources.getDrawable(context, R.drawable.select_button_unpressed_drawable));

        switch (button) {
            case 0:
                buttonDate.setBackground(AppCompatResources.getDrawable(context, R.drawable.select_button_pressed_drawable));
                adapter.orderByDate();
                break;
            case 1:
                buttonName.setBackground(AppCompatResources.getDrawable(context, R.drawable.select_button_pressed_drawable));
                adapter.orderByName();
                break;
        }

        sortType = button;
    }



}