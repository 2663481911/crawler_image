package com.view.image.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.view.image.R
import com.view.image.analyzeRule.Rule
import com.view.image.model.ManageRuleViewModel


class ManageRuleAdapter(
    private val manageRuleViewModel: ManageRuleViewModel,
    private val ruleList: ArrayList<Rule>,
) :
    RecyclerView.Adapter<ManageRuleAdapter.ManageRuleHolder>() {
    private val lock = Object()
    private val checkboxMap: HashMap<Int, Boolean> = HashMap()

    inner class ManageRuleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.edit_rule_name)
        val editView: ImageButton = itemView.findViewById(R.id.edit_image_view)
        val remoteView: ImageButton = itemView.findViewById(R.id.remove_image_view)
        val toTopView: ImageButton = itemView.findViewById(R.id.top_image_view)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageRuleHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_manage_rule, parent, false)
        return ManageRuleHolder(itemView)
    }


    override fun onBindViewHolder(holder: ManageRuleHolder, position: Int) {

        holder.remoteView.setOnClickListener {
            removeItem(position)
            manageRuleViewModel.removeRule(position)
        }

        holder.editView.setOnClickListener {
            manageRuleViewModel.editRule(position)
        }

        holder.toTopView.setOnClickListener {
            val rule = ruleList[position]
            removeItem(position)
            addItem(0, rule)
            manageRuleViewModel.ruleToTop(position)
        }
        holder.nameTextView.text = ruleList[position].sourceName

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            checkboxMap[position] = isChecked
        }
    }

    fun getCheckboxMap(): HashMap<Int, Boolean> {
        return checkboxMap
    }

    override fun getItemCount(): Int {
        return ruleList.size
    }

    fun updateItem(rule: Rule, position: Int) {
        synchronized(lock) {
            ruleList[position] = rule
            notifyItemChanged(position)
        }
    }

    //添加数据
    private fun addItem(position: Int, rule: Rule) {
        synchronized(lock) {
            Log.d("pos", position.toString())
            ruleList.add(position, rule)
            notifyItemInserted(position)
            notifyItemRangeChanged(position, ruleList.size - position)
        }
    }

    // 删除数据
    private fun removeItem(position: Int) {
        synchronized(lock) {
            ruleList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, ruleList.size)
        }
    }


}





