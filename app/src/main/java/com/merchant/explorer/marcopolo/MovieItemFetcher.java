package com.merchant.explorer.marcopolo;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieItemFetcher {

    private static final String TAG = "MovieItemFetcher";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<MovieItem> fetchItems() {

        List<MovieItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://itunes.apple.com/search?term=The+Lion+King&media=movie&limit=5")
                    .buildUpon()
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private void parseItems(List<MovieItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONArray productsJsonArray = jsonBody.getJSONArray("results");
        for (int i = 0; i < productsJsonArray.length(); i++) {
            JSONObject productJsonObject = productsJsonArray.getJSONObject(i);

            String name = productJsonObject.getString("trackName");
            String date = productJsonObject.getString("releaseDate");
            String url = productJsonObject.getString("artworkUrl100");

            String desc = "";

            if (productJsonObject.has("shortDescription")) {
                desc = productJsonObject.getString("shortDescription");
            } else {
                // The movie is missing a short description
                desc = productJsonObject.getString("longDescription");
            }

            MovieItem item = new MovieItem();

            item.setTrackName(name);
            item.setReleaseDate(date);
            item.setArtworkUrl100(url);
            item.setShortDescription(desc);


            items.add(item);
        }
    }
}
