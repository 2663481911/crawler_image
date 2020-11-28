package com.binding.image.model

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class VolleySingleton {
    companion object {
        private var INSTANCE: VolleySingleton? = null

        fun getRequestQueue(context: Context): RequestQueue {
            INSTANCE ?: synchronized(this) {
                VolleySingleton().also { INSTANCE = it }
            }
            return Volley.newRequestQueue(context)
        }
    }


}