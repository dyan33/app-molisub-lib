package com.mos.lib.common.http

class SubProxy(val host: String, val username: String, val password: String, val port: Int) {


    override fun toString(): String {
        return "SubProxy{" +
                "host='" + host + '\''.toString() +
                ", username='" + username + '\''.toString() +
                ", password='" + password + '\''.toString() +
                ", port=" + port +
                '}'.toString()
    }
}
