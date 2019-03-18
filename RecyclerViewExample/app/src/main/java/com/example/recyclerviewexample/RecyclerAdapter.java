package com.example.recyclerviewexample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    ArrayList<RecyclerItem> mItems;
    Context itemContext;

    public RecyclerAdapter(ArrayList<RecyclerItem> items){
        setItems(items);
    }

    public void setItems(ArrayList<RecyclerItem> items){
        mItems = items;
    }


    // 새로운 뷰 홀더 생성
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view, parent, false);
        itemContext = parent.getContext();
        return new ItemViewHolder(view);
    }


    // View 의 내용을 해당 포지션의 데이터로 바꿉니다.
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        holder.mNameTv.setText(mItems.get(position).getName());
        holder.itemImage.setImageResource(R.drawable.man);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(itemContext, mItems.get(position).getName() + "의 전체를 클릭", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItems.get(position).switchMan();

                if(mItems.get(position).isMan()) {
                    Toast.makeText(itemContext, mItems.get(position).getName() + "의 남자를 비활성화", Toast.LENGTH_SHORT).show();
                    holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground);
                }
                else {
                    Toast.makeText(itemContext, mItems.get(position).getName() + "의 남자를 활성화", Toast.LENGTH_SHORT).show();
                    holder.itemImage.setImageResource(R.drawable.man);
                }
            }
        });
    }

    // 데이터 셋의 크기를 리턴해줍니다.
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // 커스텀 뷰홀더
// item layout 에 존재하는 위젯들을 바인딩합니다.
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv;
        private ImageView itemImage;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.itemNameTv);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }
}