package com.bitatron.statestream.logger

interface Logger {

    fun e(caller: Any, message: String)

    fun e(caller: Any, throwable: Throwable)

    fun d(caller: Any, message: String)

}
