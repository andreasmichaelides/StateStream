package com.bitatron.statestream.schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class SchedulersProviderImpl : SchedulersProvider {
    override fun io(): Scheduler = Schedulers.io()

    override fun computation(): Scheduler = Schedulers.computation()

    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()

    override fun fixedPool(): Scheduler = Schedulers.from(Executors.newFixedThreadPool(4))

    override fun single(): Scheduler = Schedulers.single()
}
