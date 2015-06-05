package com.puigmusic.hramosdgil.android.Activities;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.puigmusic.hramosdgil.puigmusic.R;
import com.puigmusic.hramosdgil.android.tabs.MainActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LocalSongs extends ActionBarActivity {

    ListView lv;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> LocalList;
    private static final String TAG_TITLE = "title";
    private static final String TAG_URI = "uri";
    private ListView navList;
    Toolbar toolbar;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_song);
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
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        LocalList = new ArrayList<HashMap<String, String>>();
        new GetLocalSongs().execute();
    }

    private class GetLocalSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LocalSongs.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String path = Environment.getExternalStorageDirectory().toString()+"/Music/";
            File file = new File(path);
            File[] files = file.listFiles();
            for (File infile : files){
                HashMap<String, String> artistaItem = new HashMap<String, String>();
                Uri myUri = Uri.parse(path + infile.getName());
                artistaItem.put(TAG_URI, myUri.toString());
                String title = myUri.toString().replace(Environment.getExternalStorageDirectory().toString()+"/Music/","");
                title = title.replace(".mp3","");
                artistaItem.put(TAG_TITLE, title);
                LocalList.add(artistaItem);
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            lv = (ListView)findViewById(R.id.LocalSongs);

            ListAdapter adapter = new SimpleAdapter(LocalSongs.this, LocalList,
                    R.layout.local_item,
                    new String[]{TAG_TITLE}, new int[]{
                    R.id.artist});
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent in = new Intent(getApplicationContext(),PlayMusic.class);
                    in.putExtra("list", LocalList);
                    in.putExtra("position",position);
                    startActivity(in);
                }
            });
        }
    }
    // Change Activity in Navigation Drawer
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
