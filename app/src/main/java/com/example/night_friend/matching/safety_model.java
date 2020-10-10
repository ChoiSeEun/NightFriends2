package com.example.night_friend.matching;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class safety_model {
    Interpreter model;

    int lamp;
    int cctv;
    int crimY;
    int crimN;
    int entertainY;
    int entertainN;

    Context mContext;

    float[][] input = new float[1][6];
    float[][] output = new float[1][1];
    double resulttemp;
    Random random = new Random();

    public safety_model(Context context){
        mContext = context;
    }

    public safety_model(Context context, int lamp, int cctv, int crimY,int crimN, int entertainY,int entertainN ){
        mContext=context;
        this.lamp = lamp;
        this.cctv = cctv;
        this.crimY =crimY;
        this.crimN=crimN;
        this.entertainY = entertainY;
        this.entertainN  = entertainN;
    }

    public void setSafe(){
        model = getTfliteInterpreter("converted_model.tflite");

        lamp = random.nextInt(11);
        cctv = random.nextInt(11);
        crimY = random.nextInt(2);
        crimN = random.nextInt(2);
        entertainY = random.nextInt(2);
        entertainN = random.nextInt(2);

        input[0][0] = lamp;
        input[0][1] = cctv;
        input[0][2] = crimY;
        input[0][3] = crimN;
        input[0][4] = entertainY;
        input[0][5] = entertainN;

        model.run(input, output);
        resulttemp = Math.round(output[0][0]*100)/100.0;  //소수점 둘째자리까지 나타냄
    }

    public double getSafe(){
        setSafe();
        return resulttemp;
    }



    private Interpreter getTfliteInterpreter(String modelPath){
        try{
            return new Interpreter(loadModelFile((Activity) mContext,modelPath));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private MappedByteBuffer loadModelFile(Activity activity,String modelPath) throws IOException{
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
    }
}