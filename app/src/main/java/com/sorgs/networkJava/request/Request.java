package com.sorgs.networkJava.request;

import com.sorgs.JavaBean;
import com.sorgs.networkJava.response.Response;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Zaifeng on 2018/2/28.
 * 封装请求的接口
 */

public interface Request {

    public static String HOST = "http://192.168.31.197:8080/demo/";

    @POST("user/sorgs")
    abstract Observable<Response<JavaBean>> getSorgs(@Query("id") String id);

}
