package com.subreax.schedule.di

import org.koin.dsl.module

val KoinModules = module {
    includes(appModule, coroutinesModule, retrofitModule, roomModule, viewModelModule, useCasesModule)
}