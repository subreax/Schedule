package com.subreax.schedule.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

enum class KoinCoroutineDispatcher {
    Main, IO, Default
}

val coroutinesModule = module {
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    factory(named(KoinCoroutineDispatcher.Main)) { Dispatchers.Main }
    factory(named(KoinCoroutineDispatcher.IO)) { Dispatchers.IO }
    factory(named(KoinCoroutineDispatcher.Default)) { Dispatchers.Default }
}

fun Scope.getMainDispatcher(): CoroutineDispatcher = get(named(KoinCoroutineDispatcher.Main))
fun Scope.getIoDispatcher(): CoroutineDispatcher = get(named(KoinCoroutineDispatcher.IO))
fun Scope.getDefaultDispatcher(): CoroutineDispatcher = get(named(KoinCoroutineDispatcher.Default))