package com.memoryDiary.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.memoryDiary.Activity.Memory.ShowMemoryActivity
import com.memoryDiary.Entity.Memory
import com.memoryDiary.Holder.MemoryDataHolder
import com.memoryDiary.Holder.UserDataHolder
import com.memoryDiary.R
import com.squareup.picasso.Picasso

class MemoryAdapter : PagedListAdapter<Memory, DiaryAdapter.MemoryViewHolder>(MemoryAdapter) {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryAdapter.MemoryViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.memory_cardview_item, parent, false)

        return DiaryAdapter.MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryAdapter.MemoryViewHolder, position: Int) {
        val memory = getItem(position).
        if(!memory!!.userId.equals(UserDataHolder.getUserDataHolder().user.uid))
            return
        holder.txv_memory_title.setText(memory!!.getMemoryTitle())
        if (!memory.getImagePath().isEmpty())
            Picasso.get().load(memory.getImagePath()).into(holder.img_memory_thumbnail)
        else
            Picasso.get().load(R.drawable.forgot_photo).into(holder.img_memory_thumbnail)
        holder.itemView.setOnClickListener(View.OnClickListener {
            MemoryDataHolder.getMemoryDataHolder().memory.setAll(memory)
            val intent = Intent(context, ShowMemoryActivity::class.java)
            context!!.startActivity(intent)
        })

        if (memory != null) holder.bind(memory)
    }

    companion object : DiffUtil.ItemCallback<Memory>() {

        override fun areItemsTheSame(
                oldItem: Memory,
                newItem: Memory
        ): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(
                oldItem: Memory,
                newItem: Memory
        ): Boolean {
            return oldItem.memoryTitle == newItem.memoryTitle
        }
    }
}