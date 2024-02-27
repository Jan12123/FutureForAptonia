package com.example.aptonia.scheduleFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;
import com.example.aptonia.cloud.Cloud;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.MonthItem;
import com.example.aptonia.expirationTable.NameItem;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;

// RecyclerView.Adapter is class that manages RecyclerView.ViewHolder (one from list) in recycler view (load data to it when user scroll to their position)
public class ScheduleFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // This RecyclerView has multiple ViewHolders
    // ID for the ViewHolders
    private static final int header = 11;
    private static final int item = 12;


    private boolean sortedByDates;
    private Context context;
    private Cloud cloud;
    private ExpirationTable expirationTable;

    private ArrayList<Integer> viewTypes;

    public ScheduleFragmentAdapter(Context context, Cloud cloud, ExpirationTable expirationTable) {
        this.context = context;
        this.expirationTable = expirationTable;
        this.cloud = cloud;

        this.viewTypes = new ArrayList<>();

        this.sortedByDates = true;
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypes.get(position);
    }

    // Creating data when shown
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == header) {
            return new ScheduleFragmentHeader(LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_fragment_recyclerview_header, parent, false));
        }

        return new ScheduleFragmentItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_fragment_recyclerview_item, parent, false), context, cloud, expirationTable, ScheduleFragmentAdapter.this);
    }

    // Loading data when shown
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case header:
                ScheduleFragmentHeader headerView = (ScheduleFragmentHeader) holder;

                int pos = findRealPos(position, header);

                if (sortedByDates) {
                    String title = DateItem.getMonthFullName(DateItem.getMonthShortName(expirationTable.getMonthItems().get(pos).getMonth())) + " " + expirationTable.getMonthItems().get(pos).getYear();

                    headerView.titleTextView.setText(title);
                }
                else {
                    headerView.titleTextView.setText(expirationTable.getNameItems().get(pos).getName());
                }

                break;
            case item:
                ScheduleFragmentItem itemView = (ScheduleFragmentItem) holder;

                int pos1 = findRealPos(position, item);

                DateItem dateItem;

                if (sortedByDates) {
                    dateItem = ExpirationTable.sortDateItemsByDate(expirationTable.getDateItems()).get(pos1);
                }
                else {
                    dateItem = ExpirationTable.sortDateItemsByName(expirationTable.getDateItems()).get(pos1);
                }

                int day = Integer.parseInt(dateItem.getDayNumber());
                int month = Integer.parseInt(dateItem.getMonthNumber());
                int year = Integer.parseInt(dateItem.getYear());

                String date = day + "." + month + "." + year;

                itemView.setDate(date);
                itemView.setID(dateItem.getID());

                int dayRemaining = Days.daysBetween(DateTime.now(), new DateTime(year, month, day, 0, 0, 0,0)).getDays() + 1;

                if (dayRemaining < 7) {
                    itemView.background.setBackgroundResource(R.drawable.item_layout_date_caution);
                }
                else if (dayRemaining < 14) {
                    itemView.background.setBackgroundResource(R.drawable.item_layout_date_warning);
                }
                else {
                    itemView.background.setBackgroundResource(R.drawable.item_layout_date_ok);
                }

                itemView.name.setText(dateItem.getName());

                break;
        }
    }

    @Override
    public int getItemCount() {
        return viewTypes.size();
    }

    public void orderByDate() {
        this.viewTypes.clear();

        ArrayList<MonthItem> monthItems = (ArrayList<MonthItem>) expirationTable.getMonthItems();

        for (MonthItem monthItem : monthItems) {
            this.viewTypes.add(header);

            for (DateItem dateItem : monthItem.getDateItems()) {
                this.viewTypes.add(item);
            }
        }

        sortedByDates = true;

        this.notifyDataSetChanged();
    }

    private int findRealPos(int pos, int type) {
        int result = 0;

        for (int i = 0; i < pos; i++) {
            if (viewTypes.get(i) == type) {
                result++;
            }
        }

        return result;
    }

    public void orderByName() {
        this.viewTypes.clear();

        ArrayList<NameItem> nameItems = ExpirationTable.dateItemsToNameItems((ArrayList<DateItem>) expirationTable.getDateItems());

        for (NameItem nameItem : nameItems) {
            this.viewTypes.add(header);

            for (DateItem dateItem : nameItem.getDateItems()) {
                this.viewTypes.add(item);
            }
        }

        sortedByDates = false;

        this.notifyDataSetChanged();
    }

    public int getLength() {
        return viewTypes.size();
    }

    public boolean getOrder() {
        return sortedByDates;
    }
}
