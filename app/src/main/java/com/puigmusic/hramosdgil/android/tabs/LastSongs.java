package com.puigmusic.hramosdgil.android.tabs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.puigmusic.hramosdgil.android.Activities.PlayMusic;
import com.puigmusic.hramosdgil.android.ServiceHandler;
import com.puigmusic.hramosdgil.puigmusic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class LastSongs extends Fragment {
    ListView lv;
    private ProgressDialog pDialog;

    // URL to get information JSON
    private static String url = "http://puigmusic-prueba121.rhcloud.com/rest/songs";

    private static final String TAG_TITLE = "title";
    private static final String TAG_ARTIST = "artist";
    private static final String TAG_GENRE = "genre";
    private static final String TAG_PATH = "path";


    // Hashmap for ListView
    ArrayList<HashMap<String, String>> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.last_songs, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        songList = new ArrayList<HashMap<String, String>>();
        new GetSongs().execute();


    }

    private class GetSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET,null);

            if (jsonStr != null) {
                try {
                    JSONArray canciones = new JSONArray(jsonStr);
                    for (int i = 0; i < canciones.length(); i++) {
                        JSONObject cancion = (JSONObject) canciones.get(i);
                        String title = cancion.getString(TAG_TITLE);
                        String artist = cancion.getString(TAG_ARTIST).replaceAll("_", " ");
                        String genre = cancion.getString(TAG_GENRE).replace("_"," ");
                        String path = cancion.getString(TAG_PATH);
                        Log.i("App","Response : Path ->>"+cancion.getString("path"));
                        HashMap<String, String> cancionItem = new HashMap<String, String>();
                        cancionItem.put(TAG_TITLE, title);
                        cancionItem.put(TAG_ARTIST, artist);
                        cancionItem.put(TAG_GENRE, genre);
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
            lv = (ListView)getView().findViewById(R.id.listsongs);

            ListAdapter adapter = new SimpleAdapter(getActivity(), songList,
                    R.layout.last_songs_view_item,
                    new String[]{TAG_TITLE, TAG_ARTIST, TAG_GENRE}, new int[]{
                    R.id.title, R.id.artist, R.id.genre,});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent in = new Intent(getActivity(),PlayMusic.class);
                    in.putExtra("list", songList);
                    in.putExtra("position",position);
                    startActivity(in);

                }
            });
        }
    }
}
