package com.example.night_friend.main_map;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        try{
            String url = "https://apis.openapi.sk.com/tmap/jsv2?version=1&appKey=l7xx95f2e1d70e6a430484c5f00181f5ea93";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");

            byte[] outputInBytes = params[0].getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write( outputInBytes );
            os.close();

            int retCode = conn.getResponseCode();

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = br.readLine()) != null) {
                response.append(line);
                response.append(' ');
            }
            br.close();

            String res = response.toString();
            Log.d("LOG","결과: "+res);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
