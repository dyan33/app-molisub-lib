package com.mos.lib.common.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings

object PermissionUtils {

    var alertDialog: AlertDialog? = null
    fun initDialog(context: Context?):AlertDialog? {
        if(alertDialog == null){
            alertDialog = AlertDialog.Builder(context)
                    .setMessage("Notification permissions have been disabled")
                    .setPositiveButton("Setting") { _, _ ->
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context?.startActivity(intent)
                    }
                    .setCancelable(false)
                    .create()
        }else{
            return alertDialog
        }
        return alertDialog
    }

    fun showing() {
        alertDialog?.show()
    }

    fun isShow(): Boolean? {
        return alertDialog?.isShowing
    }
}