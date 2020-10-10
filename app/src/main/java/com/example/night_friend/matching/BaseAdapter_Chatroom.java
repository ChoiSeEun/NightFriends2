package com.example.night_friend.matching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.night_friend.R;

import java.util.ArrayList;

public class BaseAdapter_Chatroom extends BaseAdapter {
    Context mContext = null;
    ArrayList<ChatData> mData = null;
    LayoutInflater mLayoutInflater = null;

    public BaseAdapter_Chatroom(Context context, ArrayList<ChatData> data) {
        mContext = context;
        mData = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout = null;
        ChatData item = mData.get(position);
        itemLayout = mLayoutInflater.inflate(R.layout.other_chat_layout, parent, false);

        TextView name = (TextView) itemLayout.findViewById(R.id.tv_chatName);
        TextView msg = (TextView) itemLayout.findViewById(R.id.tv_chatMsg);

        Log.e("size",mData.size()+"");
        name.setText(mData.get(position).getName());
        msg.setText(String.valueOf(mData.get(position).getMsg()));

        return itemLayout;
    }

    public void add(int index, ChatData addData) {
        mData.add(index, addData);
        notifyDataSetChanged();
    }

    public void delete(int index) {
        mData.remove(index);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }
}

