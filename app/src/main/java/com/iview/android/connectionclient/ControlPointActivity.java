package com.iview.android.connectionclient;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iview.android.connectionclient.controlservice.ControlService;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class ControlPointActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "ControlPointActivity";

    private ControlServiceConnection mServiceConnection;
    private ControlService.ControlBinder mControlBinder = null;
    private ControlService mControlService;
    private Intent mIntent = null;

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_point);
        Log.e(TAG, "onCreate");
        if (Build.VERSION.SDK_INT > 23) {
            checkPermission();
        }
        initView();
        initControl();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.testview);
        mTextView.setOnClickListener(this);
    }

    private void initControl() {
        mServiceConnection = new ControlServiceConnection();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.ControlService");
        intent.setPackage("com.iview.android.connectionclient");
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private class ControlServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnect");
            mControlBinder = (ControlService.ControlBinder) service;
            mControlService = mControlBinder.getService();
            mControlService.searchDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mControlBinder = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testview:
                Log.e(TAG, "onClick");
                mControlService.searchDevice();
                break;
        }
    }

    public void checkPermission() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.INTERNET)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Log.d(TAG, "Internet permission accept");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d(TAG, permission.name + " is denied. More info should be provided.");
                            finish();
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d(TAG, permission.name + " is denied.");
                            finish();
                        }
                    }
                });
    }
}
