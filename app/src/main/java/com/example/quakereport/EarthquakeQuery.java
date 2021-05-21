package com.example.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeQuery {

    private static final String LOG_TAG = "";

    private EarthquakeQuery() {
    }

    public static ArrayList<Earthquake> extractFeaturefromJson(String earthquakeJSON) {

        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            for(int i=0 ; i<earthquakeArray.length() ; i++){
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                Earthquake earthquake = new Earthquake(magnitude , place , time , url);
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {

            Log.e("EarthquakeQuery", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;

    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private  static String makeHTTpRequest(URL url) throws IOException{
        String JsonResponse= "";

        if(url ==null)
        {
            return JsonResponse;
        }
        HttpURLConnection urlConnection= null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return  JsonResponse;

    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream , Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while(line!=null){
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Earthquake> earthquakes = extractFeaturefromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return earthquakes;
    }

}
