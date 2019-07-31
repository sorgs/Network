package com.sorgs.sorgsnet.network.response

import io.reactivex.ObservableTransformer

/**
 * description: 对返回的数据进行处理，区分异常的情况.不必再需要上面去重复判断

 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class ResponseTransformer {

    companion object {
        /**
         * 处理数据结果
         */
        fun <T> handleResult(): ObservableTransformer<Response<T>, T> {
            /*return ObservableTransformer<Response<T>, T>(object : ObservableTransformer<Response<T>, T> {
                 override fun apply(upstream: Observable<Response<T>>): ObservableSource<T> {
                 }

             })*/
            return ObservableTransformer { upstream ->
                upstream.onErrorResumeNext(ErrorResumeFunction())
                        .flatMap(ResponseFunction())
            }
        }
    }


}