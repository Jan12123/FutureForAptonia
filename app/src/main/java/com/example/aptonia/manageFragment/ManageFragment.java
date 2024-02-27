package com.example.aptonia.manageFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.BarcodeActivity;
import com.example.aptonia.MainActivity;
import com.example.aptonia.R;
import com.example.aptonia.builder.Builder;
import com.example.aptonia.cloud.Cloud;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.Item;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageFragment extends Fragment {

    Cloud cloud;
    ExpirationTable expirationTable;
    Context context;

    RecyclerView recyclerView;
    ManageFragmentAdapter manageFragmentAdapter;
    LinearLayoutManager linearLayoutManager;

    // Shows all items names and its IDs
    public ManageFragment(Cloud cloud, ExpirationTable expirationTable) {
        this.cloud = cloud;
        this.expirationTable = expirationTable;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);

        manageFragmentAdapter = new ManageFragmentAdapter(view.getContext(), expirationTable);
        linearLayoutManager = new LinearLayoutManager(view.getContext());

        recyclerView = view.findViewById(R.id.manage_fragment_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(manageFragmentAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText searchProduct = view.findViewById(R.id.manage_fragment_search_item_text_input_edit_text);

        searchProduct.setOnClickListener(view1 -> {
            TextInputEditText editText = (TextInputEditText) view1;

            editText.setFocusableInTouchMode(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(true);
            editText.setFocusable(true);

            MainActivity.hideKeyboard(context, view);
        });

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                manageFragmentAdapter.filterList(charSequence.toString());

                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
