package com.example.aptonia.addFragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.R;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddFragmentAdapter extends RecyclerView.Adapter<AddFragmentViewHolder> {

    ExpirationTable expirationTable;
    Context context;
    List<NameItem> itemsToInsert;
    String[] itemsNames;

    public AddFragmentAdapter(Context context, ExpirationTable expirationTable, List<NameItem> itemsToInsert) {
        this.context = context;
        this.itemsToInsert = itemsToInsert;
        this.expirationTable = expirationTable;

        loadItemsNames();

        Log.d("itemsAddFragmentAdapter", Arrays.toString(itemsNames));
    }

    @NonNull
    @Override
    public AddFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddFragmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_fragment_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddFragmentViewHolder holder, int position) {
        Log.d("onBindViewHolder", "run");

        holder.itemName.setText(itemsToInsert.get(position).getName());
        holder.itemID.setText(itemsToInsert.get(position).getID());

        WebLoader.loadImageFromDecathlon(context, itemsToInsert.get(position).getID(), holder.image);

        AddFragmentItemAdapter addFragmentItemAdapter = new AddFragmentItemAdapter(context, itemsToInsert.get(position), itemsToInsert, this);

        holder.recyclerView.setAdapter(addFragmentItemAdapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return itemsToInsert.size();
    }

    private void loadItemsNames() {
        List<String> tmpItems = new ArrayList<>();

        for(int i = 0; i < expirationTable.getDateItems().size(); i++) {
            tmpItems.add(expirationTable.getDateItems().get(i).getName());
        }

        Set<String> set = new LinkedHashSet<>(tmpItems);

        itemsNames = set.toArray(new String[0]);
    }
}
