package com.view.image.model

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions

object MyPermissions {
    fun shouldAskPermissions(): Boolean {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    fun askPermissions(activity: Activity) {
        val permissions = listOf("android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE")
        val requestCode = 200;
        requestPermissions(activity, permissions.toTypedArray(), requestCode);
    }

}