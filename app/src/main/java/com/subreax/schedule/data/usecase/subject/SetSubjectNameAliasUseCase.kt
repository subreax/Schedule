package com.subreax.schedule.data.usecase.subject

import com.subreax.schedule.data.repository.subject.SubjectRepository
import com.subreax.schedule.utils.Resource

class SetSubjectNameAliasUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(name: String, alias: String): Resource<Unit> {
        return subjectRepository.setSubjectNameAlias(name, alias.trim())
    }
}