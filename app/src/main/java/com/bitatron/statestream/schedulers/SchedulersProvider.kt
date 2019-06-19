package com.bitatron.statestream.schedulers

import io.reactivex.Scheduler

interface SchedulersProvider {

    fun io(): Scheduler

    fun computation(): Scheduler

    fun mainThread(): Scheduler

    fun fixedPool(): Scheduler

    fun single(): Scheduler
}
