package com.sorgs.sorgsnet.network.response

import com.sorgs.sorgsnet.network.exception.CustomException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function

/**
 * description: 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误等等.

 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class ErrorResumeFunction<T> : Function<Throwable, ObservableSource<out Response<T>>> {
    override fun apply(throwable: Throwable): ObservableSource<out Response<T>> {
        return Observable.error(CustomException.handleException(throwable))
    }
}