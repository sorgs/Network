package com.sorgs.sorgsnet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.sorgs.JavaBean
import com.sorgs.sorgsnet.network.NetWorkManager
import com.sorgs.sorgsnet.network.exception.LocalException
import com.sorgs.sorgsnet.network.request.Request
import com.sorgs.sorgsnet.network.response.ResponseData
import com.sorgs.sorgsnet.network.response.ResponseTransformer
import com.sorgs.sorgsnet.network.schedulers.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetWorkManager.instant.init(Request.HOST)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        // 初始化okhttp
        val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()


        btnTest1.setOnClickListener {
            NetWorkManager.request
                    ?.getSorgs("1")
                    ?.compose(ResponseTransformer.handleResult())
                    ?.compose(SchedulerProvider.schedulerProvider.applySchedulers())
                    ?.subscribe({ javabean ->
                        Log.i("sorgs", "btnTest1 name:${javabean?.name}")
                    }, { t ->
                        Log.e("sorgs", "btnTest1 e:${(t as LocalException).displayMessage}")
                    })
        }


        btnTest2.setOnClickListener {
            // 初始化Retrofit
            val request = Retrofit.Builder()
                    .client(client)
                    .baseUrl(Request.HOST)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //数据解析器
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Request::class.java)

            request.getSorgs("1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ t ->
                        Log.i("sorgs", "btnTest2 JavaBean:${(t.data as JavaBean).name}")
                    }, { e ->
                        Log.e("sorgs", "btnTest2 e:${e.message}")
                    })
        }

        btnTest3.setOnClickListener {

            // 初始化Retrofit
            val request = Retrofit.Builder()
                    .client(client)
                    .baseUrl(Request.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val callApi = request.create(Request::class.java)
            callApi.getCallTest("1").enqueue(object : Callback<ResponseData<JavaBean>> {
                override fun onFailure(call: Call<ResponseData<JavaBean>>, t: Throwable) {
                    Log.e("sorgs", "btnTest3 e:${t.message}")
                }

                override fun onResponse(call: Call<ResponseData<JavaBean>>,
                                        responseData: Response<ResponseData<JavaBean>>) {
                    Log.i("sorgs", "btnTest3 JavaBean:${(responseData.body()?.data as JavaBean).name}")
                }
            })
        }
    }
}

