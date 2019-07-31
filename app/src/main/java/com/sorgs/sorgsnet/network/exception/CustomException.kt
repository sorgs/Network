package com.sorgs.sorgsnet.network.exception

import android.net.ParseException

import com.google.gson.JsonParseException

import org.json.JSONException

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * description: 自定义异常处理，包括解析异常等其他异常.
 *
 * @author Sorgs.
 * Created date: 2019/7/30.
 */
object CustomException {

    /**
     * 未知错误
     */
    const val UNKNOWN = 1000

    /**
     * 解析错误
     */
    const val PARSE_ERROR = 1001

    /**
     * 网络错误
     */
    const val NETWORK_ERROR = 1002

    /**
     * 协议错误
     */
    const val HTTP_ERROR = 1003

    fun handleException(e: Throwable): LocalException {
        val ex: LocalException
        if (e is JsonParseException
                || e is JSONException
                || e is ParseException
        ) {
            //解析错误
            ex = LocalException(PARSE_ERROR, e.message)
            return ex
        } else if (e is ConnectException) {
            //网络错误
            ex = LocalException(NETWORK_ERROR, e.message)
            return ex
        } else if (e is UnknownHostException || e is SocketTimeoutException) {
            //连接错误
            ex = LocalException(NETWORK_ERROR, e.message)
            return ex
        } else {
            //未知错误
            ex = LocalException(UNKNOWN, e.message)
            return ex
        }
    }
}
