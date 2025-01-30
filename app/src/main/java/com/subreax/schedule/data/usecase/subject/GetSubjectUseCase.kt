package com.subreax.schedule.data.usecase.subject

import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.repository.subject.SubjectRepository

class GetSubjectUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(id: Long): Subject? {
        return subjectRepository.getSubjectById(id)
    }
}
