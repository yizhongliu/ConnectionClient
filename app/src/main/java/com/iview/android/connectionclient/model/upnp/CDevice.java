package com.iview.android.connectionclient.model.upnp;

import android.util.Log;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.ActionList;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;

@SuppressWarnings("rawtypes")
public class CDevice implements IUpnpDevice {

	private static final String TAG = "CDevice";

	Device device;

	public CDevice(Device device)
	{
		this.device = device;
	}

	public Device getDevice()
	{
		return device;
	}

	@Override
	public String getFriendlyName()
	{
		return device.getFriendlyName();
	}

	@Override
	public boolean equals(IUpnpDevice otherDevice)
	{
		return device.getUDN().equals(((CDevice) otherDevice).getDevice().getUDN());
	}

	@Override
	public String getUUID()
	{
		return device.getUUID();
	}

	@Override
	public void printService()
	{
		ServiceList serviceList = device.getServiceList();
		for (int i = 0; i < serviceList.size(); i++) {
			Service service = serviceList.getService(i);
			Log.d(TAG, "\t Service :" + service);
			ActionList actionList = service.getActionList();
			for (int j = 0; j < actionList.size(); j++) {
				Action action = actionList.getAction(i);
				Log.i(TAG, "\t\t Action : " + action);
			}
		}
	}

	@Override
	public String getManufacturer()
	{
		return device.getManufacture();
	}

	@Override
	public String getManufacturerURL()
	{
		return device.getManufactureURL();
	}

	@Override
	public String getModelName()
	{
		return device.getModelName();
	}

	@Override
	public String getModelDesc()
	{
		return device.getModelDescription();
	}

	@Override
	public String getModelNumber()
	{
		return device.getModelNumber();
	}

	@Override
	public String getModelURL()
	{
		return device.getModelURL();
	}

	@Override
	public String getXMLURL()
	{
		return device.getURLBase();
	}

	@Override
	public String getPresentationURL()
	{
		return device.getPresentationURL();
	}

	@Override
	public String getSerialNumber()
	{
		return device.getSerialNumber();
	}

	@Override
	public String getUDN()
	{
		return device.getUDN();
	}

	@Override
	public String getDeviceType() {
		return device.getDeviceType();
	}
}
