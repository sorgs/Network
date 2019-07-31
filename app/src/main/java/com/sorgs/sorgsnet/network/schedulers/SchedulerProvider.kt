package com.sorgs.sorgsnet.network.schedulers

import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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
        /*return ObservableTransformer<T,T>(object : ObservableTransformer<T,T>{
            override fun apply(upstream: Observable<T>): ObservableSource<T> {
            }

        })*/
        return ObservableTransformer { observable -> observable.subscribeOn(io()).observeOn(ui()) }
    }

    companion object {
        val schedulerProvider: SchedulerProvider by lazy(
                mode = LazyThreadSafetyMode.SYNCHRONIZED) { SchedulerProvider() }
    }
}