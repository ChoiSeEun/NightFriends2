package com.example.night_friend.main_map;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.night_friend.R;
import com.example.night_friend.login.LoginActivity;
import com.example.night_friend.main_map.Fragment1;
import com.example.night_friend.main_map.Fragment2;
import com.example.night_friend.main_map.Fragment3;
import com.example.night_friend.main_map.Fragment4;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

public class Night_main extends AppCompatActivity implements AutoPermissionsListener{ //AutoPermissionsListener : 긴급신고 기능 구현 위함

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    Fragment4 fragment4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment1).commit();
        //fragmentTransaction.add(R.id.container, Fragment2.newInstance(null)).commit();
        //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Intent intent_login = getIntent();
        String userID = intent_login.getStringExtra("userID");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.container);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.drawer_userid);
        navUsername.setText(userID);

//메뉴 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_setting:


                    case R.id.nav_gallery:

                    case R.id.nav_logout://로그아웃+++++++++++++여기서부터 다시
                        Log.i("logout", "logout");
                        //new AlertDialog.Builder(getApplicationContext())
                        //.setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        //.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        //public void onClick(DialogInterface dialog, int whichButton) {


                        // Intent i = new Intent(Night_main.this//현재 액티비티 위치 , LoginActivity.class//이동 액티비티 위치);
                        // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

                        SharedPreferences auto = getSharedPreferences("setting", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto.edit();
                        //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                        editor.clear();
                        editor.commit();//startActivity(i);
                        finish();


                }

                DrawerLayout drawer1 = findViewById(R.id.drawer_layout);
                drawer1.closeDrawer(GravityCompat.START);

                return true;
            }
        });


        Intent intent = getIntent();

        if (this.getIntent().getExtras()!=null && this.getIntent().getExtras().containsKey("code")){
            //GuideActivity에서 넘어온 데이터
            if(intent.getExtras().getInt("code")==103) {
                Double start_lat = intent.getExtras().getDouble("start_lat");
                Double start_lon = intent.getExtras().getDouble("start_lon");
                Double end_lat = intent.getExtras().getDouble("end_lat");
                Double end_lon = intent.getExtras().getDouble("end_lon");

                if(intent.getExtras().containsKey("code2") && intent.getExtras().getInt("code2")==1031) {
                    String passList_mid = intent.getExtras().getString("passList"); //경도, 위도 순서
                    //Log.e("night_main: ","passList_mid 전달받음"+passList_mid);
                    Bundle bundle = new Bundle(6);
                    bundle.putInt("code",1031);
                    bundle.putDouble("start_lat", start_lat);
                    bundle.putDouble("start_lon", start_lon);
                    bundle.putDouble("end_lat", end_lat);
                    bundle.putDouble("end_lon", end_lon);
                    bundle.putString("passList_mid", passList_mid);
                    fragment1.setArguments(bundle);
                } else if(intent.getExtras().containsKey("code2") && intent.getExtras().getInt("code2")==1032) {
                    String passList2 = intent.getExtras().getString("passList");  //경도, 위도 순서
                    //Log.e("night_main: ","passList2 전달받음"+passList2);
                    Bundle bundle = new Bundle(6);
                    bundle.putInt("code",1032);
                    bundle.putDouble("start_lat", start_lat);
                    bundle.putDouble("start_lon", start_lon);
                    bundle.putDouble("end_lat", end_lat);
                    bundle.putDouble("end_lon", end_lon);
                    bundle.putString("passList2", passList2);
                    fragment1.setArguments(bundle);
                } else if(intent.getExtras().containsKey("code2") && intent.getExtras().getInt("code2")==1033) {
                    String passList3 = intent.getExtras().getString("passList");  //경도, 위도 순서
                    //Log.e("night_main: ","passList3 전달받음"+passList3);
                    Bundle bundle = new Bundle(6);
                    bundle.putInt("code",1033);
                    bundle.putDouble("start_lat", start_lat);
                    bundle.putDouble("start_lon", start_lon);
                    bundle.putDouble("end_lat", end_lat);
                    bundle.putDouble("end_lon", end_lon);
                    bundle.putString("passList3", passList3);
                    fragment1.setArguments(bundle);
                } else {
                    Bundle bundle = new Bundle(5);
                    bundle.putInt("code", 103);
                    bundle.putDouble("start_lat", start_lat);
                    bundle.putDouble("start_lon", start_lon);
                    bundle.putDouble("end_lat", end_lat);
                    bundle.putDouble("end_lon", end_lon);
                    fragment1.setArguments(bundle);
                }
            }
            else if (intent.getExtras().getInt("code") == 104) {
                Double destLat = intent.getExtras().getDouble("destLat");
                Double destLon = intent.getExtras().getDouble("destLon");
                String destPlace = intent.getExtras().getString("destPlace");

                Bundle bundle2 = new Bundle(4);
                bundle2.putInt("code",104);
                bundle2.putString("destPlace", destPlace);
                bundle2.putDouble("destLat", destLat);
                bundle2.putDouble("destLon", destLon);
                fragment1.setArguments(bundle2);
                fragment4.setArguments(bundle2);
            }
        }

        Intent intent_m=getIntent();

        if(intent_m.getExtras().getInt("code3") == 105){
            Bundle bundle3 = new Bundle(1);
            bundle3.putInt("code3",105);
            fragment2.setArguments(bundle3);

            replaceFragment(fragment2);
            //fragment2.bt_guide.performClick();

        }else if(intent_m.getExtras().getInt("key")==44){
            Log.e("fragment4","ddddd");
            Bundle bundle4 = new Bundle(1);
            bundle4.putInt("q",27);
            fragment4.setArguments(bundle4);
            replaceFragment(fragment4);
        }
        else if(intent_m.getExtras().getInt("key")==66){
            Log.e("fragment4","ddddd");
            Bundle bundle4 = new Bundle(1);
            bundle4.putInt("q",28);
            fragment4.setArguments(bundle4);
            replaceFragment(fragment4);
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1.newInstance()).commit();

                                return true;
                            case R.id.tab2:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                                return true;
                            case R.id.tab3:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                                return true;
                            case R.id.tab4:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment4).commit();

                                return true;
                        }
                        return false;
                    }
                }
        );
        String[] permissions = {
                Manifest.permission.CALL_PHONE
        };
        AutoPermissions.Companion.loadAllPermissions(this,101);
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_logout) {
            new android.app.AlertDialog.Builder(this/* 해당 액티비티를 가르킴 */)
                    .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent i = new Intent(Night_main.this/*현재 액티비티 위치*/, LoginActivity.class/*이동 액티비티 위치*/);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    })
                    .show();

        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }
    @Override
    public void onDenied(int requestCode, @NonNull String[] permissions) {
        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onGranted(int requestCode, @NonNull String[] permissions) {
        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }
    //public boolean flag=true;
    public int count = 0;
    long start;
    long prev = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(SOS_setting.autoSwitchState!=false){
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN: {

                    if (SystemClock.elapsedRealtime() - prev > 1000)
                        count = 0;
                    if (count == 0) {
                        start = SystemClock.elapsedRealtime();
                        Log.d("start time", String.valueOf(start));
                    }
                    prev = SystemClock.elapsedRealtime();
                    count++;
                    Log.d("count", String.valueOf(count));
                    Log.d("prev time", String.valueOf(prev));

                    if (count == 4 && prev - start < 2000) {
                        Log.d("log****", String.valueOf(prev - start));
                        showMessage();
                    }
                }
            }
        }

        return true;
    }

    Handler handler = new Handler();
    private void showMessage() {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("지금 즉시 경찰에 신고 됩니다.\n신고를 원하지 않으면 취소를 누르세요.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.removeCallbacksAndMessages(null);
                //CDT.cancel();
            }
        });
        final androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(intent);
            }
        },5000);

    }

    //프래그먼트 전환 함수
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }
   /* //뒤로가기 두번이면 앱 종료(미완성)
    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    @Override
    public void onBackPressed() {
        Log.e("backbutton","dd");
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
            toast = Toast.makeText(this, "이용해 주셔서 감사합니다.", Toast.LENGTH_LONG);
            toast.show();
        }
    }*/
}
