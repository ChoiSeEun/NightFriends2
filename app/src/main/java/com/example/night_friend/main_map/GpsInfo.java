package com.example.night_friend.main_map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

import static com.example.night_friend.main_map.Fragment1.REQUEST_CODE_PERMISSIONS;

public class GpsInfo {

    private final Context mContext;
    private boolean isPermission = false;

    boolean isGpsEnabled = false;
    boolean isNetworkEnabled =  false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    private double lat, lon; // 위도, 경도

    // 최소 GPS 정보 업데이트 거리
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // 최소 GPS 정보 업데이트 시간(밀리세컨)
    private static final long MIN_TIME_BW_UPDATES = 1000*60*1; // 1분

    protected LocationManager locationManager;

    public GpsInfo(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public Location getLocation() {


        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                Toast.makeText(mContext, "위도 : " + lat + " 경도 : " + lon, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // GPS 정보 가져오기
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // 현재 네트워크 상태
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        else{
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                //Toast.makeText(this,"위치 정보를 사용할 수 없습니다.",Toast.LENGTH_SHORT);
                this.isGetLocation = false;
            }
            else{
                this.isGetLocation = true;

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener);

                if(location == null)
                    // gps 실패시 network로 위치 조회
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location !=null){
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
            }
        }
        return location;
    }
    // 권한 요청
    public void callPermission() {
        // 권한 체크
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 대화상자 표시
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;

        }
        else{
            isPermission = true;
        }

    }

    // 위도 값 가져오기
    public double getLat(){
        if(location!=null)
            lat = location.getLatitude();
        return lat;
    }

    // 경도 값 가져오기
    public double getLon(){
        if(location!=null)
            lon = location.getLongitude();
        return lon;
    }

    // GPS 켜있는지 확인
    public boolean isGetLocation(){
        return this.isGetLocation;
    }




    // 위도, 경도값 > 대한민국 주소로 변환
    public static String getCompleteAddress(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");


                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.w("MyCurrentloctionaddress", "주소가 리턴되지 않음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("MyCurrentloctionaddress", "주소를 가져올 수 없음");
        }

        // "대한민국" 글자 지워버림
        strAdd = strAdd.substring(5);

        return strAdd;
    }
    // GPS 정보 가져오지 못할시 설정창
    public void showSettings(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용확인");
        alertDialog.setMessage("GPS 설정을 확인해주세요.");

        // 확인할 시 설정창으로 이동.
        alertDialog.setPositiveButton("설정 창 이동",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // 취소할 시 원래 화면으로 이동
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}