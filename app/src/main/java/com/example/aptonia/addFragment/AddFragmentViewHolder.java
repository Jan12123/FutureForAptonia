package com.example.aptonia.addFragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;

public class AddFragmentViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    TextView itemID;
    ImageView image;

    RecyclerView recyclerView;

    public AddFragmentViewHolder(@NonNull View itemView) {
        super(itemView);

        this.itemName = itemView.findViewById(R.id.add_fragment_item_layout_item_name);
        this.itemID = itemView.findViewById(R.id.add_fragment_item_layout_item_id);
        this.image = itemView.findViewById(R.id.add_fragment_item_layout_item_image);
        this.recyclerView = itemView.findViewById(R.id.add_fragment_item_layout_recycler_view);
    }
}
