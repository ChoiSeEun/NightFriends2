package com.example.night_friend.Guide;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.night_friend.R;

public class GuideListView extends LinearLayout {
    TextView timeText;
    TextView roadText;
    TextView scoreText;
    Button matchingBtn;

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
        matchingBtn = (Button) findViewById(R.id.matching_button);
    }
    public void setTimeText(String time){
        timeText.setText(time);
    }
    public void setRoadText(String road){
        roadText.setText(road);
    }
    public void setScoreText(String score){
        scoreText.setText(score);
    }
    public void setMatchingBtn(String text){
        matchingBtn.setText(text);
    }
}
