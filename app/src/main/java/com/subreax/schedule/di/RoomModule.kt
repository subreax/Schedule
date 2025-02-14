package com.subreax.schedule.di

import androidx.room.Room
import com.subreax.schedule.data.local.MIGRATION_1_2
import com.subreax.schedule.data.local.MIGRATION_1_3
import com.subreax.schedule.data.local.MIGRATION_2_3
import com.subreax.schedule.data.local.MIGRATION_4_5
import com.subreax.schedule.data.local.MIGRATION_6_7
import com.subreax.schedule.data.local.MigrationFrom5To6
import com.subreax.schedule.data.local.ScheduleDatabase
import org.koin.dsl.module

val roomModule = module {
    single {
        Room
            .databaseBuilder(get(), ScheduleDatabase::class.java, "schedule")
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_1_3,
                MIGRATION_2_3,
                MIGRATION_4_5,
                MigrationFrom5To6(get()),
                MIGRATION_6_7
            )
            .build()
    }

    factory { get<ScheduleDatabase>().scheduleIdDao }
    factory { get<ScheduleDatabase>().subjectDao }
    factory { get<ScheduleDatabase>().subjectNameDao }
    factory { get<ScheduleDatabase>().teacherNameDao }
    factory { get<ScheduleDatabase>().bookmarkDao }
}
