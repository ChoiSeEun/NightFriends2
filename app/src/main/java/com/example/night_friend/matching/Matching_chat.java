package com.example.night_friend.matching;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.night_friend.R;
import com.example.night_friend.main_map.Fragment4;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.app.AlertDialog.*;
import static com.example.night_friend.matching.ChatRoom_list.roomName;

public class Matching_chat extends AppCompatActivity {

    private EditText et_send;
    private Button btn_send,btn_chatroom;
    private Button btn_chatStart, btn_chatCancel;
    private ListView lt_send,lt_room;
    private String name, msg;
    private BaseAdapterChat adapter;
    private BaseAdapter_Chatroom room_adapter;
    private String matching_id;
    DatabaseReference databaseReference;
    ArrayList mList, roomList, ansList;
    private String chat_room;
    static String s_id;
    String id;
    static int chatCode=0;
    private GetLocation gPHP;
    String url = "http://night1234.dothome.co.kr/getAns.php";
    SimpleDateFormat format1;
    Date time;

    final userLocation userLocation = new userLocation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_chat);

        gPHP = new GetLocation();
        gPHP.execute(url);

        et_send = (EditText)findViewById(R.id.et_sendChat);
        btn_send = (Button)findViewById(R.id.btn_sendChat);
        lt_send = (ListView)findViewById(R.id.lt_send);
        lt_room = (ListView)findViewById(R.id.lt_room);
        btn_chatroom = (Button)findViewById(R.id.btn_chatroom);

        btn_chatStart = (Button)findViewById(R.id.btn_chatStart);
        btn_chatCancel = (Button)findViewById(R.id.btn_chatCancel);

        mList = new ArrayList<ChatData>();
        roomList = new ArrayList<ChatData>();
        ansList = new ArrayList<matching_data>();

        // 현재 시간

        format1 = new SimpleDateFormat ( "HH:mm");

        time = new Date();

        name = "user" + new Random().nextInt(10000);  // 랜덤한 유저 이름 설정

        adapter = new BaseAdapterChat(this,mList);
        lt_send.setAdapter(adapter);
        lt_send.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);


        //room_adapter = new BaseAdapter_Chatroom(this,roomList);
        //lt_room.setAdapter(room_adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //databaseReference = database.getReference("message");
        databaseReference = database.getReference();

        //databaseReference.setValue("Hello, World!");

        Intent secondIntent = getIntent();
        matching_id = secondIntent.getStringExtra("matching_id");
        id= Fragment4.userID;
        getToken();
        chatDecide();
        startChat(id,matching_id);



        btn_chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Matching_chat.this, ChatRoom_list.class);
                startActivity(intent);

            }
        });



    }

    public void startChat(final String id, String matching_id){

        // 채팅 room 설정

        s_id=id;

        if(chatCode==0){
            if(id.charAt(0)>matching_id.charAt(0)){
                chat_room = id+"&"+matching_id;
            }
            else if(id.charAt(0)<matching_id.charAt(0)){
                chat_room = matching_id+"&"+id;
            }
            else{
                chat_room =id+"&"+matching_id;
            }
        }
        else if(chatCode==1){

            chat_room = roomName;
        }


        // 채팅 시작시에 한번 chat_room 생성할 것
        databaseReference.child(chat_room).addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                RoomData roomData = new RoomData(chat_room ,chatData);
                Log.e("chatData",chatData.getName()+","+chatData.getMsg());

                databaseReference.child("UserRooms").child(chat_room).setValue(roomData);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


        // 채팅 보내기( firebase database로 보내기)
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatData chatData = new ChatData(id, et_send.getText().toString());  // 유저 이름과 메세지로 chatData 만들기
                databaseReference.child(chat_room).push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                et_send.setText("");

            }
        });


        // 채팅 받기

        databaseReference.child(chat_room).addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신합니다.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);  // chatData를 가져오고
                mList.add(chatData);  // adapter에 추가합니다.
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        btn_chatStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Matching_chat.this);

                builder.setTitle("매칭").setMessage("매칭 수락하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int dialogId)
                    {
                        //  userAns 값을 1(True)로 변환시키기
                        userLocation.user_Map(Constant.ANS_URL,id, 1);

                        //.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();

                        // 매칭을 수락한다는 메세지 보내기
                        ChatData chatData = new ChatData(id, id+"님이 수락하였습니다");  // 유저 이름과 메세지로 chatData 만들기
                        databaseReference.child(chat_room).push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                        et_send.setText("");
                        btn_chatCancel.setEnabled(false);


                        // userAns 값 1인지 비교


                        for(int i=0; i<ansList.size(); i++){
                            matching_data p = (matching_data) ansList.get(i);

                            Log.e("ansList: ",p.getId()+": "+p.getUserAns()+"m_id: "+matching_id);

                            String ansId;

                            if(p.getId().equals(matching_id)){
                                if(p.getUserAns()==1){
                                    Toast.makeText(getApplicationContext(), "매칭에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                    ChatData data = new ChatData("success","매칭이 성공적으로 연결되었습니다.");
                                    databaseReference.child(chat_room).push().setValue(data);
                                    btn_chatStart.setEnabled(false);
                                }
                                else if(p.getUserAns()==0){
                                    //Toast.makeText(getApplicationContext(), "매칭에 실패하였습니다.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }



                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();

            }
        });

        btn_chatCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // 수락 거절 기능 구현
    public void chatDecide(){

    }

    // userAns 값 가져오기

    class GetLocation extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null ) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if ( conn.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true ) {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            try {
                // PHP에서 받아온 JSON 데이터를 JSON오브젝트로 변환
                JSONObject jObject = new JSONObject(str);
                // results라는 key는 JSON배열로 되어있다.
                JSONArray results = jObject.getJSONArray("result");

                for(int i=0; i<results.length();i++){
                    JSONObject c = results.getJSONObject(i);
                    String id = c.getString("id");
                    double Lat = c.getDouble("userLat");
                    double Lon = c.getDouble("userLon");
                    double destlat=c.getDouble("destLat");
                    double destlon=c.getDouble("destLon");
                    int userAns = c.getInt("userAns");

                    matching_data person = new matching_data(id,Lat,Lon,destlat,destlon);
                    person.setUserAns(userAns);

                    ansList.add(person);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }




    // 토큰 값 가져오기
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("FirebaseInstanceId", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast

                        Log.e("FirebaseInstanceId", token);
                        //Toast.makeText(Matching_chat.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}