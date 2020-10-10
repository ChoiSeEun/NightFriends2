package com.example.night_friend.Guide;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.night_friend.R;
import com.example.night_friend.main_map.Fragment4;

public class GuideListView extends LinearLayout {
    TextView timeText;
    TextView roadText;
    TextView scoreText;

    public GuideListView(Context context){
        super(context);
        init(context);
    }
    public GuideListView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.guide_road_list,this,true);

        timeText = (TextView) findViewById(R.id.road_time);
        roadText = (TextView) findViewById(R.id.road_detail);
        scoreText = (TextView) findViewById(R.id.safe_score);
        //matchingBtn = (Button) findViewById(R.id.matching_button);

    }
    public void setTimeText(Integer time){
        timeText.setText(""+time+"분");
    }
    public void setRoadText(String road){
        roadText.setText(road);
    }
    public void setScoreText(Double score){
        scoreText.setText(""+score+"%");
    }

}
