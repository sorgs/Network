package com.sorgs.sorgsnet.network

import com.sorgs.sorgsnet.BuildConfig
import com.sorgs.sorgsnet.network.request.Request
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * description: API初始化类.
 *
 * @author Sorgs.
 * Created date: 2019/7/29.
 */
class NetWorkManager {

    companion object {
        private var retrofit: Retrofit? = null
        //单例获取NetWorkManager
        val instant: NetWorkManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { NetWorkManager() }
        val request: Request? by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            retrofit?.create(Request::class.java)
        }
    }

    /**
     * 初始化必要对象和参数
     * @param baseUrl 基础连接
     */
    fun init(baseUrl: String) {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        /* val logging2 = HttpLoggingInterceptor()
         logging2.level = HttpLoggingInterceptor.Level.HEADERS*/
        // 初始化okhttp
        val client = OkHttpClient.Builder()
                //.addInterceptor(logging2)
                .addInterceptor(logging)
                .build()

        // 初始化Retrofit
        retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}


