package com.puigmusic.hramosdgil.android.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.puigmusic.hramosdgil.android.ServiceHandler;
import com.puigmusic.hramosdgil.android.tabs.MainActivity;
import com.puigmusic.hramosdgil.puigmusic.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class GenreSongs extends ActionBarActivity {
    ListView lv;
    private ListView navList;
    private ProgressDialog pDialog;
    // URL to get information JSON
    private static String url = "http://puigmusic-prueba121.rhcloud.com/rest/genre/";
    private static final String TAG_TITLE = "title";
    private static final String TAG_ARTIST = "artist";
    private static final String TAG_GENRE = "genre";
    private static final String TAG_PATH = "path";
    private String finalUrl;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> songList;
    Intent i;
    public GenreSongs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre);
        Bundle args = getIntent().getExtras();
        String genre= args.getString(TAG_GENRE).replace("_"," ");
        setTitle(genre);
        finalUrl = url+args.getString(TAG_GENRE);
        songList = new ArrayList<HashMap<String, String>>();

        final String[] names = getResources().getStringArray(
                R.array.nav_options);
        this.navList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, names));
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                ChangeActivity(arg2);
                drawerLayout.closeDrawers();
            }
        });

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        new GetSongs().execute();


    }

    private class GetSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GenreSongs.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(finalUrl, ServiceHandler.GET,null);

            if (jsonStr != null) {
                try {
                    JSONArray canciones = new JSONArray(jsonStr);
                    for (int i = 0; i < canciones.length(); i++) {
                        JSONObject cancion = (JSONObject) canciones.get(i);
                        String title = cancion.getString(TAG_TITLE);
                        String artist = cancion.getString(TAG_ARTIST).replaceAll("_", " ");
                        String path = cancion.getString(TAG_PATH);
                        Log.i("App", "Response : Path ->>" + cancion.getString("path"));
                        HashMap<String, String> cancionItem = new HashMap<String, String>();
                        cancionItem.put(TAG_TITLE, title);
                        cancionItem.put(TAG_ARTIST, artist);
                        cancionItem.put(TAG_PATH,path);
                        songList.add(cancionItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            lv = (ListView)findViewById(R.id.GenreListSong);

            ListAdapter adapter = new SimpleAdapter(getApplication(), songList,
                    R.layout.genre_songs_item,
                    new String[]{TAG_TITLE, TAG_ARTIST}, new int[]{
                    R.id.title, R.id.artist});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent in = new Intent(getApplicationContext(),PlayMusic.class);
                    in.putExtra("list", songList);
                    in.putExtra("position",position);
                    startActivity(in);

                }
            });
        }
    }
    public void ChangeActivity(int args) {
        Intent i;
        switch (args) {
            case 0:
                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(getApplicationContext(), DownloadActivity.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(getApplicationContext(), LocalSongs.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}

