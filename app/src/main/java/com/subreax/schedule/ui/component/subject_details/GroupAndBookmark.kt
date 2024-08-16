package com.subreax.schedule.ui.component.subject_details

import com.subreax.schedule.data.model.Group
import com.subreax.schedule.data.model.ScheduleBookmark

data class GroupAndBookmark(
    val group: Group,
    val bookmark: ScheduleBookmark?
)