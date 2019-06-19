package com.bitatron.statestream.logger

import android.util.Log

private const val TAG = "SNAZZY"

class AndroidLogger : Logger {

    override fun d(caller: Any, message: String) {
        Log.d(getNameOfCaller(caller), String.format("%1\$s - %2\$s", TAG, message))
    }

    override fun e(caller: Any, message: String) {
        Log.e(getNameOfCaller(caller), message)
    }

    override fun e(caller: Any, throwable: Throwable) {
        throwable.printStackTrace()
        Log.e(getNameOfCaller(caller), String.format("%1\$s - %2\$s", TAG, getMessage(throwable)))
    }

    private fun getNameOfCaller(caller: Any): String {
        return caller.javaClass.simpleName
    }

    private fun getMessage(throwable: Throwable?): String {
        return throwable?.message.orEmpty()
    }

}
