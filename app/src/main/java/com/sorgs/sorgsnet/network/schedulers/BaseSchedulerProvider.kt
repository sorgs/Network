package com.sorgs.sorgsnet.network.schedulers

import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler

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
