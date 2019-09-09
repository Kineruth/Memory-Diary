package com.memoryDiary.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.memoryDiary.Activity.Memory.ShowTaggedActivity;
import com.memoryDiary.Entity.Memory;
import com.memoryDiary.Entity.Tags;
import com.memoryDiary.Holder.MemoryDataHolder;
import com.memoryDiary.R;
import com.squareup.picasso.Picasso;

public class TagsAdapter extends RecyclerView.Adapter <TagsAdapter.MemoryViewHolder> {

    private Context mContext;
    private Tags memories;

    public TagsAdapter(Context mContext, Tags mData) {
        this.mContext = mContext;
        this.memories = mData;
    }

    @NonNull
    @Override
    public TagsAdapter.MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /* get the inflator */
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        /* inflate the xml which gives us a view */
        View view = mInflater.inflate(R.layout.memory_cardview_item, null);
        TagsAdapter.MemoryViewHolder holder = new TagsAdapter.MemoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagsAdapter.MemoryViewHolder memoryViewHolder, int i) {
        memoryViewHolder.txv_memory_title.setText(memories.getMemory(i).getMemoryTitle());
//        myViewHolder.img_memory_thumbnail.setImageResource(mData.get(i).getImagePath());
        //maybe need to change and do picaso & on click & if empty

        final Memory memory = memories.getMemory(i);
        memoryViewHolder.txv_memory_title.setText(memory.getMemoryTitle());
        if(!memory.getImagePath().isEmpty())
            Picasso.get().load(memory.getImagePath()).into(memoryViewHolder.img_memory_thumbnail);
        else
            Picasso.get().load(R.drawable.forgot_photo).into(memoryViewHolder.img_memory_thumbnail);
        memoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemoryDataHolder.getMemoryDataHolder().getMemory().setAll(memory);
                Intent intent = new Intent(mContext, ShowTaggedActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memories.getAmount();
    }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder{
        TextView txv_memory_title;
        ImageView img_memory_thumbnail;

        public MemoryViewHolder(View itemView){
            super(itemView);
            txv_memory_title = itemView.findViewById(R.id.memory_title_id);
            img_memory_thumbnail = itemView.findViewById(R.id.memory_img_id);
        }

        public void bind(Memory m){
            txv_memory_title.setText(m.getMemoryTitle());
        }
    }


}

