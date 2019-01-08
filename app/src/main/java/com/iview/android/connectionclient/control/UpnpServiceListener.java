package com.iview.android.connectionclient.control;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.iview.android.connectionclient.controlservice.ControlService;
import com.iview.android.connectionclient.model.IServiceListener;
import com.iview.android.connectionclient.model.upnp.CDevice;
import com.iview.android.connectionclient.model.upnp.IDeviceDiscoveryListener;
import com.iview.android.connectionclient.model.upnp.IUpnpDevice;

import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UpnpServiceListener implements IDeviceDiscoveryListener{
    private final static String TAG = "UpnpServiceListener";

    private Context mContext;
    private ControlServiceConnection mControlServiceConnection = null;
    private ControlService.ControlBinder mControlBinder = null;
    private ControlService mControlService = null;

    private List<IDeviceDiscoveryListener> mDeviceListerner = new ArrayList<>();

    public UpnpServiceListener(Context context)
    {
        mContext = context;
        mControlServiceConnection = new ControlServiceConnection(this);
    }

    public ControlServiceConnection getControlServiceConnection () {
        return mControlServiceConnection;
    }

    private class ControlServiceConnection implements ServiceConnection {

        IDeviceDiscoveryListener listener;

        public ControlServiceConnection(IDeviceDiscoveryListener listener) {
            this.listener = listener;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnect");
            mControlBinder = (ControlService.ControlBinder) service;
            mControlService = mControlBinder.getService();
            mControlService.addDeviceListerner(listener);
            mControlService.searchDevice();

            if (mControlService != null) {
                DeviceList deviceList = mControlService.getDeviceList();
                for (int i = 0; i < mDeviceListerner.size(); i++) {
                    for (int j = 0; j < deviceList.size(); j++) {
                        mDeviceListerner.get(i).addedDevice(new CDevice(deviceList.getDevice(j)));
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mControlBinder = null;
            mControlService = null;
        }
    }

    public void addDeviceListener(IDeviceDiscoveryListener listener) {
        if (!mDeviceListerner.contains(listener)) {
            mDeviceListerner.add(listener);
        }
    }

    public void removeDeviceListener(IDeviceDiscoveryListener listener) {
        if (mDeviceListerner.contains(listener)) {
            mDeviceListerner.remove(listener);
        }
    }

    @Override
    public void addedDevice(IUpnpDevice device) {
        for (int i = 0; i < mDeviceListerner.size(); i++) {
                mDeviceListerner.get(i).addedDevice(device);
        }
    }

    @Override
    public void removedDevice(IUpnpDevice device) {
        for (int i = 0; i < mDeviceListerner.size(); i++) {
            mDeviceListerner.get(i).removedDevice(device);
        }
    }

    public RemoteControl getControlPoint() {
        return mControlService.getControlPoint();
    }

    public Collection<IUpnpDevice> getMediaServerDeviceList() {
        DeviceList deviceList = mControlService.getControlPoint().getDeviceList();
        ArrayList<IUpnpDevice> iDeviceList = new ArrayList<IUpnpDevice>();
        for(int i = 0; i < deviceList.size(); i++) {
            if (deviceList.getDevice(i).getDeviceType().contains("MediaServer")) {
                iDeviceList.add(new CDevice(deviceList.getDevice(i)));
            }
        }

        return iDeviceList;
    }

    public Collection<IUpnpDevice> getMediaRendererDeviceList() {
        DeviceList deviceList = mControlService.getControlPoint().getDeviceList();
        ArrayList<IUpnpDevice> iDeviceList = new ArrayList<IUpnpDevice>();
        for(int i = 0; i < deviceList.size(); i++) {
            if (deviceList.getDevice(i).getDeviceType().contains("MediaRenderer")) {
                iDeviceList.add(new CDevice(deviceList.getDevice(i)));
            }
        }

        return iDeviceList;
    }

    public void Search() {
        mControlService.getControlPoint().search();
    }

}
