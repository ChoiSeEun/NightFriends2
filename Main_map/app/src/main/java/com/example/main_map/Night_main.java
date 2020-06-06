package com.example.main_map;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.main_map.Fragment1;
import com.example.main_map.Fragment2;
import com.example.main_map.Fragment3;
import com.example.main_map.Fragment4;
import com.example.main_map.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Night_main extends AppCompatActivity {

    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    Fragment4 fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottombar_main);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment1).commit();

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
    }

}
