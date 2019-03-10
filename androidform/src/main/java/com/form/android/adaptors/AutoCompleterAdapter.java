package com.form.android.adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Class to suggest the predictions on the basis of entered text.
 */
public class AutoCompleterAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> resultList;
    private String autoCompleterURL;

    /**
     * Public constructor to initiate the auto-completer
     *
     * @param context            Context of calling class.
     * @param textViewResourceId Resource id of the text view.
     * @param url                Url for auto suggestion
     */
    public AutoCompleterAdapter(Context context, int textViewResourceId, String url) {
        super(context, textViewResourceId);
        this.autoCompleterURL = url;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autoCompleter(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Method to get result list of the suggestions.
     *
     * @param input Entered text by the user.
     * @return Returns the list of items on the basis of input.
     */
    private ArrayList<String> autoCompleter(String input) {

        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            autoCompleterURL = autoCompleterURL + "&input=" + URLEncoder.encode(input, "utf8");
            URL url = new URL(autoCompleterURL);

            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());

            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());
            resultList = new ArrayList<>(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++) {

                JSONObject list = predsJsonArray.optJSONObject(i);
                String value = list.optString("description");
                resultList.add(value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}

