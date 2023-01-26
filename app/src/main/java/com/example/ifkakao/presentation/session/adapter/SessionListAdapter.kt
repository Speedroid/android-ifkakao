package com.example.ifkakao.presentation.session.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ifkakao.R
import com.example.ifkakao.databinding.ItemSessionBinding
import com.example.ifkakao.domain.model.Info

class SessionListAdapter : ListAdapter<Info, SessionListAdapter.ViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Info>() {
            override fun areItemsTheSame(
                oldItem: Info,
                newItem: Info
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Info,
                newItem: Info
            ): Boolean = oldItem == newItem
        }
    }

    class ViewHolder(val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                // TODO
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
        return ViewHolder(ItemSessionBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sessionDateText.text = currentList[position].sessionDate.toString()
        holder.binding.sessionCompanyText.text = currentList[position].company
        holder.binding.sessionTitle.text = currentList[position].title
        holder.binding.sessionTypeText.text = currentList[position].sessionType
    }

    override fun getItemCount(): Int = currentList.size
}