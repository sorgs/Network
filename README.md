> 网络请求在移动端是极为常见和重要，随处可见。为此，为了避免到处使用增加内存和性能，以及方便使用和解耦，进行网络库的简单封装。

# 特点
1. 解耦：对下面使用的网络请求框架和上层网络进行解耦。方便底层可以根据业务要求换更网络请求网络也不影响到上层业务逻辑。
2. 方便：对使用的场景极为方便，仅仅5行左右代码，即可完成一次网络请求以及数据处理。
3. 解放：解放繁琐的线程切换，错误处理和判断，数据处理，Json的转换等，使调用方不必考虑与业务逻辑无关的事情。
4. 透明：调用方对调用的函数使用起来简单，便于理解

# Retrofit简介
- Retrofit，一个远近闻名的网络框架，它是由Square公司开源的。Square公司，是我们的老熟人了，很多框架都是他开源的，比如OkHttp，picasso，leakcanary等等。他们公司的很多开源库，几乎已经成为现在开发Android APP的标配。
- 简单来说，Retrofit其实是底层还是用的OkHttp来进行网络请求的，只不过他包装了一下，使得开发者在使用访问网络的时候更加方便简单高效。
- 一句话总结：Retrofit将接口动态生成实现类，该接口定义了请求方式+路径+参数等注解，Retrofit可以方便得从注解中获取这些参数，然后拼接起来，通过OkHttp访问网络。

# Retrofit简单使用

## 引入依赖
- 引入retrofit2以及需要转化使用的Gson

```
implementation 'com.squareup.retrofit2:retrofit:2.3.0'
implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
```

## API的interface接口
- 需要先定义出注解的接口

```Java
interface Request {
    companion object {
        val HOST = "http://42.157.129.91/"
    }

    @POST("user/sorgs")
    fun getSorgs(@Query("id") id: String): Observable<ResponseData<JavaBean>>

    @POST("user/sorgs")
    fun getCallTest(@Query("id") id: String): Call<ResponseData<JavaBean>>
}
```
- 定义出静态字符串HOST，用来限定请求的服务器，即BaseUrl
- 定义出接口方法，`@POST`用于指定请求的方式，包括POST，GET等。
- 方法需要定义出返回值以及接受的参数

## 创建Retrofit实例
- 通过构造者方式，创建出Retrofit实例
- baseUrl即传入服务器地址
- addConverterFactory为转化工厂，我们需要将获取的Json直接通过Gson转化为Bean对象

```Java
// 初始化Retrofit
val request = Retrofit.Builder()
        .client(client)
        .baseUrl(Request.HOST)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
```

## API接口转换成实例
- 将刚定义的interface文件引入转化为实例

```Java
val callApi = request.create(Request::class.java)
```

## 进行网络请求
- 直接调用interface定义的请求方法，调用enqueue方法进行回调Callback返回请求成功以及失败

```Java
callApi.getCallTest("1").enqueue(object : Callback<ResponseData<JavaBean>> {
    override fun onFailure(call: Call<ResponseData<JavaBean>>, t: Throwable) {
        Log.e("sorgs", "btnTest3 e:${t.message}")
    }
    override fun onResponse(call: Call<ResponseData<JavaBean>>,
                            responseData: Response<ResponseData<JavaBean>>) {
        Log.i("sorgs", "btnTest3 JavaBean:${(responseData.body()?.data as JavaBean).name}")
    }
})
```

# 封装网络库
> 上诉简单使用仅仅是demo如此写，如果放到实际业务中，还需考虑request的创建单例化，日志拦截查看，线程切换，数据处理以及错误处理

## 应用依赖


## 单例建立NetWorkManager
- 通过单例构建retrofit等

```Java
private var retrofit: Retrofit? = null
//单例获取NetWorkManager
val instant: NetWorkManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { NetWorkManager() }
val request: Request? by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    retrofit?.create(Request::class.java)
}
```

- 进行初始化

```Java
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
```
- 在初始化时候，构建HttpLoggingInterceptor，用于日志拦截。并利用BuildConfig.DEBUG进行区分，因为不希望在正式包里面也暴露出请求信息
- 日志拦截分为NONE、BASIC、HEADERS以及BODY，一般来说使用BASIC以及BODY，为了方便调试和查看，使用BODY更加方便。
- 同时addInterceptor是可以添加多个的，可以同时把BASIC和BODY添加上进行打印输出。通过client()构建到retrofit中。
- retrofit的构造，同时加入`addCallAdapterFactory(RxJava2CallAdapterFactory.create())`,直接把RxJava引入，方便错误处理，数据处理，线程切换等

## 接口返回数据封装
- 服务器返回的数据，一般都是有严格的格式，分为code、msg和data。真实数据包含在data中，code和msg是用来进行判断这次请求的结果，这些判断我们就需要在底层直接处理好，所以直接封装起来。

```Java
/**
 * description: 接口返回数据封装.
 * {code:0,data:"",msg:""}
 * code：接口返回的code 一定不能为空
 * data：接口返回具体的数据结果 可能为空
 * msg：message 可用来返回接口的说明 可能为空
 *
 * @author Sorgs.
 * Created date: 2019/7/30.
 */
data class ResponseData<T>(
        var code: Int,
        var data: T?,
        var msg: String?
)

```

## 错误处理
- 错误处理分为了服务器异常和本地异常
  - 服务器异常包括404,500等
  - 本地异常情况更多，包括网络错误，连接异常等

```Java
/**
 * description: 非服务器产生的异常，比如本地无无网络请求，Json数据解析错误等等.

 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class ErrorResumeFunction<T> : Function<Throwable, ObservableSource<out ResponseData<T>>> {
    override fun apply(throwable: Throwable): ObservableSource<out ResponseData<T>> {
        return Observable.error(CustomException.handleException(throwable))
    }
}

/**
 * description: 自定义异常处理，包括解析异常等其他异常.
 *
 * @author Sorgs.
 * Created date: 2019/7/30.
 */
object CustomException {

    /**
     * 未知错误
     */
    const val UNKNOWN = 1000

    /**
     * 解析错误
     */
    const val PARSE_ERROR = 1001

    /**
     * 网络错误
     */
    const val NETWORK_ERROR = 1002

    /**
     * 协议错误
     */
    const val HTTP_ERROR = 1003

    fun handleException(e: Throwable): LocalException {
        val ex: LocalException
        if (e is JsonParseException
                || e is JSONException
                || e is ParseException
        ) {
            //解析错误
            ex = LocalException(PARSE_ERROR, e.message)
            return ex
        } else if (e is ConnectException) {
            //网络错误
            ex = LocalException(NETWORK_ERROR, e.message)
            return ex
        } else if (e is UnknownHostException || e is SocketTimeoutException) {
            //连接错误
            ex = LocalException(NETWORK_ERROR, e.message)
            return ex
        } else {
            //未知错误
            ex = LocalException(UNKNOWN, e.message)
            return ex
        }
    }
}
``` 

- 把异常进行封装成自己的异常处理。提前把Json转化出错，网络错误，连接异常处理出来
- 再利用继承ResponseFunction进行处理服务器异常处理，然后把异常进行封装。正确的数据发送到下游

```java
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


/**
 * description: 异常处理.
 *
 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class LocalException : Exception {
    var code: Int = 0
    var displayMessage: String? = null

    constructor(code: Int, displayMessage: String?) {
        this.code = code
        this.displayMessage = displayMessage
    }

    constructor(code: Int, message: String, displayMessage: String?) : super(message) {
        this.code = code
        this.displayMessage = displayMessage
    }
}
```

## 线程切换
> 网络请求是需要进行放到子线程进行处理防止阻塞主线程，等待网络请求结果回来之后，再进行转化到UI线程处理数据
- 定义切线接口,然后进行线程切换

```Java
/**
 * description: 切换线程的线程定义

 * @author Sorgs.
 * Created date: 2019/7/30.
 */
interface BaseSchedulerProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler

    fun <T> applySchedulers(): ObservableTransformer<T, T>
}

/**
 * description: 完成处理线程切换.

 * @author Sorgs.
 * Created date: 2019/7/30.
 */
class SchedulerProvider : BaseSchedulerProvider {
    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun <T> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable -> observable.subscribeOn(io()).observeOn(ui()) }
    }

    companion object {
        val schedulerProvider: SchedulerProvider by lazy(
                mode = LazyThreadSafetyMode.SYNCHRONIZED) { SchedulerProvider() }
    }
}
```

## 调用方使用

```Java
NetWorkManager.request
        ?.getSorgs("1")
        ?.compose(ResponseTransformer.handleResult())
        ?.compose(SchedulerProvider.schedulerProvider.applySchedulers())
        ?.subscribe({ javabean ->
            Log.i("sorgs", "btnTest1 name:${javabean?.name}")
        }, { t ->
            Log.e("sorgs", "btnTest1 e:${(t as LocalException).displayMessage}")
        })
```

- 直接利用subscribe返回得到处理数据后的结果，拿到数据直接进行业务逻辑代码边写。对于错误，直接将服务器错误和本地异常都抛出来，由调用方选择进行处理


# 结语
- 封装成库的好处就是方便调用者，利用最简单的方式进行复杂而常用的操作。
- 同时对接口透明，对实现封装，实现对调用者最大的友好
- demo：[https://github.com/sorgs/Network](https://github.com/sorgs/Network "https://github.com/sorgs/Network")