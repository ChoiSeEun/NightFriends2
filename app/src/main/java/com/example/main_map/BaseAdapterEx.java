package com.example.main_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BaseAdapterEx extends BaseAdapter {
    Context mContext=null;
    ArrayList<matching_data> mData= null;
    LayoutInflater mLayoutInflater=null;

    public BaseAdapterEx(Context context, ArrayList<matching_data> data){
        mContext=context;
        mData=data;
        mLayoutInflater=LayoutInflater.from(mContext);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout = convertView;
        if(itemLayout==null){
            itemLayout=mLayoutInflater.inflate(R.layout.list_view_item_layout,null);

        }
        TextView startTv=(TextView)itemLayout.findViewById(R.id.tv_start);
        TextView destTv=(TextView)itemLayout.findViewById(R.id.tv_dest);

        startTv.setText(mData.get(position).time);
        destTv.setText(mData.get(position).dest);


        return itemLayout;
    }

    public void add(int index, matching_data addData){
        mData.add(index,addData);
        notifyDataSetChanged();
    }

    public void delete(int index){
        mData.remove(index);
        notifyDataSetChanged();
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }




}

