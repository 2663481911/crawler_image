package com.view.image.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.view.image.R
import com.view.image.model.ManageRuleViewModel
import com.view.image.model.Rule


class ManageRuleAdapter(
    private val manageRuleViewModel: ManageRuleViewModel,
    private val ruleList: List<Rule>,
) :
    RecyclerView.Adapter<ManageRuleAdapter.ManageRuleHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageRuleHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_rule, parent, false)
        return ManageRuleHolder(itemView)
    }


    override fun onBindViewHolder(holder: ManageRuleHolder, position: Int) {

        holder.remoteView.setOnClickListener {
            manageRuleViewModel.removeRule(position)
        }

        holder.editView.setOnClickListener {
            manageRuleViewModel.editRule(position)
        }

        holder.toTopView.setOnClickListener {
            manageRuleViewModel.ruleToTop(position)
        }
        holder.nameTextView.text = ruleList[position].sourceName
    }


    inner class ManageRuleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.edit_rule_name)
        val editView: ImageButton = itemView.findViewById(R.id.edit_image_view)
        val remoteView: ImageButton = itemView.findViewById(R.id.remove_image_view)
        val toTopView: ImageButton = itemView.findViewById(R.id.top_image_view)
    }

    override fun getItemCount(): Int {
        return ruleList.size
    }

}





