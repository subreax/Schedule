package com.subreax.schedule.di

import com.subreax.schedule.data.usecase.AcademicScheduleUseCases
import com.subreax.schedule.data.usecase.ScheduleUseCases
import com.subreax.schedule.data.usecase.SubjectUseCases
import com.subreax.schedule.data.usecase.ac_schedule.GetAcademicScheduleUseCase
import com.subreax.schedule.data.usecase.schedule.GetScheduleUseCase
import com.subreax.schedule.data.usecase.schedule.IsScheduleExpiredUseCase
import com.subreax.schedule.data.usecase.schedule.SyncAndGetScheduleUseCase
import com.subreax.schedule.data.usecase.schedule.SyncIfNeededAndGetScheduleUseCase
import com.subreax.schedule.data.usecase.subject.GetPlaceMapPointUseCase
import com.subreax.schedule.data.usecase.subject.GetSubjectUseCase
import com.subreax.schedule.data.usecase.subject.SetSubjectNameAliasUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCasesModule = module {
    singleOf(::GetAcademicScheduleUseCase)
    singleOf(::AcademicScheduleUseCases)

    singleOf(::GetScheduleUseCase)
    singleOf(::IsScheduleExpiredUseCase)
    single { SyncAndGetScheduleUseCase(get(), getDefaultDispatcher()) }
    single { SyncIfNeededAndGetScheduleUseCase(get(), get(), getDefaultDispatcher()) }
    singleOf(::ScheduleUseCases)

    singleOf(::GetSubjectUseCase)
    singleOf(::SetSubjectNameAliasUseCase)
    singleOf(::GetPlaceMapPointUseCase)
    singleOf(::SubjectUseCases)
}
