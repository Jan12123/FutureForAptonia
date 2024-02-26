package com.example.aptonia.manageFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.MainActivity;
import com.example.aptonia.R;
import com.example.aptonia.StartLoadingActivity;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.Item;
import com.example.aptonia.expirationTable.NameItem;
import com.example.aptonia.itemManage.ItemManageActivity;

import java.util.List;

public class ManageFragmentViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    TextView itemID;

    ExpirationTable expirationTable;

    Context context;

    boolean clicked;

    public ManageFragmentViewHolder(@NonNull View itemView, ExpirationTable expirationTable, Context context) {
        super(itemView);

        this.expirationTable = expirationTable;
        this.context = context;

        this.itemName = itemView.findViewById(R.id.manage_fragment_item_layout_item_name);
        this.itemID = itemView.findViewById(R.id.manage_fragment_item_layout_item_id);

        this.clicked = false;

        /*itemView.setOnClickListener(view -> {
            if (clicked) {
                return;
            }

            clicked = true;


            NameItem nameItem = expirationTable.getNameItem(itemID.getText().toString());

            ItemManageActivity.show(context, nameItem, expirationTable, new VolleyCallBack() {
                @Override
                public void onSuccess(Object... o) {

                }

                @Override
                public void onFailure(Object... o) {

                }
            });

            clicked = false;
        });*/
    }

}
