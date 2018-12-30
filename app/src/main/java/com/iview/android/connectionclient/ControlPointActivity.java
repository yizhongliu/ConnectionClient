package com.iview.android.connectionclient;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iview.android.connectionclient.controlservice.ControlService;
import com.iview.android.connectionclient.model.DeviceDisplay;
import com.iview.android.connectionclient.model.IServiceListener;
import com.iview.android.connectionclient.view.Content;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class ControlPointActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "ControlPointActivity";

    private ControlServiceConnection mServiceConnection;
    private ControlService.ControlBinder mControlBinder = null;
    private ControlService mControlService;

    private DrawerFragment mDrawerFragment;

    private ServiceListerner mServiceListerner = new ServiceListerner();

    private final static int MSG_ADD_DEVICE = 0;
    private final static int MSG_REMOVE_DEVICE = 1;

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_DEVICE:
                    ArrayList addData = msg.getData().getParcelableArrayList("data");
                    DeviceDisplay addDeviceDisplay = (DeviceDisplay) addData.get(0);
                    addDevice(addDeviceDisplay);
                    break;
                case MSG_REMOVE_DEVICE:
                    ArrayList removeData = msg.getData().getParcelableArrayList("data");
                    DeviceDisplay removeDeviceDisplay = (DeviceDisplay) removeData.get(0);
                    switch (removeDeviceDisplay.getDeviceType()) {
                        case DeviceDisplay.DEVICE_TYPE_MEDIASERVER:
                            removeDevice(removeDeviceDisplay);
                            break;
                    }
                    break;
            }
        }
    };

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
        if(getFragmentManager().findFragmentById(R.id.navigation_drawer) instanceof DrawerFragment)
        {
            mDrawerFragment = (DrawerFragment)
                    getFragmentManager().findFragmentById(R.id.navigation_drawer);

            // Set up the drawer.
            mDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
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
            mControlService.addServiceListerner(mServiceListerner);
            mControlService.searchDevice();

            DeviceList deviceList = mControlService.getDeviceList();
            for (int i = 0; i < deviceList.size(); i++ ) {
                Log.e(TAG, "getDecivelist :" + deviceList.getDevice(i).getFriendlyName());
                DeviceDisplay deviceDisplay = new DeviceDisplay(deviceList.getDevice(i));
                addDevice(deviceDisplay);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mControlBinder = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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

    private class ServiceListerner implements IServiceListener {

        @Override
        public void deviceAdded(final Device device) {
            Log.d(TAG, "device add");
            DeviceDisplay deviceDisplay = new DeviceDisplay(device);
            Bundle bundle = new Bundle();
            ArrayList arr = new ArrayList();
            arr.add(deviceDisplay);
            bundle.putStringArrayList("data",arr);
            Message message = new Message();
            message.what = MSG_ADD_DEVICE;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void deviceRemoved(final Device device) {
            Log.d(TAG, "device remove");
            DeviceDisplay deviceDisplay = new DeviceDisplay(device);
            Bundle bundle = new Bundle();
            ArrayList arr = new ArrayList();
            arr.add(deviceDisplay);
            bundle.putStringArrayList("data",arr);
            Message message = new Message();
            message.what = MSG_REMOVE_DEVICE;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    public void addDevice(DeviceDisplay dev) {
        Log.e(TAG, "addDevie und" + dev.getUDN());
        switch (dev.getDeviceType()) {
            case DeviceDisplay.DEVICE_TYPE_MEDIASERVER:
                mDrawerFragment.addContetnDisplayDevice(dev);
                break;
            case DeviceDisplay.DEVICE_TYPE_RENDERER:
                mDrawerFragment.addRendererDevice(dev);
                break;
        }
    }

    public void removeDevice(DeviceDisplay dev) {
        Log.e(TAG, "removeDevice und" + dev.getUDN());
        switch (dev.getDeviceType()) {
            case DeviceDisplay.DEVICE_TYPE_MEDIASERVER:
                mDrawerFragment.removeContetnDisplayDevice(dev);
                break;
            case DeviceDisplay.DEVICE_TYPE_RENDERER:
                mDrawerFragment.removeRendererDevice(dev);
                break;
        }
    }
}
