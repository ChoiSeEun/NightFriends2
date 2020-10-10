package com.example.night_friend.matching;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.night_friend.R;
import com.example.night_friend.main_map.Fragment4;

import java.util.ArrayList;

public class BaseAdapterEx extends BaseAdapter {
    Context mContext=null;
    ArrayList<userdistance> mData= null;
    LayoutInflater mLayoutInflater=null;

    public BaseAdapterEx(Context context, ArrayList<userdistance> data){
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View itemLayout = convertView;
        if(itemLayout==null){
            itemLayout=mLayoutInflater.inflate(R.layout.list_view_item_layout,null);

        }
        TextView otid=(TextView)itemLayout.findViewById(R.id.tv_otid);
        TextView distance=(TextView)itemLayout.findViewById(R.id.distance);
        otid.setText(mData.get(position).getId());
        distance.setText(((int) (mData.get(position).getDistance()*1000))+"m");
        final String text=mData.get(position).getId();
        Button button = (Button)itemLayout.findViewById(R.id.matching_map2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("매칭");
                builder.setMessage(text+"의 정보 확인하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //매칭맵으로 데이터 전달
                                Toast.makeText(mContext,"예를 선택했습니다.",Toast.LENGTH_LONG).show();
                                Intent intent= new Intent(mContext, Matching_map.class);
                                intent.putExtra("key",11);
                                intent.putExtra("set",mData.get(position));
                                mContext.startActivity(intent);

                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext,"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        });

        return itemLayout;
    }

    public void add(int index, userdistance addData){
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

