package com.example.aptonia.manageFragment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.R;

public class ManageFragmentViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    TextView itemID;

    public ManageFragmentViewHolder(@NonNull View itemView) {
        super(itemView);

        this.itemName = itemView.findViewById(R.id.manage_fragment_item_layout_item_name);
        this.itemID = itemView.findViewById(R.id.manage_fragment_item_layout_item_id);

    }

}
