/**
 * CodesRecyclerViewAdapter
 * MainActivity의 RecyclerView에서 쓰일 RecyclerViewAdapter
 * @author Kyujin Cho
 * @version 1.0
 * @see com.hanyang.bpreader.MainActivity
 */

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


public class CodesRecyclerViewAdapter extends FixedRecyclerView.Adapter<CodesRecyclerViewAdapter.CodesRecyclerViewHolder>{
    Context mContext;
    ArrayList<ListData> mData;
    LayoutInflater mInflater;

    /**
     * 클래스 생성자. Activity의 Context와 RecyclerView에서 사용될 데이터 집합체를 전달받음
     * @param context Activity Context
     * @param data 데이터 셋
     */
    public CodesRecyclerViewAdapter(Context context, ArrayList<ListData> data) {
        mContext = context; // Context를 전달받아 클래스 전역변수에 저장
        mData = data; // Data Set을 전달받아 클래스 전역변수에 저장
        mInflater = LayoutInflater.from(mContext); // 전달받은 context로 LayoutInflater 생성
    }

    /**
     * onCreateViewHoler를 통해 inflate된 viewholder의 UI Component들에 text를 설정하고, onClickListener를 달아주는 메소드
     * @param holder UI Component를 수정할 ViewHolder
     * @param position 해당 View가 List의 몇 번째 위치에 있는지 알려주는 위치 변수
     */

    @Override
    public void onBindViewHolder(CodesRecyclerViewHolder holder, int position) {
        holder.nameView.setText(mData.get(position).getAirline() + " " +  mData.get(position).getCode()); // ViewHolder에서 항목의 이름을 보여줄 UI Component에 텍스트 설정
        holder.contentView.setText(mData.get(position).getDepart() + " -> " + mData.get(position).getArrival()); // ViewHolder에서 비행 경로를 보여줄 UI Component에 텍스트 설정
        final int f_position = position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 아이템이 터치되었을 때
                Intent intent = new Intent(mContext, ScanActivity.class); // 상세내용을 보여주기 위해 ScanActivity로 이동 준비
                intent.putExtra("prev", true);  // 이 data가 이미 등록된 데이터임을 전달해서 추가로 db에 등록되지 않도록 하기
                intent.putExtra("data", mData.get(f_position).getRawdata()); // Parse할 String 전달
                mContext.startActivity(intent); // 액티비티 시작
            }
        });
        holder.view.setLongClickable(true);
    }

    /**
     * XML Layout을 가져와 ViewHolder 형태로 inflate 시켜서 돌려주는 메소드
     * @param parent ViewGroup
     * @param viewType ViewType
     * @return Inflate된 ViewHolder
     */
    @Override
    public CodesRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CodesRecyclerViewHolder(mInflater.inflate(R.layout.pass_element_card, parent, false)); // pass_element_card Layout을 inflate시켜서 리턴
    }

    /**
     * RecyclerView에서 사용할 Data Set의 크기를 전달해주는 메소드
     * @return mData의 크기
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * CodesRecyclerViewHolder
     * RecyclerView에서 표시될 Element의 View를 정의하는 Class
     */
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
