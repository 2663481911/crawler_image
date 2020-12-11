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

    override fun getItemCount(): Int {
        return ruleList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageRuleHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_manage_rule, parent, false)
        return ManageRuleHolder(itemView)
    }


    override fun onBindViewHolder(holder: ManageRuleHolder, position: Int) {

        // 删除规则
        holder.remoteView.setOnClickListener {
            removeRule(position)
        }

        // 编辑规则
        holder.editView.setOnClickListener {
            manageRuleViewModel.editRule(position)
        }

        // 置顶
        holder.toTopView.setOnClickListener {
            toTopRule(position)
        }

        holder.nameTextView.text = ruleList[position].sourceName

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            (checkboxMap.containsKey(position)).also { checkboxMap.remove(position) }
            if (isChecked) checkboxMap[position] = isChecked
        }

        (checkboxMap.containsKey(position)).also { holder.checkbox.isChecked = it }
    }

    // 改变选中的位置，用于置顶，删除后选中position不乱
    private fun changeCheckboxKeys(position: Int, isToTop: Boolean = false) {
        val keys = checkboxMap.keys.toList()
        for (pos in keys) {
            when {
                pos > position ->
                    if (!isToTop) {
                        checkboxMap.remove(pos)
                        checkboxMap[pos - 1] = true
                    }

                position == pos -> {
                    if (!isToTop) checkboxMap.remove(position)
                    else {
                        checkboxMap[0] = true
                        if (pos - 1 !in keys) {
                            checkboxMap.remove(pos)
                        }
                    }
                }

                pos < position -> {
                    if (isToTop) {
                        if (pos - 1 !in keys) {
                            checkboxMap.remove(pos)
                        }
                        checkboxMap[pos + 1] = true
                    }
                }
            }


        }

    }

    fun getCheckboxMap(): HashMap<Int, Boolean> {
        return checkboxMap
    }

    fun removeRuleList() {
        val keys = checkboxMap.keys.toList().reversed()
        for (pos in keys) {
            removeRule(pos)
        }
        setCheckboxNor()


    }

    // 删除规则
    private fun removeRule(position: Int) {
        removeItem(position)
        manageRuleViewModel.changRuleListVale()
        changeCheckboxKeys(position)
    }

    private fun toTopRule(position: Int) {
        val rule = ruleList[position]
        removeItem(position)
        addItem(0, rule)
        manageRuleViewModel.changRuleListVale()
        changeCheckboxKeys(position, true)
    }

    // 全不选
    fun setCheckboxNor() {
        checkboxMap.clear()
        notifyDataSetChanged()
    }

    // 全选
    fun setCheckboxAll() {
        for (i in ruleList.indices) {
            checkboxMap[i] = true
        }
        notifyDataSetChanged()
    }

    // 编辑规则后更新item
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





