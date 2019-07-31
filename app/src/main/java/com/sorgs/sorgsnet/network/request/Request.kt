package com.sorgs.sorgsnet.network.request

import com.sorgs.JavaBean
import com.sorgs.sorgsnet.network.response.Response
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * description: 封装请求的接口.
 *
 * @author Sorgs.
 * Created date: 2019/7/29.
 */
interface Request {
    companion object {
        val HOST = "http://192.168.31.197:8080/demo/"
    }

    @POST("user/sorgs")
    abstract fun getSorgs(@Query("id") id: String): Observable<Response<JavaBean>>
}
