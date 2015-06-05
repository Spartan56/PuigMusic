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

import com.puigmusic.hramosdgil.android.ServiceHandler;
import com.puigmusic.hramosdgil.android.Activities.GenreSongs;
import com.puigmusic.hramosdgil.puigmusic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Genres extends Fragment {
    ListView lv;
    private ProgressDialog pDialog;
    // URL to get information JSON
    private static String url = "http://puigmusic-prueba121.rhcloud.com/rest/genres";
    private static final String TAG_GENRE = "genre";
    private static final String TAG_SHOW_GENRE = "show_genre";
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> listGenres;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.genres, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listGenres = new ArrayList<HashMap<String, String>>();
        new GetSongs().execute();


    }
    private class GetSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET,null);

            if (jsonStr != null) try {
                JSONObject artistas = new JSONObject(jsonStr);
                Iterator<String> iter = artistas.keys();

                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        HashMap<String, String> artistaItem = new HashMap<String, String>();
                        String value = artistas.get(key).toString();
                        String genre = key.replace("_"," ");
                        artistaItem.put(TAG_GENRE, key);
                        artistaItem.put(TAG_SHOW_GENRE,genre);
                        listGenres.add(artistaItem);
                    } catch (JSONException e) {
                    }
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
             else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            lv = (ListView)getView().findViewById(R.id.list);

            ListAdapter adapter = new SimpleAdapter(getActivity(), listGenres,
                    R.layout.genres_view_item,
                    new String[]{TAG_SHOW_GENRE}, new int[]{
                    R.id.genre});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent in = new Intent(getActivity(),GenreSongs.class);
                    in.putExtra(TAG_GENRE, listGenres.get(position).get(TAG_GENRE).toString());
                    startActivity(in);
                }
            });
        }
    }
}
