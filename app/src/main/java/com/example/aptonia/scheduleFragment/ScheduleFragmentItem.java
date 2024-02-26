package com.example.aptonia.scheduleFragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.cloud.Cloud;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;

public class ScheduleFragmentItem extends RecyclerView.ViewHolder {

    public TextView date;
    public TextView name;
    public View background;

    boolean expanded;

    Context context;
    RecyclerView.Adapter<?> adapter;
    ExpirationTable expirationTable;

    String displayedDate;
    String displayedID;

    public ScheduleFragmentItem(View itemView, Context context, Cloud cloud, ExpirationTable expirationTable, ScheduleFragmentAdapter adapter) {
        super(itemView);

        this.context = context;
        this.adapter = adapter;
        this.expirationTable = expirationTable;

        this.expanded = false;

        date = itemView.findViewById(R.id.schedule_fragment_item_date_text_view);

        name = itemView.findViewById(R.id.schedule_fragment_item_name_text_view);

        background = itemView.findViewById(R.id.schedule_fragment_background_view);

        //https://api-eu.decathlon.net/stockcontrol-bff/v3/graphql/2011/model-info

        itemView.setOnLongClickListener(v -> {
            Builder.DIALOG.removeItemDialog(context, name.getText().toString(), expirationTable.findID(name.getText().toString()), date.getText().toString(), v1 -> {
                String[] date = displayedDate.split("\\.");

                cloud.removeDate(new DateItem(name.getText().toString(), displayedID, date[0], date[1], date[2]), new VolleyCallBack() {
                    @Override
                    public void onSuccess(Object... o) {
                        Toast.makeText(context, "Products successfully removed", Toast.LENGTH_SHORT).show();

                        expirationTable.remove(displayedID, date[0], date[1], date[2]);

                        if (adapter.getOrder()) {
                            adapter.orderByDate();
                        } else {
                            adapter.orderByName();
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Object... o) {
                        Toast.makeText(context, "Product failed to remove", Toast.LENGTH_SHORT).show();
                    }
                });
            }, v12 -> {}).show();

            return true;
        });
    }

    public void setDate(String date) {
        this.displayedDate = date;

        this.date.setText(date);
    }

    public void setID(String id) {
        this.displayedID = id;

        //this.id.setText("ID: " + id);
    }
}
