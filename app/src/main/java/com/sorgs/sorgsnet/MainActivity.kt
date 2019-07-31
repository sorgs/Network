package com.sorgs.sorgsnet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.sorgs.JavaBean
import com.sorgs.sorgsnet.network.NetWorkManager
import com.sorgs.sorgsnet.network.exception.LocalException
import com.sorgs.sorgsnet.network.request.Request
import com.sorgs.sorgsnet.network.response.ResponseTransformer
import com.sorgs.sorgsnet.network.schedulers.SchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NetWorkManager.instant.init(Request.HOST)

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
        val request = Retrofit.Builder()
                .client(client)
                .baseUrl(Request.HOST)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Request::class.java)


        btnTest1.setOnClickListener {
            NetWorkManager
                    .request
                    ?.getSorgs("1")
                    ?.compose(ResponseTransformer.handleResult())
                    ?.compose(SchedulerProvider.schedulerProvider.applySchedulers())
                    ?.subscribe({ javabean ->
                        Log.i("sorgs", "name:${javabean?.name}")
                    }, { t ->
                        Log.e("sorgs", "e:${(t as LocalException).displayMessage}")
                    })
        }


        btnTest2.setOnClickListener {

            request.getSorgs("1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ t ->
                        Log.i("sorgs", "JavaBean:${(t.data as JavaBean).name}")
                    }, { e ->
                        Log.e("sorgs", "e:${e.message}")
                    })


        }
    }
}

