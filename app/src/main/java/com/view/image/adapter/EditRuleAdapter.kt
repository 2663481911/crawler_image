package com.view.image.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.view.image.R
import com.view.image.model.AdjustRuleViewModel
import com.view.image.model.Rule


class EditRuleAdapter(
    private val adjustRuleViewModel: AdjustRuleViewModel,
    val ruleList: List<Rule>,
) :
    RecyclerView.Adapter<EditRuleAdapter.EditRuleHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditRuleHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_rule, parent, false)
        return EditRuleHolder(itemView)
    }

    override fun onBindViewHolder(holder: EditRuleHolder, position: Int) {
        val nameTextView: TextView = holder.itemView.findViewById(R.id.edit_rule_name)
        val editView = holder.itemView.findViewById<ImageButton>(R.id.edit_image_view)
        val remoteView = holder.itemView.findViewById<ImageButton>(R.id.remove_image_view)
        val toTopView = holder.itemView.findViewById<ImageButton>(R.id.top_image_view)

        remoteView.setOnClickListener {
            adjustRuleViewModel.removeRule(position)
        }

        editView.setOnClickListener {
            adjustRuleViewModel.editRule(position)
        }

        toTopView.setOnClickListener {
            adjustRuleViewModel.ruleToTop(position)
        }
        nameTextView.text = ruleList[position].sourceName
    }


    class EditRuleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int {
        return ruleList.size
    }

}





