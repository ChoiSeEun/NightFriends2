package com.example.deeplearning;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

public class MainActivity extends AppCompatActivity {
    Interpreter model;
     TextView result;
     Button button;
     int lamp;
     int cctv;
     int crimY;
     int crimN;
     int entertainY;
     int entertainN;
     EditText editText1;
     EditText editText2;
     EditText editText3;
     EditText editText4;
     EditText editText5;
     EditText editText6;
     float[][] input = new float[1][6];
     float[][] output = new float[1][1];
     String resulttemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.safescore);
        button = findViewById(R.id.button1);
        editText1 = (EditText)findViewById(R.id.lamp);
        editText2 = (EditText)findViewById(R.id.cctv);
        editText3 = (EditText)findViewById(R.id.crimY);
        editText4 = (EditText)findViewById(R.id.crimN);
        editText5 = (EditText)findViewById(R.id.EntertainY);
        editText6 = (EditText)findViewById(R.id.EntertainN);

        model = getTfliteInterpreter("converted_model.tflite");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    lamp = Integer.parseInt(editText1.getText().toString());
                    cctv = Integer.parseInt(editText2.getText().toString());
                    crimY = Integer.parseInt(editText3.getText().toString());
                    crimN = Integer.parseInt(editText4.getText().toString());
                    entertainY = Integer.parseInt(editText5.getText().toString());
                    entertainN = Integer.parseInt(editText6.getText().toString());
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                input[0][0] = lamp;
                input[0][1] = cctv;
                input[0][2] = crimY;
                input[0][3] = crimN;
                input[0][4] = entertainY;
                input[0][5] = entertainN;

                model.run(input,output);
                resulttemp = Float.toString(output[0][0]);

                result.setText(resulttemp);
            }
        });

    }
    private Interpreter getTfliteInterpreter(String modelPath){
        try{
            return new Interpreter(loadModelFile(MainActivity.this,modelPath));
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

