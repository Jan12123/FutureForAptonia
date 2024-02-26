package com.example.aptonia.scheduleFragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;

public class ScheduleFragmentHeader extends RecyclerView.ViewHolder {

    TextView titleTextView;

    public ScheduleFragmentHeader(@NonNull View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.schedule_fragment_recyclerview_text);
    }
}
