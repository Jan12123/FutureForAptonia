package com.example.aptonia.addFragment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.NameItem;

import java.util.ArrayList;
import java.util.List;

public class AddFragmentItemViewHolder extends RecyclerView.ViewHolder {

    TextView date;
    TextView daysLeft;

    Context context;
    List<NameItem> itemsToInsert;
    NameItem nameItem;
    AddFragmentItemAdapter itemAdapter;
    AddFragmentAdapter adapter;

    public AddFragmentItemViewHolder(@NonNull View itemView, Context context, List<NameItem> itemsToInsert, NameItem nameItem, AddFragmentItemAdapter itemAdapter, AddFragmentAdapter adapter) {
        super(itemView);

        this.context = context;
        this.itemsToInsert = itemsToInsert;
        this.nameItem = nameItem;
        this.itemAdapter = itemAdapter;
        this.adapter = adapter;

        date = itemView.findViewById(R.id.add_fragment_name_item_layout_date);
        daysLeft = itemView.findViewById(R.id.add_fragment_name_item_layout_days_left);

        itemView.setOnLongClickListener(v -> {
            Builder.DIALOG.areYouSureDialog(context, "Confirm delete", "Are you sure you want to detele this date?", v12 -> {
                int index = getDateItem(date.getText().toString());

                nameItem.getDateItems().remove(index);

                if (nameItem.getDateItems().isEmpty()) {
                    itemsToInsert.remove(nameItem);

                    adapter.notifyDataSetChanged();
                }
                else {
                    itemAdapter.notifyDataSetChanged();
                }
            }, v1 -> {}).show();

            return true;
        });
    }

    private int getDateItem(String date) {
        String[] tmpDate = date.split(" / ");

        String day = tmpDate[0];
        String month = tmpDate[1];
        String year = tmpDate[2];

        for (int i = 0; i < nameItem.getDateItems().size(); i++) {
            DateItem dateItem = nameItem.getDateItems().get(i);

            if (dateItem.getDayNumber().equals(day) && dateItem.getMonthNumber().equals(month) && dateItem.getYear().equals(year)) {
                return i;
            }
        }

        return -1;
    }

}
