package com.example.administrator.mylinphone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.administrator.mylinphone.util.LinphoneService;


import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import static android.content.Intent.ACTION_MAIN;


public class StartActivity extends AppCompatActivity  {
    private static final int PERMISSIONS_REQUEST = 1;
    private Handler mHandler;
    private ServiceWaitThread mThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        requestPermiss();


    }


    /**
     * 请求权限
     */
    private void requestPermiss(){
        PermissionGen.with(this)
                .addRequestCode(PERMISSIONS_REQUEST)
                .permissions(
                        android.Manifest.permission.WRITE_CONTACTS,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_SYNC_SETTINGS,
                        Manifest.permission.CAMERA
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = PERMISSIONS_REQUEST)
    public void requestPhotoSuccess(){
        mHandler = new Handler();
        if (LinphoneService.isReady()) {
            onServiceReady();
        } else {
            // start linphone as background
            startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
            mThread = new ServiceWaitThread();
            mThread.start();
        }

    }

    @PermissionFail(requestCode = PERMISSIONS_REQUEST)
    public void requestPhotoFail(){
        Log.e("sss", "requestPhotoFail: " );
        //失败之后的处理，我一般是跳到设置界面

    }



    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onServiceReady();
                }
            });
            mThread = null;
        }
    }




    protected void onServiceReady() {
        final Class<? extends Activity> classToStart;

            classToStart = TwoActivity.class;


        // We need LinphoneService to start bluetoothManager
//        if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
//            BluetoothManager.getInstance().initBluetooth();
//        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this,TwoActivity.class));
                finish();
            }
        }, 1000);
    }

}
