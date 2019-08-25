package com.memoryDiary.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.memoryDiary.Entity.Memory
import com.memoryDiary.R

class MemoryAdapter : PagedListAdapter<Memory, DiaryAdapter.MemoryViewHolder>(MemoryAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryAdapter.MemoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memory_cardview_item, parent, false)

        return DiaryAdapter.MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryAdapter.MemoryViewHolder, position: Int) {
        val memory = getItem(position)

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