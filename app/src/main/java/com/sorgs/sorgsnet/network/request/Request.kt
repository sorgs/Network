package com.sorgs.sorgsnet.network.request

import com.sorgs.JavaBean
import com.sorgs.sorgsnet.network.response.ResponseData
import io.reactivex.Observable
import retrofit2.Call
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
        val HOST = "http://42.157.129.91/"
    }

    @POST("user/sorgs")
    fun getSorgs(@Query("id") id: String): Observable<ResponseData<JavaBean>>

    @POST("user/sorgs")
    fun getCallTest(@Query("id") id: String): Call<ResponseData<JavaBean>>
}
