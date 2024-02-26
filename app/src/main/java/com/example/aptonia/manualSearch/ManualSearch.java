package com.example.aptonia.manualSearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.aptonia.BarcodeActivity;
import com.example.aptonia.R;
import com.example.aptonia.cloud.VolleyCallBack;
import com.example.aptonia.expirationTable.ExpirationTable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class ManualSearch extends AppCompatActivity {

    private static Context context;
    private static String barcode;
    private static ExpirationTable expirationTable;
    private static VolleyCallBack callBack;

    private TextView barcodeTitle;
    private ImageView barcodeView;
    private TextInputEditText idEditText;
    private TextInputEditText nameEditText;
    private Button cancel;
    private Button confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);

        barcodeTitle = this.findViewById(R.id.manual_search_barcode_title);
        barcodeView = this.findViewById(R.id.manual_search_barcode_view);
        idEditText = this.findViewById(R.id.manual_search_id_edit_text);
        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = expirationTable.findName(s.toString());

                if (name != null) {
                    nameEditText.setText(name);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nameEditText = this.findViewById(R.id.manual_search_name_edit_text);
        confirm = this.findViewById(R.id.manual_search_confirm);
        confirm.setOnClickListener(v1 -> {
            String id = idEditText.getText().toString();
            String name = nameEditText.getText().toString();

            if (id.length() != 6 && id.length() != 7) {
                return;
            }

            if (name.equals("")) {
                return;
            }

            end(id, name, "https://prints.ultracoloringpages.com/9b4fb3c9b191bbd3a81b415c19efa857.png");
        });

        cancel = this.findViewById(R.id.manual_search_cancel);
        cancel.setOnClickListener(v -> {
            end();
        });

        Log.d("manual search", barcode);

        if (barcode.equals("")) {
            barcodeTitle.setVisibility(View.GONE);

            barcodeView.setVisibility(View.GONE);

            return;
        }

        try {
            if (barcode.contains("qr")) {
                barcodeView.setImageBitmap(encodeAsBitmap(barcode, BarcodeFormat.QR_CODE, 400, 400));
            }
            else {
                barcodeView.setImageBitmap(encodeAsBitmap(barcode, BarcodeFormat.CODE_128, 800, 100));
            }
        } catch (WriterException e) {
            e.printStackTrace();

            barcodeView.setBackgroundColor(getColor(R.color.gray));
        }
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        if (contents == null) {
            return null;
        }

        Map<EncodeHintType, Object> hints = null;

        String encoding = guessAppropriateEncoding(contents);

        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }

        MultiFormatWriter writer = new MultiFormatWriter();

        BitMatrix result;

        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();

        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }

        return null;
    }

    private void end(Object... o) {
        if (o.length == 0) {
            callBack.onFailure();
        }
        else {
            callBack.onSuccess(o);
        }

        Log.d("endManualSearch", Arrays.toString(o));

        finish();

        Animatoo.INSTANCE.animateZoom(ManualSearch.this);
    }

    public static void getItem(Context context, String barcode, ExpirationTable expirationTable, VolleyCallBack volleyCallBack) {
        Intent intent = new Intent(context, ManualSearch.class);

        ManualSearch.context = context;
        ManualSearch.barcode = barcode;
        ManualSearch.expirationTable = expirationTable;
        ManualSearch.callBack = volleyCallBack;

        context.startActivity(intent);

        Animatoo.INSTANCE.animateZoom(context);
    }

}
