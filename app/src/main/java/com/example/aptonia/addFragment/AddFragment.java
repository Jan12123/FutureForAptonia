package com.example.aptonia.addFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.BarcodeActivity;
import com.example.aptonia.R;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.cloud.Cloud;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;
import com.example.aptonia.storage.FileManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

// Fragment is screen with shared context (having so many Activities is slow and not effective)
// One activity can have several fragments

// This one for getting data and then save them to the database and Google sheets
public class AddFragment extends Fragment {

    ExpirationTable expirationTable;
    ArrayList<NameItem> itemsToInsert;

    Cloud cloud;
    Context context;
    FileManager fileManager;

    RecyclerView recyclerView;
    Button addButton;
    ProgressBar progressBar;

    AddFragmentAdapter addFragmentAdapter;
    LinearLayoutManager linearLayoutManager;

    public AddFragment(Cloud cloud, ExpirationTable expirationTable, FileManager fileManager) {
        this.cloud = cloud;
        this.expirationTable = expirationTable;
        this.fileManager = fileManager;

        this.itemsToInsert = fileManager.loadData();
    }

    // Get parents (root activity) context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView", "here");

        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Init RecyclerView (views allows us to scroll content effectively and save memory)
        addFragmentAdapter = new AddFragmentAdapter(view.getContext(), expirationTable, itemsToInsert);
        linearLayoutManager = new LinearLayoutManager(view.getContext());

        recyclerView = view.findViewById(R.id.list_item_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(addFragmentAdapter);

        progressBar = view.findViewById(R.id.add_fragment_progress_bar);

        MaterialButton save = view.findViewById(R.id.add_fragment_save_item_button);

        MaterialButton clear = view.findViewById(R.id.add_fragment_clear_item_button);

        save.setVisibility(View.GONE);
        save.setOnClickListener(v -> {
            if (itemsToInsert.isEmpty()) {
                Toast.makeText(context, "No products to upload", Toast.LENGTH_SHORT).show();

                return;
            }

            save.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);

            // Dialog is even less then fragment
            // It is small screen on fragment or activity (make background darker and is shown)
            Builder.DIALOG.areYouSureDialog(context, "Confirm upload", "Are you sure you want to upload items?", v13 -> cloud.addItem(itemsToInsert, new VolleyCallBack() {
                @Override
                public void onSuccess(Object... o) {
                    Log.d("saveDate", "save");

                    expirationTable.addAll(itemsToInsert);

                    itemsToInsert.clear();

                    progressBar.setVisibility(View.INVISIBLE);

                    fileManager.clearData();

                    addFragmentAdapter.notifyDataSetChanged();

                    Toast.makeText(context, "Products successfully uploaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Object... o) {
                    progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(context, "Products failed to upload", Toast.LENGTH_SHORT).show();
                }
            }), v14 -> {
                progressBar.setVisibility(View.INVISIBLE);
            }).show();
        });

        clear.setVisibility(View.GONE);
        clear.setOnClickListener(v -> {
            save.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);

            Builder.DIALOG.areYouSureDialog(context, "Confirm clearing", "Are you sure you want to clear all items?", v1 -> {
                itemsToInsert.clear();

                fileManager.clearData();

                addFragmentAdapter.notifyDataSetChanged();
            }, v12 -> {

            }).show();
        });

        addButton = view.findViewById(R.id.add_fragment_add_item_button);
        addButton.setOnClickListener(v -> {
            save.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);

            // Starts Barcode Activity and get data from barcode
            BarcodeActivity.getItem(context, expirationTable, true, new VolleyCallBack() {
                @Override
                public void onSuccess(Object... o) {
                    Log.d("AddFragment", o[0].toString());

                    addToItemsToInsert((NameItem) o[0]);
                }

                @Override
                public void onFailure(Object... o) {

                }
            });
        });
        addButton.setOnLongClickListener(v -> {
            save.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);

            return true;
        });

        // For RecyclerView items swiping events
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();

                switch (direction) {
                    case ItemTouchHelper.RIGHT:
                        addFragmentAdapter.notifyItemChanged(position);

                        Builder.DIALOG.areYouSureDialog(context, "Confirm delete", "Are you sure you want to delete this item?", v -> {
                            itemsToInsert.remove(position);

                            fileManager.saveData(itemsToInsert);

                            addFragmentAdapter.notifyItemRemoved(position);
                        }, v -> {

                        }).show();

                        break;
                    case ItemTouchHelper.LEFT:
                        AddFragmentViewHolder holder = (AddFragmentViewHolder) viewHolder;

                        String id = holder.itemID.getText().toString();
                        String name = holder.itemName.getText().toString();

                        Log.d("swipe", id + " " + position);

                        addFragmentAdapter.notifyItemChanged(position);

                        Builder.DIALOG.addItemDialog(context, id, name, null, new VolleyCallBack() {
                            @Override
                            public void onSuccess(Object... o) {
                                addToItemsToInsert((NameItem) o[0]);
                            }

                            @Override
                            public void onFailure(Object... o) {

                            }
                        }).show();

                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("Add")
                        .setSwipeLeftLabelColor(getResources().getColor(R.color.black, context.getTheme()))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_add_24)
                        .setSwipeLeftActionIconTint(getResources().getColor(R.color.black, context.getTheme()))
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.green, context.getTheme()))
                        .addSwipeRightLabel("Remove")
                        .setSwipeRightLabelColor(getResources().getColor(R.color.black, context.getTheme()))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                        .setSwipeRightActionIconTint(getResources().getColor(R.color.black, context.getTheme()))
                        .addSwipeRightBackgroundColor(getResources().getColor(R.color.red, context.getTheme()))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void addToItemsToInsert(NameItem nameItem) {
        if (Builder.DIALOG.addToItemsToInsert(nameItem, itemsToInsert)) {
            addFragmentAdapter.notifyItemInserted(0);
        }
        else {
            addFragmentAdapter.notifyItemChanged(0);
        }

        fileManager.saveData(itemsToInsert);

        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d("onViewCreated", "here");

        super.onViewCreated(view, savedInstanceState);
    }

}