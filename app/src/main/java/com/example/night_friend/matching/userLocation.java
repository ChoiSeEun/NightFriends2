package com.example.night_friend.matching;

import android.os.AsyncTask;
import android.util.Log;

import com.example.night_friend.main_map.GpsInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class userLocation {
    private String urlPath;

    private GpsInfo gps;
    private boolean isPermission = false;

    // 회원 가입의 정보를 MySQL에 저장할 php를 포함한 도메인 주소를 입력한다.

    private final String signup_user_information_UrlPath ="http://night1234.dothome.co.kr/userLocation.php";

    /*-- DB user에 접속하여 회원 가입에 관한 db 저장할 데이터 */

    private String user;

    private double userLat, userLon;
    private double destLat, destLon;

    private int userAns;

    ArrayList<String> results;
    public ArrayList<String> user_Map (String url,String user, double userLat, double userLon, double destLat, double destLon) {

        urlPath = url;

        this.user = user;

        this.userLat = userLat;

        this.userLon = userLon;

        this.destLat = destLat ;

        this.destLon = destLon;

        try {

            results = new SignupUserInformation().execute().get();

        } catch ( InterruptedException e ) {

            e.printStackTrace();

        } catch ( ExecutionException e ) {

            e.printStackTrace();

        }

        return results;

    }

    public ArrayList<String> user_Map (String url, String user, int userAns) {

        urlPath = url;

        this.user = user;

        this.userLat = 0;

        this.userLon = 0;

        this.destLat = 0 ;

        this.destLon = 0;

        this.userAns = userAns;


        try {

            results = new SignupUserInformation().execute().get();

        } catch ( InterruptedException e ) {

            e.printStackTrace();

        } catch ( ExecutionException e ) {

            e.printStackTrace();

        }

        return results;

    }



    /* -- 문자열로 이루어진 데이터를 서버에 POST 방식으로 전송한다 */

    class SignupUserInformation extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override

        protected ArrayList<String> doInBackground(Void... voids) {

            // TODO Auto-generated method

            try {

                URL url = new URL(urlPath); // Set url

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setDoInput(true); // Available Write

                con.setDoOutput(true); // Available Read

                con.setUseCaches(false); // No cash

                con.setRequestMethod("POST");


                String param = "&user=" + user + "&userLat=" + userLat + "&userLon=" + userLon +
                        "&destLat=" + destLat + "&destLon=" + destLon + "&userAns=" + userAns; // &user에서 오타가 있었음.


                OutputStream outputStream = con.getOutputStream();

                outputStream.write(param.getBytes());

                outputStream.flush();

                outputStream.close();


                BufferedReader rd = null;

                rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                String line = null;

                while ((line = rd.readLine()) != null) {

                    Log.d("BufferedReader:", line);

                }

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;

        }

        protected void onPostExecute(ArrayList<String> qResults) {

            super.onPostExecute(qResults);

        }
    }


}