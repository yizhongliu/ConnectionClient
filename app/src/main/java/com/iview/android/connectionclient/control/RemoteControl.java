package com.iview.android.connectionclient.control;

import android.util.Log;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;

public class RemoteControl extends ControlPoint implements NotifyListener, EventListener, SearchResponseListener, DeviceChangeListener {

    private final static String TAG = "RemoteControl";

    public RemoteControl() {
        addNotifyListener(this);
        addSearchResponseListener(this);
        addEventListener(this);

//        start();
    }

    ////////////////////////////////////////////////
    //	Listener
    ////////////////////////////////////////////////
    public void deviceNotifyReceived(SSDPPacket ssdpPacket) {
        String uuid = ssdpPacket.getUSN();
        String target = ssdpPacket.getNT();
        String subType = ssdpPacket.getNTS();
        String location = ssdpPacket.getLocation();
        Log.e(TAG, "deviceNotifyReceived : uuid:" + uuid + ", target:" + target + ",subType:" + subType + ",location:" + location);
    }

    public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
        String uuid = ssdpPacket.getUSN();
        String target = ssdpPacket.getNT();
        String subType = ssdpPacket.getNTS();
        String location = ssdpPacket.getLocation();
        Log.e(TAG, "deviceNotifyReceived : uuid:" + uuid + ", target:" + target + ",subType:" + subType + ",location:" + location);
    }

    public void eventNotifyReceived(String uuid, long seq, String name, String value) {
    }

    public void deviceAdded (Device dev) {

    }
    public void deviceRemoved(Device dev) {

    }

    ////////////////////////////////////////////////
    //	other function
    ////////////////////////////////////////////////
    public void deviceSearch() {
        search();
    }
}
