package com.example.civiladvocacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class GoogleCivicApiRunnable implements Runnable {

    private static final String TAG = "GoogleCivicApiRunnable";

    private final MainActivity mainActivity;
    private final String addr;

    private static final String civicURL = "https://www.googleapis.com/civicinfo/v2/representatives";
    private static final String yourAPIKey = "AIzaSyDRP8nwHFki-Cu3re500Nef5aNyTFQKkr0";


    GoogleCivicApiRunnable(MainActivity mainActivity, String addr) {
        this.mainActivity = mainActivity;
        this.addr = addr;
    }


    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(civicURL).buildUpon();

        buildURL.appendQueryParameter("key", yourAPIKey);
        buildURL.appendQueryParameter("address", addr);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleResults(final String jsonString) {
        final String addr = parseNormalizedJSON(jsonString);
        final List<Official> oList = parseOfficialJSON(jsonString);
        mainActivity.runOnUiThread(() -> mainActivity.updateCivicData(addr, oList));
    }

    private String parseNormalizedJSON(String s) {
        String addr = "";

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "normalizedInput" section
            JSONObject normalized = jObjMain.getJSONObject("normalizedInput");
            addr += normalized.getString("line1");
            addr += ", " + normalized.getString("city");
            addr += ", " + normalized.getString("state");
            addr += " " + normalized.getString("zip");

            return addr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Official> parseOfficialJSON(String s) {
        List<Official> list = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "normalizedInput" section
            JSONObject normalized = jObjMain.getJSONObject("normalizedInput");

            // "offices" section
            JSONArray offices = jObjMain.getJSONArray("offices");

            // "officials" section
            JSONArray officials = jObjMain.getJSONArray("officials");

            // Data processing
            for(int i=0; i<offices.length(); i++) {
                JSONObject officesObject = offices.getJSONObject(i);
                JSONArray officialIndices = officesObject.getJSONArray("officialIndices");

                for(int j=0; j<officialIndices.length(); j++) {
                    Integer officialIndex = officialIndices.getInt(j);
                    JSONObject officialObject = officials.getJSONObject(officialIndex);

                    String officeName = officesObject.getString("name");
                    String officialName = officialObject.getString("name");
                    String officialAddr = "";
                    String officialParty = "Unknown";
                    String officialPhone = null;
                    String officialUrl = null;
                    String officialEmail = null;
                    String officialPhoto = null;
                    String officialYt = null;
                    String officialFb = null;
                    String officialTw = null;

                    // Address Section
                    if (officialObject.has("address")) {
                        JSONArray addrArr = officialObject.getJSONArray("address");
                        if (addrArr.length() > 0) {
                            JSONObject addrObject = addrArr.getJSONObject(0);
                            Integer addrIndex = 1;
                            while (addrObject.has("line" + addrIndex.toString())) {
                                officialAddr += addrObject.getString("line" + addrIndex.toString());
                                addrIndex += 1;
                            }
                            officialAddr += ", " + addrObject.getString("city");
                            officialAddr += ", " + addrObject.getString("state");
                            officialAddr += " " + addrObject.getString("zip");
                        }
                    }

                    // Party Section
                    if (officialObject.has("party")) {
                        officialParty = officialObject.getString("party");
                    }

                    // Phone Section
                    if (officialObject.has("phones")) {
                        officialPhone = officialObject.getJSONArray("phones").getString(0);
                    }

                    // Url Section
                    if (officialObject.has("urls")) {
                        officialUrl = officialObject.getJSONArray("urls").getString(0);
                    }

                    // Email Section
                    if (officialObject.has("emails")) {
                        officialEmail = officialObject.getJSONArray("emails").getString(0);
                    }

                    // Photo Section
                    if (officialObject.has("photoUrl")) {
                        officialPhoto = officialObject.getString("photoUrl");
                    }

                    // Channels Section
                    if (officialObject.has("channels")) {
                        JSONArray channels = officialObject.getJSONArray("channels");
                        for(int k=0; k<channels.length(); k++) {
                            JSONObject channel = channels.getJSONObject(k);
                            String type = channel.getString("type");
                            String id = channel.getString("id");

                            if (type.equals("Facebook")) {
                                officialFb = id;
                            } else if (type.equals("Youtube")) {
                                officialYt = id;
                            } else if (type.equals("Twitter")) {
                                officialTw = id;
                            }
                        }
                    }

                    Official officialObj = new Official(officeName, officialName, officialAddr, officialParty,
                            officialPhone, officialUrl, officialEmail, officialPhoto,
                            officialYt, officialFb, officialTw);
                    list.add(officialObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
