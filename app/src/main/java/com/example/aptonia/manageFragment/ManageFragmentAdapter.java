package com.example.aptonia.manageFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.Item;

import java.util.ArrayList;
import java.util.List;

public class ManageFragmentAdapter extends RecyclerView.Adapter<ManageFragmentViewHolder> {

    ExpirationTable expirationTable;
    List<Item> items;
    Context context;

    public ManageFragmentAdapter(Context context, ExpirationTable expirationTable) {
        this.context = context;
        this.expirationTable = expirationTable;

        this.items = new ArrayList<>(expirationTable.getNamesAndIds());
    }

    @NonNull
    @Override
    public ManageFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ManageFragmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_fragment_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ManageFragmentViewHolder holder, int position) {
        holder.itemName.setText(items.get(position).getName());
        holder.itemID.setText(items.get(position).getID());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void filterList(String text) {
        items.clear();

        for (Item item : expirationTable.getNamesAndIds()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                items.add(item);
            }

            else if (item.getID().toLowerCase().contains(text.toLowerCase())) {
                items.add(item);
            }
        }

        notifyDataSetChanged();
    }
}
