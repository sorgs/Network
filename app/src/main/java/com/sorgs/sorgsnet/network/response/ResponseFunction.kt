package com.sorgs.sorgsnet.network.response

import com.sorgs.sorgsnet.network.exception.LocalException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function

/**
 * description: 服务其返回的数据解析
 * 正常服务器返回数据和服务器可能返回的exception.

 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class ResponseFunction<T> : Function<ResponseData<T>, ObservableSource<T>> {
    override fun apply(tResponseData: ResponseData<T>): ObservableSource<T> {
        val code = tResponseData.code
        val message = tResponseData.msg
        return if (code == 200) {
            Observable.just(tResponseData.data)
        } else {
            Observable.error(LocalException(code, message))
        }
    }
}