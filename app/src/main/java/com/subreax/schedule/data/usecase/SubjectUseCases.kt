package com.subreax.schedule.data.usecase

import com.subreax.schedule.data.usecase.subject.GetPlaceMapPointUseCase
import com.subreax.schedule.data.usecase.subject.GetSubjectUseCase
import com.subreax.schedule.data.usecase.subject.SetSubjectNameAliasUseCase

data class SubjectUseCases(
    val getById: GetSubjectUseCase,
    val setNameAlias: SetSubjectNameAliasUseCase,
    val getPlaceMapPoint: GetPlaceMapPointUseCase
)
