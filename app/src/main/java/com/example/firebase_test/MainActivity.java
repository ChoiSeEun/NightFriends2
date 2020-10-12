package com.example.firebase_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button bt_cctv;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference;
    private DatabaseReference dReference;
    private ChildEventListener mChild;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<CCTV> cctvlist=new ArrayList<CCTV>();
    List<Object> Array = new ArrayList<Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_cctv=(Button)findViewById(R.id.bt_cctv);
        //tv_cctv=(TextView)findViewById(R.id.cctv);
        listView = (ListView) findViewById(R.id.listviewmsg);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(adapter);
        mReference = mDatabase.getReference("cctv/"); // 변경값을 확인할 child 이름
        dReference = mDatabase.getReference("lamp/"); // 변경값을 확인할 child 이름

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long start = System.currentTimeMillis();
                //dataSnapshot.getKey();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //dataSnapshot.getKey();
                    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    //String str= (String) snapshot.getValue();

                    //Log.d("MainActivity", "getkey : " + snapshot.getKey());

                }
                long end = System.currentTimeMillis();
                Log.d("cctv_수행시간 : ", String.valueOf(( end - start )));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long start = System.currentTimeMillis();
                dataSnapshot.getKey();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    for (DataSnapshot deeperSnapshot : snapshot.getChildren()) {
                        CCTV cctv=new CCTV();
                        //Log.d("MainActivity", "depper key : " + deeperSnapshot.getKey());
                        if(deeperSnapshot.getKey().equals("경도")){
                             cctv.setXpos(Double.parseDouble(String.valueOf(deeperSnapshot.getValue())));
                        }else if (deeperSnapshot.getKey().equals("위도")){
                            cctv.setYpos(Double.parseDouble(String.valueOf(deeperSnapshot.getValue())));
                        }
                        cctvlist.add(cctv);

                    }
/*
                    String str= (String) snapshot.getValue();

                    String[] tokens=str.split(",");
                    CCTV t = new CCTV();
                    if (tokens[0].length() > 0) {   //위도 값이 비어있지 않으면
                        String[] token=tokens[0].split("=");
                        Log.d("dd",token[1]);
                        t.setYpos(Double.parseDouble(token[1]));
                    } else {
                        t.setYpos((double) 0);
                    }
                    if (tokens[11].length() > 0) {   //경도 값이 비어있지 않으면
                        t.setXpos(Double.parseDouble(tokens[11]));
                    } else {
                        t.setXpos((double) 0);
                    }
                    cctvlist.add(t);
                    //Log.d("MyActivity", "Just created cctv Latitude: " + t.getXpos()+"Longitude: "+t.getYpos());
*/


                }
                long end = System.currentTimeMillis();
                Log.d("수행시간 : ", String.valueOf(( end - start )/1000));
                Log.d("cctvlist", String.valueOf(cctvlist.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        bt_cctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
