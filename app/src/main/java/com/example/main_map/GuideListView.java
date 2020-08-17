package com.example.main_map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

        matchingBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void setTimeText(Integer time){
        timeText.setText(""+time+"분");
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
