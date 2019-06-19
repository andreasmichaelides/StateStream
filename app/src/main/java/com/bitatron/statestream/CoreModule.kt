package com.bitatron.statestream

import android.content.res.Resources
import com.bitatron.statestream.logger.AndroidLogger
import com.bitatron.statestream.logger.Logger
import com.bitatron.statestream.schedulers.SchedulersProvider
import com.bitatron.statestream.schedulers.SchedulersProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

var androidModule = module {
    factory<Resources> { androidContext().resources }
}

var loggingModule  = module {
    single<Logger> { AndroidLogger() }
}

var shedulersModule  = module {
    single<SchedulersProvider> { SchedulersProviderImpl() }
}