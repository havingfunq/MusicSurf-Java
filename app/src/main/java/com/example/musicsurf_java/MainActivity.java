package com.example.musicsurf_java;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODOs Are Every Where!
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: findViewById is deprecated: https://medium.com/androiddevelopers/use-view-binding-to-replace-findviewbyid-c83942471fc
        ListView listView = findViewById(R.id.listview);
        TextView textView = findViewById(R.id.textview);

        String[] initial_artists = new String[] {"Madonna", "Sonny & Cher", "Ella Fitzgerald"};

        // TODO: Put Adapter In A Helper Method?
        List<String> Artists_list = new ArrayList<String>(Arrays.asList(initial_artists));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Artists_list);
        listView.setAdapter(arrayAdapter);

        // TODO: refactor pyramid of doom, abstract into helper methods and classes
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);

                textView.setText("Artists Related To: " + selectedItem);

                String[] artists = new String[10];

                // creates a worker thread from ui thread
                // is this deprecated?
                new Thread(
                        new Runnable(){
                            public void run(){
                                try{
                                    URL url = new URL("https://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist=" + selectedItem + "&api_key=f36c744c788a84bfe032282f79979e7a&format=json&limit=10");

                                    // TODO: Put Into Helper Method
                                    URLConnection connection = url.openConnection();

                                    BufferedReader in = new BufferedReader(new InputStreamReader(
                                            connection.getInputStream()));

                                    String inputLine ="";
                                    String outputLine ="";

                                    while ((inputLine = in.readLine()) != null) {
                                        outputLine = outputLine + inputLine;
                                    }

                                    // TODO: Put JSON Logic Into A Helper Function
                                    JSONObject reader = new JSONObject(outputLine);

                                    JSONObject similar  = reader.getJSONObject("similarartists");
                                    JSONArray arrayOfArtists = similar.getJSONArray("artist");

                                    in.close();

                                    for (int i = 0; i < arrayOfArtists.length(); i++) {
                                        JSONObject current = arrayOfArtists.getJSONObject(i);
                                        String name = current.getString("name");

                                        artists[i] = name;
                                    }

                                    /* https://stackoverflow.com/questions/11140285/how-do-we-use-runonuithread-in-android */
                                    runOnUiThread(new Runnable(){
                                        public void run(){
                                            List<String> artists_list = new ArrayList<String>(Arrays.asList(artists));
                                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, artists_list);
                                            listView.setAdapter(arrayAdapter);
                                        }
                                    });

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).start();
            }
        });
    }
}