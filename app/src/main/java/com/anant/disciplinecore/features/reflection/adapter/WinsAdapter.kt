package com.anant.disciplinecore.features.reflection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anant.disciplinecore.data.local.entities.DailyWin
import com.anant.disciplinecore.databinding.ItemWinBinding

class WinsAdapter(
    private var wins: List<DailyWin>
) : RecyclerView.Adapter<WinsAdapter.WinViewHolder>() {

    inner class WinViewHolder(
        val binding: ItemWinBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WinViewHolder {

        val binding = ItemWinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return WinViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WinViewHolder,
        position: Int
    ) {

        holder.binding.tvWinText.text =
            wins[position].text
    }

    override fun getItemCount() = wins.size

    fun updateWins(newWins: List<DailyWin>) {
        wins = newWins
        notifyDataSetChanged()
    }
}