package com.example.night_friend.matching;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.night_friend.R;
import com.example.night_friend.main_map.Fragment4;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.night_friend.matching.Matching_chat.chatCode;

public class ChatRoom_list extends AppCompatActivity {
    private ListView lt_room;
    private BaseAdapter_Chatroom room_adapter;
    DatabaseReference databaseReference;
    ArrayList chatList, roomList;
    static String roomName;
    private String chat_room;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        lt_room = (ListView) findViewById(R.id.lt_room);
        chatList = new ArrayList<ChatData>();
        roomList = new ArrayList<RoomData>();
        id= Fragment4.userID;
        room_adapter = new BaseAdapter_Chatroom(this, chatList);
        lt_room.setAdapter(room_adapter);

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //databaseReference = database.getReference("message");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setChatroom();
        clickEvent();

    }

    public void clickEvent(){
        lt_room.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomData select = (RoomData) roomList.get(position);
                Log.e("selectRoom",select.getRoomName()+"");
                chatCode=1;
                roomName = select.getRoomName();

                Intent intent = new Intent(ChatRoom_list.this, Matching_chat.class);
                startActivity(intent);


            }
        });

    }




    public void setChatroom() {

        databaseReference.child("UserRooms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                Log.e("getKey",key+"");
                if(key.contains(id)){
                    databaseReference.child("UserRooms").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            RoomData roomData = snapshot.getValue(RoomData.class);
                            Log.e("roomData",roomData.getRoomName());
                            chatList.add(roomData.getLastChat());  // adapter에 추가합니다.
                            roomList.add(roomData);
                            room_adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


/*
int j=0;
                Log.e("getKey",snapshot.getKey()+"");
                Log.e("snapshot",snapshot+","+previousChildName);
                Log.e("length",snapshot.getChildrenCount()+"");
                String key = snapshot.getKey();

                //ChatData data = snapshot.child("message").getValue(ChatData.class);
                // Log.e("datatest",data.getName()+","+data.getMsg());


                if(key.contains("abc")){
                    for(int i=0; i<snapshot.getChildrenCount();i++){
                        Log.e("getValue",snapshot.getValue(ChatData.class)+"");
                        ChatData chatData = snapshot.getValue(ChatData.class);  // chatData를 가져오고
                        Log.e("ChatData",chatData.getName()+","+chatData.getMsg());
                        chatList.add(chatData);  // adapter에 추가합니다.
                        room_adapter.notifyDataSetChanged();

                        Log.e("getCount","j= "+j+++"key: "+key);

                    }


                }
 */
