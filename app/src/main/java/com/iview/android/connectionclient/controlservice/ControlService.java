package com.iview.android.connectionclient.controlservice;

import android.app.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.iview.android.connectionclient.control.RemoteControl;
import com.iview.android.connectionclient.model.IServiceListener;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.cybergarage.upnp.device.ST.ALL_DEVICE;


public class ControlService extends Service implements IServiceListener{

    private final static String TAG = "ControlService";

    private ControlBinder mBinder = new ControlBinder();

    private RemoteControl mControlPoint;

    private List<IServiceListener> mServiceListerner = new ArrayList<>();

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
        mControlPoint.regisiterControlListener(this);
        mControlPoint.search();
    }

    private void uninit() {
        mControlPoint.unregisiterControlListener();
        mControlPoint = null;
    }

    public void searchDevice() {
        mControlPoint.search(ALL_DEVICE);
    }

    public void addServiceListerner(IServiceListener serviceListerner) {
        mServiceListerner.add(serviceListerner);
    }

    public void removeSerivceListerner(IServiceListener serviceListener) {
        if (mServiceListerner.contains(serviceListener)) {
            mServiceListerner.remove(serviceListener);
        }
    }
    @Override
    public void deviceAdded(final Device device) {
        if (!mServiceListerner.isEmpty()) {
            for (int i = 0; i < mServiceListerner.size(); i++) {
                mServiceListerner.get(i).deviceAdded(device);
            }
        }
    }

    @Override
    public void deviceRemoved(final Device device) {
        if (!mServiceListerner.isEmpty()) {
            for (int i = 0; i < mServiceListerner.size(); i++) {
                mServiceListerner.get(i).deviceRemoved(device);
            }
        }
    }

    public DeviceList getDeviceList() {
        DeviceList list = mControlPoint.getDeviceList();
        return list;
    }

    public RemoteControl getControlPoint() {
        return mControlPoint;
    }
}