package com.example.main_map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;


public class Night_main extends AppCompatActivity implements AutoPermissionsListener { //AutoPermissionsListener : 긴급신고 기능 구현 위함

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    Fragment4 fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottombar_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.add(R.id.container, Fragment2.newInstance(null)).commit();

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment1).commit();

        Intent intent = getIntent();

        if (this.getIntent().getExtras()!=null && this.getIntent().getExtras().containsKey("code")){
            Log.e("여기까진..","성공..");
            Double start_lat = intent.getExtras().getDouble("start_lat");
            Double start_lon = intent.getExtras().getDouble("start_lon");
            Double end_lat = intent.getExtras().getDouble("end_lat");
            Double end_lon = intent.getExtras().getDouble("end_lon");

            Bundle bundle = new Bundle(4);
            bundle.putDouble("start_lat", start_lat);
            bundle.putDouble("start_lon", start_lon);
            bundle.putDouble("end_lat", end_lat);
            bundle.putDouble("end_lon", end_lon);
            fragment1.setArguments(bundle);
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
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

        return true;
    }
    Handler handler = new Handler();
    private void showMessage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        final AlertDialog dialog = builder.create();
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
}
