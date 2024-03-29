package com.sorgs.networkJava.response;


import com.sorgs.networkJava.Exception.ApiException;
import com.sorgs.networkJava.Exception.CustomException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * Created by Zaifeng on 2018/2/28.
 * 对返回的数据进行处理，区分异常的情况。
 */

public class ResponseTransformer {

    public static <T> ObservableTransformer<Response<T>, T> handleResult() {
        return new ObservableTransformer<Response<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<Response<T>> upstream) {
                return upstream
                        .onErrorResumeNext(new ErrorResumeFunction<>())
                        .flatMap(new ResponseFunction<>());
            }

        };
        /*return upstream -> upstream
                .onErrorResumeNext(new ErrorResumeFunction<>())
                .flatMap(new ResponseFunction<>());*/
    }


    /**
     * 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误等等。
     *
     * @param <T>
     */
    private static class ErrorResumeFunction<T>
            implements Function<Throwable, ObservableSource<? extends Response<T>>> {

        @Override
        public ObservableSource<? extends Response<T>> apply(Throwable throwable) {
            return Observable.error(CustomException.handleException(throwable));
        }
    }

    /**
     * 服务其返回的数据解析
     * 正常服务器返回数据和服务器可能返回的exception
     *
     * @param <T>
     */
    private static class ResponseFunction<T> implements Function<Response<T>, ObservableSource<T>> {

        @Override
        public ObservableSource<T> apply(Response<T> tResponse) {
            int code = tResponse.getCode();
            String message = tResponse.getMsg();
            if (code == 200) {
                return Observable.just(tResponse.getData());
            } else {
                return Observable.error(new ApiException(code, message));
            }
        }
    }


}
