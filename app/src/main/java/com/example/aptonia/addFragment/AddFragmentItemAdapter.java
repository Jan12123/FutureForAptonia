package com.example.aptonia.addFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.NameItem;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;
import java.util.List;

public class AddFragmentItemAdapter extends RecyclerView.Adapter<AddFragmentItemViewHolder> {

    Context context;
    NameItem nameItem;
    List<NameItem> itemsToInsert;
    AddFragmentAdapter adapter;

    public AddFragmentItemAdapter(Context context, NameItem nameItem, List<NameItem> itemsToInsert, AddFragmentAdapter adapter) {
        this.context = context;
        this.nameItem = nameItem;
        this.itemsToInsert = itemsToInsert;
        this.adapter = adapter;

        nameItem.getDateItems().sort((di1, di2) -> {
            if (Integer.parseInt(di1.getYear()) > Integer.parseInt(di2.getYear())) {
                return 1;
            }
            if (Integer.parseInt(di1.getYear()) < Integer.parseInt(di2.getYear())) {
                return -1;
            }

            if (Integer.parseInt(di1.getMonthNumber()) > Integer.parseInt(di2.getMonthNumber())) {
                return 1;
            }
            if (Integer.parseInt(di1.getMonthNumber()) < Integer.parseInt(di2.getMonthNumber())) {
                return -1;
            }

            return Integer.compare(Integer.parseInt(di1.getDayNumber()), Integer.parseInt(di2.getDayNumber()));
        });
    }

    @NonNull
    @Override
    public AddFragmentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddFragmentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_fragment_name_item_layout, parent, false), context, itemsToInsert, nameItem, this, adapter);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFragmentItemViewHolder holder, int position) {
        DateItem dateItem = nameItem.getDateItems().get(position);

        String date = dateItem.getDayNumber() + " / " + dateItem.getMonthNumber() + " / " + dateItem.getYear();

        holder.date.setText(date);

        DateTime dateTime = new DateTime(Integer.parseInt(dateItem.getYear()), Integer.parseInt(dateItem.getMonthNumber()), Integer.parseInt(dateItem.getDayNumber()), 0,0 );

        int dayRemaining = Days.daysBetween(DateTime.now().withTimeAtStartOfDay(), dateTime.withTimeAtStartOfDay()).getDays() + 1;

        if (dayRemaining < 0) {
            holder.date.setBackgroundResource(R.drawable.item_layout_date_caution);
        }
        else if (dayRemaining < 7) {
            holder.date.setBackgroundResource(R.drawable.item_layout_date_caution);
        }
        else if (dayRemaining < 14) {
            holder.date.setBackgroundResource(R.drawable.item_layout_date_warning);
        }
        else {
            holder.date.setBackgroundResource(R.drawable.item_layout_date_ok);
        }

        holder.daysLeft.setText(dayRemaining + " days left");
    }

    @Override
    public int getItemCount() {
        return nameItem.getDateItems().size();
    }
}
