package com.iview.android.connectionclient.controlservice;

import android.app.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.iview.android.connectionclient.control.RemoteControl;

import static org.cybergarage.upnp.device.ST.ALL_DEVICE;


public class ControlService extends Service {

    private final static String TAG = "ControlService";

    private ControlBinder mBinder = new ControlBinder();

    private RemoteControl mControlPoint;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
        uninit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(TAG, "onBind");
        return mBinder;
    }

    public class ControlBinder extends Binder {
        public ControlService getService() {
            return ControlService.this;
        }
    }

    private void init() {
        mControlPoint = new RemoteControl();
        mControlPoint.search();
    }

    private void uninit() {
        mControlPoint = null;
    }

    public void searchDevice() {
        mControlPoint.search(ALL_DEVICE);
    }
}