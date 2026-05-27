package com.anant.disciplinecore.features.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anant.disciplinecore.databinding.ItemInsightBinding
import com.anant.disciplinecore.features.home.insights.InsightManager

class InsightsAdapter :
    ListAdapter<
            InsightManager.Insight,
            InsightsAdapter.InsightViewHolder
            >(
        object : DiffUtil.ItemCallback<InsightManager.Insight>() {

            override fun areItemsTheSame(
                oldItem: InsightManager.Insight,
                newItem: InsightManager.Insight
            ): Boolean {

                return oldItem.message ==
                        newItem.message
            }

            override fun areContentsTheSame(
                oldItem: InsightManager.Insight,
                newItem: InsightManager.Insight
            ): Boolean {

                return oldItem == newItem
            }
        }
    ) {

    inner class InsightViewHolder(
        private val binding: ItemInsightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            insight: InsightManager.Insight
        ) {

            binding.tvInsightEmoji.text =
                insight.emoji

            binding.tvInsightMessage.text =
                insight.message
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InsightViewHolder {

        val binding =
            ItemInsightBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return InsightViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: InsightViewHolder,
        position: Int
    ) {

        holder.bind(getItem(position))
    }
}