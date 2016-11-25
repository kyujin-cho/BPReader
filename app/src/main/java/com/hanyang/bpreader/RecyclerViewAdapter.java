package com.hanyang.bpreader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyujin on 2016-08-18.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    Context mContext;
    ArrayList<Data> mData;
    LayoutInflater mInflater;

    public RecyclerViewAdapter(Context mContext, ArrayList<Data> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nameView.setText(mData.get(position).getName());
        holder.contentView.setText(mData.get(position).getContent());
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(mInflater.inflate(R.layout.pass_element_card, parent, false));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView contentView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.nameView);
            contentView = (TextView) itemView.findViewById(R.id.contentView);

        }
    }
}
