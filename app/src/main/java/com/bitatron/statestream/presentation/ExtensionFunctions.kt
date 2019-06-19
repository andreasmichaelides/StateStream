package com.bitatron.statestream.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bitatron.statestream.logger.Logger

fun Context.openAppInPlaystore(packageName: String, logger: Logger) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("market://details?id=$packageName")
        startActivity(intent)
    } catch (throwable: Throwable) {
        logger.e(this, throwable)
    }
}