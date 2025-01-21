package com.subreax.schedule.di

import com.subreax.schedule.ui.ac_schedule.AcademicScheduleViewModel
import com.subreax.schedule.ui.bookmark_manager.BookmarkManagerViewModel
import com.subreax.schedule.ui.bookmark_manager.add_bookmark.AddBookmarkViewModel
import com.subreax.schedule.ui.home.HomeViewModel
import com.subreax.schedule.ui.schedule_explorer.ScheduleExplorerViewModel
import com.subreax.schedule.ui.search_schedule.SearchScheduleViewModel
import com.subreax.schedule.ui.welcome.EnterScheduleIdViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ScheduleExplorerViewModel)
    viewModelOf(::SearchScheduleViewModel)
    viewModelOf(::BookmarkManagerViewModel)
    viewModelOf(::AddBookmarkViewModel)
    viewModelOf(::AcademicScheduleViewModel)
    viewModelOf(::EnterScheduleIdViewModel)
}