package com.example.ifkakao.presentation.session.select.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ifkakao.R
import com.example.ifkakao.databinding.ItemSessionBinding
import com.example.ifkakao.domain.model.Info
import java.util.*

class SessionSelectListAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<Info, SessionSelectListAdapter.ViewHolder>(diffUtil) {
    private var day = 0

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

    class ViewHolder(
        val onItemClick: (Int) -> Unit,
        val binding: ItemSessionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
        return ViewHolder(onItemClick, ItemSessionBinding.bind(view))
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.sessionDateText.text = currentList[position].sessionDate
        holder.binding.sessionTimeText.text = currentList[position].sessionTime
        holder.binding.sessionCompanyText.text = currentList[position].company
        holder.binding.sessionTitle.text = currentList[position].title

        val trackString = currentList[position].track.joinToString(separator = "  ")
        val trackText = currentList[position].sessionType + "  " + trackString
        holder.binding.sessionTrackText.text = trackText

        if (day == 0) {
            holder.binding.sessionDateText.visibility = VISIBLE
            holder.binding.sessionTimeText.visibility = GONE
        } else {
            holder.binding.sessionDateText.visibility = GONE
            holder.binding.sessionTimeText.visibility = VISIBLE
        }
    }

    override fun getItemCount(): Int = currentList.size

    fun changeDay(day: Int) {
        this.day = day
        notifyItemRangeChanged(0, itemCount)
    }
}