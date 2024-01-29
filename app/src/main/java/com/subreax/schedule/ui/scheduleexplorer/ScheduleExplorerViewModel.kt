package com.subreax.schedule.ui.scheduleexplorer

import android.content.Context
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.ui.BaseScheduleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ScheduleExplorerViewModel @Inject constructor(
    @ApplicationContext appContext: Context,
    scheduleRepository: ScheduleRepository
) : BaseScheduleViewModel(appContext, scheduleRepository) {
}
