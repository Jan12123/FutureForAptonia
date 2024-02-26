package com.example.aptonia.builder;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptonia.BarcodeActivity;
import com.example.aptonia.MainActivity;
import com.example.aptonia.R;
import com.example.aptonia.addFragment.AddFragmentAdapter;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.cloud.WebLoader;
import com.example.aptonia.expirationTable.DateItem;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.example.aptonia.expirationTable.NameItem;
import com.example.aptonia.storage.FileManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Builder {

    public static class DIALOG {

        public static Dialog areYouSureDialog(Context context, String titleText, String messageText, View.OnClickListener confirmOperation, View.OnClickListener cancelOperation) {
            Dialog sureDialog = new Dialog(context);
            sureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            sureDialog.setCancelable(true);
            sureDialog.setContentView(R.layout.are_you_sure_dialog);
            sureDialog.getWindow().setBackgroundDrawableResource(R.drawable.select_range_dialog_background);

            TextView title = sureDialog.findViewById(R.id.sure_dialog_title);
            title.setText(titleText);

            TextView message = sureDialog.findViewById(R.id.sure_dialog_message);
            message.setText(messageText);

            Button cancel = sureDialog.findViewById(R.id.sure_dialog_cancel);
            cancel.setOnClickListener(v -> {
                cancelOperation.onClick(v);

                sureDialog.dismiss();
            });

            Button confirm = sureDialog.findViewById(R.id.sure_dialog_confirm);
            confirm.setOnClickListener(v -> {
                confirmOperation.onClick(v);

                sureDialog.dismiss();
            });

            return sureDialog;
        }

        public static Dialog addItemDialog(Context context, String idText, String name, String dateText, VolleyCallBack callBack) {
            Log.d("BUilder-additemDialog", "here");

            Dialog addItemDialog = new Dialog(context);
            addItemDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addItemDialog.setCancelable(false);
            addItemDialog.setContentView(R.layout.add_fragment_add_item_dialog);
            addItemDialog.getWindow().setBackgroundDrawableResource(R.drawable.select_range_dialog_background);

            ImageView image = addItemDialog.findViewById(R.id.add_fragment_add_item_dialog_item_image);

            TextView itemName = addItemDialog.findViewById(R.id.add_fragment_add_item_dialog_item_name);
            TextView itemID = addItemDialog.findViewById(R.id.add_fragment_add_item_dialog_item_id);

            TextInputEditText date = addItemDialog.findViewById(R.id.add_fragment_add_item_dialog_select_date_edit_text);

            if (idText != null) {
                itemID.setText(idText);

                WebLoader.loadImageFromDecathlon(context, idText, image);
            }

            if (idText != null) {
                itemName.setText(name);
            }

            if (dateText != null) {
                date.setText(dateText);
            }

            date.addTextChangedListener(new TextWatcher() {
                private String current = "";
                private final Calendar calendar = Calendar.getInstance();

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().equals(current)) {
                        int nowYear = DateTime.now().getYear();
                        int nowMonth = DateTime.now().getMonthOfYear();
                        int nowDay = DateTime.now().getDayOfMonth();

                        String clean = s.toString().replaceAll("[^\\d.]", "");
                        String cleanC = current.replaceAll("[^\\d.]", "");

                        int sel = clean.length();

                        for (int i = 2; i <= clean.length() && i < 6; i += 2) {
                            sel++;
                        }

                        if (clean.equals(cleanC)) {
                            sel--;
                        }

                        if (clean.length() < 8){
                            clean = clean + "ddmmyyyy".substring(clean.length());
                        }
                        else {
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));

                            day = (day == 0) ? 1 : day;

                            mon = (mon == 0) ? 1 : mon;

                            mon = Math.min(mon, 12);

                            calendar.set(Calendar.MONTH, mon - 1);

                            year = (year < nowYear) ? nowYear : Math.min(year, 2100);

                            calendar.set(Calendar.YEAR, year);

                            day = Math.min(day, calendar.getActualMaximum(Calendar.DATE));
                            clean = String.format(Locale.GERMANY, "%02d%02d%02d",day, mon, year);
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2), clean.substring(2, 4), clean.substring(4, 8));

                        sel = Math.max(sel, 0);

                        current = clean;

                        date.setText(current);
                        date.setSelection(Math.min(sel, current.length()));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            date.setOnClickListener(v13 -> {
                date.setFocusableInTouchMode(false);
                date.setFocusable(false);
                date.setFocusableInTouchMode(true);
                date.setFocusable(true);

                MainActivity.hideKeyboard(context, v13);
            });

            Button cancel = addItemDialog.findViewById(R.id.add_fragment_add_item_dialog_cancel_button);
            cancel.setOnClickListener(v1 -> {
                callBack.onFailure();

                addItemDialog.dismiss();
            });

            Button confirm = addItemDialog.findViewById(R.id.add_fragment_add_item_dialog_confirm_button);
            confirm.setOnClickListener(v12 -> {
                if (!controlDialog(itemName, itemID.getText().toString(), date)) {
                    return;
                }

                NameItem nameItem = new NameItem(itemName.getText().toString(), itemID.getText().toString());

                String[] tmpDate = date.getText().toString().split("/");

                nameItem.getDateItems().add(new DateItem(nameItem.getName(),
                        nameItem.getID(),
                        String.valueOf(Integer.parseInt(tmpDate[0])),
                        String.valueOf(Integer.parseInt(tmpDate[1])),
                        String.valueOf(Integer.parseInt(tmpDate[2]))));

                Log.d("AddFragment", nameItem.toString());

                callBack.onSuccess(nameItem);

                addItemDialog.dismiss();
            });

            return addItemDialog;
        }

        public static boolean addToItemsToInsert(NameItem nameItem, List<NameItem> itemsToInsert) {
            for (NameItem tmpNameItem : itemsToInsert) {
                if (tmpNameItem.getID().equals(nameItem.getID())) {
                    DateItem itemToInsert = nameItem.getDateItems().get(0);

                    for (DateItem dateItem : tmpNameItem.getDateItems()) {
                        if (dateItem.getYear().equals(itemToInsert.getYear()) && dateItem.getMonthNumber().equals(itemToInsert.getMonthNumber()) && dateItem.getDayNumber().equals(itemToInsert.getDayNumber())) {
                            return false;
                        }
                    }

                    tmpNameItem.getDateItems().add(itemToInsert);

                    return false;
                }
            }

            itemsToInsert.add(0, nameItem);

            return true;
        }

        private static boolean controlDialog(TextView name, String id, TextInputEditText date) {
            String tmpDate = date.getText().toString();

            String dateRegex = "\\d\\d/\\d\\d/\\d\\d\\d\\d";

            if (!tmpDate.matches(dateRegex)) {
                return false;
            }

            if (name.getText().toString().length() == 0) {
                return false;
            }

            if (id.length() > 7 || id.length() < 6) {
                return false;
            }

            return true;
        }

        public static Dialog removeItemDialog(Context context, String name, String id, String date, View.OnClickListener removeClick, View.OnClickListener cancelClick) {
            Dialog removeDialog = new Dialog(context);
            removeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            removeDialog.setCancelable(true);
            removeDialog.setContentView(R.layout.schedult_fragment_remove_item_dialog);
            removeDialog.getWindow().setBackgroundDrawableResource(R.drawable.select_range_dialog_background);

            WebLoader.loadImageFromDecathlon(context, id, removeDialog.findViewById(R.id.schedule_fragment_remove_item_dialog_image_view));

            ((TextView) removeDialog.findViewById(R.id.schedule_fragment_remove_item_dialog_name)).setText(name);
            ((TextView) removeDialog.findViewById(R.id.schedule_fragment_remove_item_dialog_id)).setText(id);
            ((TextView) removeDialog.findViewById(R.id.schedule_fragment_remove_item_dialog_date)).setText(date);

            removeDialog.findViewById(R.id.schedule_fragment_remove_item_dialog_remove_button).setOnClickListener(v -> {
                removeDialog.dismiss();

                removeClick.onClick(v);
            });

            removeDialog.findViewById(R.id.schedule_fragment_remove_item_dialog_cancel_button).setOnClickListener(v -> {
                removeDialog.dismiss();

                cancelClick.onClick(v);
            });

            return removeDialog;
        }

        /*public static class ProgressDialog extends Dialog {

            TextView textProgress;

            ProgressBar progressBar;

            ProgressBarStatus status;

            public ProgressDialog(@NonNull Context context, ProgressBarStatus status) {
                super(context);

                this.status = status;
            }

            public void add(int count) {
                status.setCurrent(status.getCurrent() + count);

                textProgress.setText(status.getCurrent() + " of " + status.getMax());

                setProgressAnimate(progressBar, status.getCurrent());
            }

            private void setProgressAnimate(ProgressBar progressBar, int progressTo) {
                ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progressTo)
                        .setDuration(250)
                        .start();
            }

            public void prepare() {
                progressBar.setMax(status.getMax());
                progressBar.setProgress(status.getCurrent());

                textProgress.setText(status.getCurrent() + " of " + status.getMax());
            }

            public void endDialog() {
                textProgress.setText(status.getMax() + " of " + status.getMax());

                setProgressAnimate(progressBar, status.getMax());

                new Handler().postDelayed(this::dismiss, 300);
            }
        }*/

    }

}