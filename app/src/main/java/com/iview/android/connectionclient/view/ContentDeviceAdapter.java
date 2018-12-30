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

public class ContentDeviceAdapter extends RecyclerView.Adapter<ContentDeviceAdapter.ViewHolder> {
    private final static String TAG = "DeviceAdapter";

    private List<DeviceDisplay> mDeviceDisplayList;

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
                CheckedTextView checkedTextView = (CheckedTextView) v;
                checkedTextView.toggle();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder:" + position);
        DeviceDisplay deviceDisplay = mDeviceDisplayList.get(position);
        holder.mDeviceView.setText(deviceDisplay.getDeviceName());
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount:" + mDeviceDisplayList.size());
        return mDeviceDisplayList == null ? 0 : mDeviceDisplayList.size();
    }

    public void addData(int position, DeviceDisplay deviceDisplay) {
        Log.e(TAG, "addData :" + deviceDisplay.getDeviceName() +",position:" + position + ", size:" + mDeviceDisplayList.size());
        mDeviceDisplayList.add(position, deviceDisplay);
        notifyItemInserted(position );
        notifyItemRangeChanged(position, mDeviceDisplayList.size() - position);
    }

    public void removeData(int position) {
        Log.e(TAG, "removeData size:" + mDeviceDisplayList.size());
        mDeviceDisplayList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
}
