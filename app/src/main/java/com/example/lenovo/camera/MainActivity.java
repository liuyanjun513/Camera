package com.example.lenovo.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CameraPreview mPreview;
    FrameLayout preview;
    Button bPath,buttonCaptureVideo,buttonCapturePhoto;
    ImageView mediaPreview;
    private static final String TAG="CameraPreview";
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).toString());
    String path=mediaStorageDir.getPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPreview = (ImageView) findViewById(R.id.media_preview);
        preview=(FrameLayout) findViewById(R.id.camera_preview);

        //mPreview=new CameraPreview(this,path);
        initCamera();

        bPath=(Button)findViewById(R.id.button_path);
        bPath.setOnClickListener(this);

        buttonCaptureVideo=(Button)findViewById(R.id.button_capture_video);
        buttonCaptureVideo.setOnClickListener(this);

        buttonCapturePhoto=(Button)findViewById(R.id.button_capture_photo);
        buttonCapturePhoto.setOnClickListener(this);

        mediaPreview.setOnClickListener(this);



        //ask user for permission
        requestMultiplePermissions();
    }
    private static final int REQUEST_CODE = 1;
    private void requestMultiplePermissions(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
        requestPermissions(permissions, REQUEST_CODE);
    }


    private void initCamera() {
        mPreview = new CameraPreview(this,path);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Log.i("initCamera","initCamera is using");

    }
    public void onPause() {
        super.onPause();
        mPreview = null;
        Log.e("usePause","usePause");

    }

    public void onResume() {
        super.onResume();
        Log.e("useResume","useResume");
        if (mPreview == null) {
            initCamera();
        }
    }


    private void showDialog(View view) {
        EnterPath enterPath= new EnterPath(MainActivity.this,path);
        enterPath.setOnDialogClickListener(new EnterPath.OnDialogClickListener() {
            @Override
            public void returnPath(String path) {
                if(path!=null || !TextUtils.isEmpty(path)){
                    MainActivity.this.path=path;
                    mPreview.setPath(path);
                }
            }
        });
        enterPath.show();


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_path:
                showDialog(view);
                break;
            case R.id.button_capture_photo:
                mPreview.takePicture(mediaPreview);
                break;
            case R.id.button_capture_video:
                if (mPreview.isRecording()) {
                    mPreview.stopRecording(mediaPreview);
                    buttonCaptureVideo.setText("Record");
                } else {
                    if (mPreview.startRecording()) {
                        buttonCaptureVideo.setText("Stop");
                    }
                }
                break;
            case R.id.media_preview:
                Intent intent = new Intent(MainActivity.this, ShowPhotoVideo.class);
                String MediaFileType=mPreview.getOutputMediaFileType();
                Uri MediaFileUri=mPreview.getOutputMediaFileUri();
                if(MediaFileType!=null && MediaFileUri!=null){
                    intent.setDataAndType(MediaFileUri, MediaFileType);
                    startActivityForResult(intent, 0);
                }else{
                    Toast.makeText(getBaseContext(),"Please take a photo or video first",Toast.LENGTH_LONG).show();
                }
                break;


        }

    }
}
