/** RecyclerViewAdapter
 * ScanActivity의 RecyclerView에서 사용될 RecyclerViewAdapter
 * @author Kyujin Cho
 * @version 1.0
 * @see com.hanyang.bpreader.ScanActivity
 */

package com.hanyang.bpreader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {
    Context mContext;
    ArrayList<Data> mData;
    LayoutInflater mInflater;

    /**
     * 클래스 생성자. Activity의 Context와 RecyclerView에서 사용될 데이터 집합체를 전달받음
     * @param mContext Activity Context
     * @param mData 데이터 셋
     */
    public RecyclerViewAdapter(Context mContext, ArrayList<Data> mData) {
        this.mContext = mContext; // Context를 전달받아 클래스 전역변수에 저장
        this.mData = mData; // Data Set을 전달받아 클래스 전역변수에 저장
        mInflater = LayoutInflater.from(mContext); // 전달받은 context로 LayoutInflater 생성
    }

    /**
     * onCreateViewHoler를 통해 inflate된 viewholder의 UI Component들에 text를 설정하고, onClickListener를 달아주는 메소드
     * @param holder UI Component를 수정할 ViewHolder
     * @param position 해당 View가 List의 몇 번째 위치에 있는지 알려주는 위치 변수
     */
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.nameView.setText(mData.get(position).getName()); // ViewHolder에서 항목의 이름을 보여줄 UI Component에 텍스트를 설정해줌
        holder.contentView.setText(mData.get(position).getContent()); // ViewHolder에서 항목의 내용을 보여줄 UI Component에 텍스트를 설정해줌
    }

    /**
     * XML Layout을 가져와 ViewHolder 형태로 inflate 시켜서 돌려주는 메소드
     * @param parent ViewGroup
     * @param viewType ViewType
     * @return Inflate된 ViewHolder
     */
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(mInflater.inflate(R.layout.pass_element_card, parent, false));
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
     * RecyclerViewHolder
     * RecyclerView에서 표시될 Element의 View를 정의하는 Class
     */
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
