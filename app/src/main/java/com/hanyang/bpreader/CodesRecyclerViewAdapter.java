package com.hanyang.bpreader;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kyujin on 14/12/2016.
 */

public class CodesRecyclerViewAdapter extends RecyclerView.Adapter<CodesRecyclerViewAdapter.CodesRecyclerViewHolder>{
    Context mContext;
    ArrayList<ListData> mData;
    LayoutInflater mInflater;

    public CodesRecyclerViewAdapter(Context context, ArrayList<ListData> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onBindViewHolder(CodesRecyclerViewHolder holder, int position) {
        holder.nameView.setText(mData.get(position).getAirline() + " " +  mData.get(position).getCode());
        holder.contentView.setText(mData.get(position).getDepart() + " -> " + mData.get(position).getArrival());
        final int f_position = position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ScanActivity.class);
                intent.putExtra("prev", true);
                Log.i("CodesRecyclerView", mData.get(f_position).getRawdata());
                intent.putExtra("data", mData.get(f_position).getRawdata());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public CodesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CodesRecyclerViewHolder(mInflater.inflate(R.layout.pass_element_card, parent, false));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class CodesRecyclerViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView nameView;
        TextView contentView;

        public CodesRecyclerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            nameView = (TextView) itemView.findViewById(R.id.nameView);
            contentView = (TextView) itemView.findViewById(R.id.contentView);
        }
    }
}
