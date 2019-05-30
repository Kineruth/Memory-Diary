package com.memoryDiary.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.memoryDiary.Activity.Main.MainActivity;
import com.memoryDiary.Activity.Start.ShowCaptureActivity;
import com.memoryDiary.R;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class CameraFragment extends Fragment implements SurfaceHolder.Callback {
    Camera camera;
    Camera.PictureCallback jpegCallback;
    public SurfaceView cameraSurfaceView;
    public SurfaceHolder cameraSurfaceHolder;
    private final int CAMERA_REQUEST_CODE = 1;
    public MainActivity mainActivity = (MainActivity) getActivity();




    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraSurfaceView = view.findViewById(R.id.cameraSurfaceView);
        cameraSurfaceHolder = cameraSurfaceView.getHolder();

//        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            cameraSurfaceHolder.addCallback(this);
            cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        Button captureButton = view.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        jpegCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
                intent.putExtra("capture", data);
                startActivity(intent);
                return;
            }
        };
        return view;
    }

    private void captureImage() {
        camera.takePicture(null, null, jpegCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = android.hardware.Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();

        camera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for(int i = 1; i < sizeList.size(); i++){
            if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        parameters.setPictureSize(bestSize.width, bestSize.height);

        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, Objects.requireNonNull(grantResults));
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    cameraSurfaceHolder.addCallback(this);
                    cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }else{
                    Toast.makeText(getContext(), "Please provide permission to camera.", Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }
}