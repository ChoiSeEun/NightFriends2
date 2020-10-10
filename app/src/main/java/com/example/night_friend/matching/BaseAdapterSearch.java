package com.example.night_friend.matching;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.night_friend.R;

import java.util.ArrayList;

public class BaseAdapterSearch extends BaseAdapter {
    Context mContext=null;
    ArrayList<Locationitem> mData= null;
    LayoutInflater mLayoutInflater=null;

    public BaseAdapterSearch(Context context, ArrayList<Locationitem> data){
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
            itemLayout=mLayoutInflater.inflate(R.layout.listview_search_layout,null);

        }
        TextView placeTv=(TextView)itemLayout.findViewById(R.id.tv_place);
        TextView AddressTv = (TextView)itemLayout.findViewById(R.id.tv_address);

        placeTv.setText(mData.get(position).getPlace_name());
        AddressTv.setText(mData.get(position).getAddress());

        return itemLayout;
    }

    public void add(int index, Locationitem addData){
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
