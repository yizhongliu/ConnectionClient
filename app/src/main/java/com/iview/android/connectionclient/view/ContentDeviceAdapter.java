package com.iview.android.connectionclient.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.iview.android.connectionclient.ControlPointActivity;
import com.iview.android.connectionclient.R;
import com.iview.android.connectionclient.model.upnp.IUpnpDevice;

import java.util.List;

public class ContentDeviceAdapter extends RecyclerView.Adapter<ContentDeviceAdapter.ViewHolder> {
    private final static String TAG = "DeviceAdapter";

    private List<DeviceDisplay> mDeviceDisplayList;

    private int selected = -1;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView mDeviceView;

        public ViewHolder(View view) {

            super(view);
            mDeviceView = view.findViewById(R.id.checkboxTextView);
            Log.e(TAG, "ViewHolder");
        }
    }

    public ContentDeviceAdapter(List<DeviceDisplay> devicelist) {
        Log.e(TAG, "ContentDeviceAdapter");
        mDeviceDisplayList = devicelist;
        Log.e(TAG, "device size :" + mDeviceDisplayList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder:" + mDeviceDisplayList.size());
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mDeviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DeviceDisplay deviceDisplay = mDeviceDisplayList.get(position);
                selected = position;
                notifyDataSetChanged();

                select(deviceDisplay.getDevice());  //Notify  device select
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder:" + position);
        DeviceDisplay deviceDisplay = mDeviceDisplayList.get(position);
        holder.mDeviceView.setText(deviceDisplay.getDeviceName());

        if (selected == position) {
            holder.mDeviceView.setChecked(true);
        } else {
            holder.mDeviceView.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount:" + mDeviceDisplayList.size());
        return mDeviceDisplayList == null ? 0 : mDeviceDisplayList.size();
    }

    protected void select(IUpnpDevice device)
    {
        select(device, false);
    }

    protected void select(IUpnpDevice device, boolean force)
    {
        ControlPointActivity.mUpnpServiceController.setSelectedContentDirectory(device, force);
    }
}
