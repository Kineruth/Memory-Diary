package com.memoryDiary.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.memoryDiary.Entity.Memory;
import com.memoryDiary.R;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MyViewHolder> {

private Context mContext;
private List<Memory> mData;

    public MemoryAdapter(Context mContext, List<Memory> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* get the inflator */
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        /* inflate the xml which gives us a view */
        View view = mInflater.inflate(R.layout.memory_cardview_item, null);
//        MemoryAdapter.MemoryViewHolder holder = new MemoryAdapter.MemoryViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txv_memory_title.setText(mData.get(i).getMemoryTitle());
//        myViewHolder.img_memory_thumbnail.setImageResource(mData.get(i).getImage());
                //maybe need to change and do picaso & on click & if empty
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txv_memory_title;
        ImageView img_memory_thumbnail;

        public MyViewHolder(View itemView){
            super(itemView);
            txv_memory_title = (TextView) itemView.findViewById(R.id.memory_title_id);
            img_memory_thumbnail = (ImageView) itemView.findViewById(R.id.memory_img_id);
        }
    }
}

