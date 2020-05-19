package com.ganshujin.androidrealtimelocation;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class fetchData extends AsyncTask<Void,Void,Void> {
    String data ="";
    String dataParsed = "";
    String singleParsed ="";

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://api.covid19api.com/live/country/malaysia");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line !=null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray JA = new JSONArray(data);
            for (int i=(JA.length()-1); i <JA.length(); i++){
                JSONObject JO = (JSONObject) JA.get(i);
                singleParsed =  "TOTAL CASES: " + JO.get("Confirmed") + "\n"+"\n"+
                        "DEATHS: " + JO.get("Deaths") + "\n"+"\n"+
                        "TOTAL RECOVERED: " + JO.get("Recovered") + "\n"+"\n";

                dataParsed = dataParsed + singleParsed;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        UpdateActivity.data.setText(this.dataParsed);
    }
}
