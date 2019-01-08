package com.iview.android.connectionclient.control;

import android.util.Log;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.std.av.renderer.AVTransport;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;
import org.cybergarage.upnp.std.av.server.object.item.ResourceNode;

public class RendererControl {

    private static final String TAG = "RendererControl";

    private final ControlPoint controlPoint;

    public RendererControl(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    ////////////////////////////////////////////////
    // play
    ////////////////////////////////////////////////

    public boolean setAVTransportURI(
            Device dev,
            ItemNode itemNode)
    {
        if (dev == null)
            return false;

        Log.e(TAG, "dev.getLocation：" + dev.getLocation());

        ResourceNode resNode = itemNode.getFirstResource();
        if (resNode == null)
            return false;
        String resURL = resNode.getURL();
        if (resURL == null || resURL.length() <= 0)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.SETAVTRANSPORTURI);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");
        action.setArgumentValue(AVTransport.CURRENTURI, resURL);
        action.setArgumentValue(AVTransport.CURRENTURIMETADATA, "");

        return action.postControlAction();
    }

    public boolean play(Device dev)
    {
        if (dev == null)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.PLAY);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");
        action.setArgumentValue(AVTransport.SPEED, "1");

        return action.postControlAction();
    }

    public boolean stop(Device dev)
    {
        if (dev == null)
            return false;

        Service avTransService = dev.getService(AVTransport.SERVICE_TYPE);
        if (avTransService == null)
            return false;

        Action action = avTransService.getAction(AVTransport.STOP);
        if (action == null)
            return false;

        action.setArgumentValue(AVTransport.INSTANCEID, "0");

        return action.postControlAction();
    }

    public boolean play(
            Device dev,
            ItemNode itemNode)
    {
        stop(dev);
        if (setAVTransportURI(dev, itemNode) == false)
            return false;
        return play(dev);
    }
}
