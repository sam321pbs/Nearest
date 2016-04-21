package com.example.sammengistu.nearest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class AddressJSONSerializer {
    private Context mContext;
    private String mFileName;

    public AddressJSONSerializer(Context c, String f){
        mContext = c;
        mFileName = f;
    }

    public void saveAddress(List<Address> goals)
            throws JSONException, IOException {
        //Build an array in JSON
        JSONArray array = new JSONArray();
        for (Address t : goals){
            array.put(t.toJSON());

        }
        //Write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext
                    .openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public List<Address> loadAddresses() throws IOException, JSONException {
        List<Address> goals = new ArrayList<Address>();
        BufferedReader reader = null;
        try {
            //Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            //Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //Build the array of crimes from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                goals.add(new Address(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            //Ignore
        } finally {
            if (reader != null)
                reader.close();
        }
        return goals;
    }
}

