package com.iview.android.connectionclient.model.upnp;

public interface IUpnpDevice {

	public String getFriendlyName();

	public String getManufacturer();

	public String getManufacturerURL();

	public String getModelName();

	public String getModelDesc();

	public String getModelNumber();

	public String getModelURL();

	public String getXMLURL();

	public String getPresentationURL();

	public String getSerialNumber();

	public String getUDN();

	public boolean equals(IUpnpDevice otherDevice);

	public String getUUID();

	public void printService();

	public String getDeviceType();

	@Override
	public String toString();
}
