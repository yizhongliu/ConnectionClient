package com.iview.android.connectionclient.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.iview.android.connectionclient.R;
import com.iview.android.connectionclient.model.DeviceDisplay;

import java.util.List;

public class RendererAdapter extends RecyclerView.Adapter<RendererAdapter.ViewHolder> {
    private final static String TAG = "DeviceAdapter";

    private List<DeviceDisplay> mDeviceDisplay;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView mDeviceView;

        public ViewHolder(View view) {

            super(view);
            mDeviceView = view.findViewById(R.id.checkboxTextView);
            Log.e(TAG, "ViewHolder");
        }
    }

    public RendererAdapter(List<DeviceDisplay> devicelist) {
        Log.e(TAG, "ContentDeviceAdapter");
        mDeviceDisplay = devicelist;
        Log.e(TAG, "device size :" + mDeviceDisplay.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder:" + mDeviceDisplay.size());
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder:" + position);
        DeviceDisplay deviceDisplay = mDeviceDisplay.get(position);
        holder.mDeviceView.setText(deviceDisplay.getDeviceName());
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount:" + mDeviceDisplay.size());
        return mDeviceDisplay == null ? 0 : mDeviceDisplay.size();
    }

    public void addData(int position, DeviceDisplay deviceDisplay) {
        Log.e(TAG, "addData :" + deviceDisplay.getDeviceName() +",position:" + position + ", size:" + mDeviceDisplay.size());
        mDeviceDisplay.add(position, deviceDisplay);
        notifyItemInserted(position );
        notifyItemRangeChanged(position,mDeviceDisplay.size() - position);
    }

    public void removeData(int position) {
        Log.e(TAG, "removeData size:" + mDeviceDisplay.size());
        mDeviceDisplay.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
}
