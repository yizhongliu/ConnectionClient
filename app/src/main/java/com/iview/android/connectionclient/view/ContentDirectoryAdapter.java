package com.iview.android.connectionclient.view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iview.android.connectionclient.R;
import com.iview.android.connectionclient.model.upnp.didl.DIDLObjectDisplay;

import java.util.List;

public class ContentDirectoryAdapter extends RecyclerView.Adapter<ContentDirectoryAdapter.ViewHolder>  {
    private final static String TAG = "ContentDirectoryAdapter";

    private List<DIDLObjectDisplay> mDIDLObjectDisplayList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView text1;
        TextView text2;
        TextView text3;
        RelativeLayout contentRelativeLayout;

        public ViewHolder(View view) {

            super(view);
            // Item
            contentRelativeLayout = (RelativeLayout) view.findViewById(R.id.custom_list_item);
            imageView = (ImageView) view.findViewById(R.id.icon);
            text1 = (TextView) view.findViewById(R.id.text1);
            text2 = (TextView) view.findViewById(R.id.text2);
            text3 = (TextView) view.findViewById(R.id.text3);
            Log.e(TAG, "ViewHolder");
        }
    }

    public ContentDirectoryAdapter(List<DIDLObjectDisplay> devicelist) {
        Log.e(TAG, "ContentDeviceAdapter");
        mDIDLObjectDisplayList = devicelist;
        Log.e(TAG, "device size :" + mDIDLObjectDisplayList.size());
    }

    @Override
    public  ContentDirectoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder:" + mDIDLObjectDisplayList.size());
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.browsing_list_item, parent, false);
        final ContentDirectoryAdapter.ViewHolder holder = new ContentDirectoryAdapter.ViewHolder(view);
        holder.contentRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                DIDLObjectDisplay didlObjectDisplay = mDIDLObjectDisplayList.get(position);
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, didlObjectDisplay);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder:" + position);
        DIDLObjectDisplay didlObjectDisplay = mDIDLObjectDisplayList.get(position);

        holder.imageView.setImageResource(didlObjectDisplay.getIcon());
        holder.text1.setText(didlObjectDisplay.getTitle());
        holder.text2.setText((didlObjectDisplay.getDescription()!=null) ? didlObjectDisplay.getDescription() : "");
        holder.text3.setText(didlObjectDisplay.getCount());
    }

    @Override
    public int getItemCount() {
     //   Log.e(TAG, "getItemCount:" + mDIDLObjectDisplayList.size());
        return mDIDLObjectDisplayList == null ? 0 : mDIDLObjectDisplayList.size();
    }

    public interface OnItemClickListener {
        /**
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        public void OnItemClick(View view, DIDLObjectDisplay data);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
