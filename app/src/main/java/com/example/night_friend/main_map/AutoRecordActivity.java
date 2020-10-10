package com.example.night_friend.main_map;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.night_friend.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class AutoRecordActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    Intent intent; // 넘어온 페이지 intent
    boolean recording;  // 녹화 진행 여부 변수

    private Camera camera;
    private MediaRecorder mediaRecorder;
    private Button record_controll;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_record);

        intent = getIntent();
        recording = intent.getBooleanExtra("record",true);

        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("녹화를 위하여 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다. 설정>권한에서 허용해주세요")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .check();

        record_controll = (Button) findViewById(R.id.record_control);
        record_controll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(recording){ //버튼 다시 누르면 녹화 종료
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording = false; //녹화 진행 여부 변경
                    mediaRecorder = null;
                    Toast.makeText(AutoRecordActivity.this,"녹화가 종료되었습니다.",Toast.LENGTH_SHORT).show();

                    //intent 사용하여 다음 페이지로 넘김
                }
                else{ //녹화 진행
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(AutoRecordActivity.this,"자동 녹화를 시작합니다.",Toast.LENGTH_SHORT).show();
                            try{
                                mediaRecorder = new MediaRecorder();
                                camera.unlock();
                                mediaRecorder.setCamera(camera);
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER); //녹화 소리
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P)); //동영상 화질
                                mediaRecorder.setOrientationHint(90); //촬영각도
                                mediaRecorder.setOutputFile("/sdcard/nightfriend.mp3");//저장경로
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                recording = true;
                            }catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(AutoRecordActivity.this,"오류가 발생하였습니다.",Toast.LENGTH_SHORT).show();
                                mediaRecorder.release();
                                mediaRecorder = null;
                            }
                        }
                    });
                }
            }
        });
    }
    PermissionListener permission = new PermissionListener() { //카메라 권한 허용 or 거부 시 실행
        @Override
        public void onPermissionGranted() { //권한 허용
            //Toast.makeText(RecodingActivity.this,"권한 허가",Toast.LENGTH_SHORT).show();

            camera = Camera.open();
            camera.setDisplayOrientation(90);
            surfaceView = (SurfaceView)findViewById(R.id.preview_camera);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(AutoRecordActivity.this);
            surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            if(!recording) // 버튼 자동 실행을 통한 자동 녹화 진행 , else 문으로 들어가 녹화 진행됨
                record_controll.performClick();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) { //권한 거부
            Toast.makeText(AutoRecordActivity.this,"권한 거부",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }
    private void refreshCamera(Camera camera){ //카메라 초기화
        if(surfaceHolder.getSurface() == null){
            return;
        }
        try{
            camera.stopPreview();
        }catch(Exception e){
            e.printStackTrace();
        }
        setCamera(camera);
    }
    private void setCamera(Camera cam){camera = cam;} //카메라 설정
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera(camera);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }
}
