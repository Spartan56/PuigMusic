package com.puigmusic.hramosdgil.android.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;


class Download extends AsyncTask<String, String, String> {
    ProgressDialog prgDialog;

    public DownloadActivity down;
    int position;
    ArrayList<HashMap<String, String>> songList;
    public Download(DownloadActivity down, ArrayList<HashMap<String, String>> songList, int position) {
        this.down=down;
        this.position= position;
        this.songList= songList;
    }
    // Show Progress bar before downloading Music
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        prgDialog = new ProgressDialog(down);
        prgDialog.setMessage("Downloading Mp3 file. Please wait...");
        prgDialog.setIndeterminate(false);
        prgDialog.setMax(100);
        prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        prgDialog.setCancelable(false);
        prgDialog.show();
    }

    // Download Music File from Internet
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // Get Music file length
            int lenghtOfFile = conection.getContentLength();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(),10*1024);
            // Output stream to write file in SD card
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+songList.get(position).get("path"));
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // Publish the progress which triggers onProgressUpdate method
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // Write data to file
                output.write(data, 0, count);
            }
            // Flush output
            output.flush();
            // Close streams
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    // While Downloading Music File
    protected void onProgressUpdate(String... progress) {
        // Set progress percentage
        prgDialog.setProgress(Integer.parseInt(progress[0]));
    }

    // Once Music File is downloaded
    @Override
    protected void onPostExecute(String file_url) {
        // Dismiss the dialog after the Music file was downloaded
        prgDialog.dismiss();
        Toast.makeText(down.getApplicationContext(), "Download complete", Toast.LENGTH_LONG).show();
    }
}
