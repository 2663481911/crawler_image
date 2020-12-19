package com.view.image.prompt

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import com.view.image.R

object PromptBox {

    //弹出输入框
    private fun inputDialog(context: Context) {
        val inputServer = EditText(context)
        inputServer.isFocusable = true
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle(context.resources.getString(R.string.net_add_rule_title))
//           .setIcon(R.drawable.save)
            .setView(inputServer)
            .setNegativeButton(context.resources.getString(R.string.net_add_rule_cancel), null)
            .setPositiveButton(context.resources.getString(R.string.net_add_rule_ok)) { _, _ ->
                val inputText = inputServer.text.toString()
            }
        builder.show()
    }


    // 删除提示框
    fun removeDialog(context: Context, dialogCall: DialogCall) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle(context.resources.getString(R.string.remove_select_rule))
            .setNegativeButton(context.resources.getString(R.string.net_add_rule_cancel)) { _, _ ->
                dialogCall.cancel()
            }
            .setPositiveButton(context.resources.getString(R.string.net_add_rule_ok)) { _, _ ->
                dialogCall.positiveButton()
            }
        builder.show()
    }


    interface DialogCall {
        fun positiveButton()
        fun cancel()
    }


}