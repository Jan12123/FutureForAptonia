package com.example.aptonia.cloud;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.aptonia.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebLoader {
    public static RequestQueue queue;
    public static String barcodeRFIDClass;
    public static String idQRClass;
    public static String notFoundClass;

    public static String testBarcodeRFIDClass;
    public static String testIdQRClass;
    public static String testNotFoundClass;

    public static void loadImageFromDecathlon(Context context, String id, ImageView imageView) {
        if (id.equals("")) {
            id = "1234567";
        }

        Log.d("id", id);

        new Loader(context).find(id).withResponse(new VolleyCallBack() {
            @Override
            public void onSuccess(Object... o) {
                String url = o[2].toString();

                Picasso.get().load(url).into(imageView);
            }

            @Override
            public void onFailure(Object... o) {
                imageView.setImageDrawable(null);
                imageView.setBackgroundColor(context.getResources().getColor(R.color.gray, context.getTheme()));

                this.onSuccess(o);
            }
        }).execute();
    }

    public static Loader using(Context context) {
        return new Loader(context);
    }

    public static class Loader {
        Context context;
        String nttCommand;
        VolleyCallBack callBack;

        public Loader(Context context) {
            this.context = context;
        }

        public Loader withResponse(VolleyCallBack callBack) {
            this.callBack = callBack;

            return this;
        }

        public Loader find(String nttCommand) {
            this.nttCommand = nttCommand;

            return this;
        }

        public void execute() {
            nttCommand = parseBarcode(nttCommand);

            if (nttCommand != null) {
                process();
            }
            else {
                callBack.onFailure("", "", "");
            }
        }

        private void process() {
            Log.d("execute", "https://www.decathlon.cz/search?Ntt=" + nttCommand);

            WebLoader.queue.add(new StringRequest("https://www.decathlon.cz/search?Ntt=" + nttCommand, response -> {
                Document document = Jsoup.parse(response);

                if (!document.getElementsByClass(notFoundClass).isEmpty()) {
                    Elements elements1 = document.getElementsByClass(barcodeRFIDClass);
                    Elements elements2 = document.getElementsByClass(idQRClass);

                    if (!elements1.isEmpty() || !elements2.isEmpty()) {
                        error("1");

                        return;
                    }

                    callBack.onFailure("", "", "https://prints.ultracoloringpages.com/9b4fb3c9b191bbd3a81b415c19efa857.png");

                    return;
                }

                Log.d("WebLoader-document", String.valueOf(document.getElementsByClass(barcodeRFIDClass)));

                Elements elements1 = document.getElementsByClass(barcodeRFIDClass);
                Elements elements2 = document.getElementsByClass(idQRClass);

                Log.d("barcodeRFIDClass", barcodeRFIDClass);
                Log.d("idQRClass", idQRClass);
                Log.d("notFoundClass", notFoundClass);

                Log.d("elements1", String.valueOf(elements1));
                Log.d("elements2", String.valueOf(elements2));

                if (!elements1.isEmpty()) {
                    String id_name = elements1.get(0).attr("href");

                    if (!id_name.contains("?mc=")) {
                        error("2");

                        return;
                    }

                    String id = id_name.split("\\?mc=")[1];
                    String name = id_name.split("/_/")[0].substring(3).replace("-", " ");
                    String img = elements1.get(0).child(0).attr("src");

                    Log.d("data1", id + "-" + name + "-" + img);

                    callBack.onSuccess(id, name, img);
                }
                else if (!elements2.isEmpty()) {
                    String id = nttCommand;

                    Log.d("id", id);

                    if (id.length() > 7) {
                        error("3");

                        return;
                    }

                    String name = elements2.get(0).attr("alt");
                    String img = elements2.get(0).attr("src");

                    Log.d("data2", id + "-" + name + "-" + img);

                    callBack.onSuccess(id, name, img);
                }
                else {
                    error("4");
                }
            }, callBack::onFailure));
        }

        private void error(String place) {
            Toast.makeText(context, "Error: " + place, Toast.LENGTH_SHORT).show();

            callBack.onFailure("", "", "https://prints.ultracoloringpages.com/9b4fb3c9b191bbd3a81b415c19efa857.png");
        }

        private static String parseQR(String qr) {
            if (!qr.contains("/qr/")) {
                return null;
            }

            return qr.split("/qr/")[1].split("/")[0];
        }

        public static String parseBarcode(String nttCommand) {
            if (parseQR(nttCommand) != null) {
                return parseQR(nttCommand);
            }

            if (!nttCommand.matches("[0-9()]+")) {
                return null;
            }

            if (nttCommand.length() > 13 && nttCommand.charAt(0) == '(') {
                return nttCommand.split("\\(")[1].substring(4);
            }
            else if (nttCommand.length() >= 6) {
                return nttCommand;
            }

            return null;
        }

    }

}
