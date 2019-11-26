package com.cp.log.event

interface LogEvent {

    fun onMessage(tag: String, content: String)

    fun onError(throwable: Throwable)


}
