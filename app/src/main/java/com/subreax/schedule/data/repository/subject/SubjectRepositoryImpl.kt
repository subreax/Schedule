package com.subreax.schedule.data.repository.subject

import com.subreax.schedule.data.local.dao.SubjectDao
import com.subreax.schedule.data.local.entitiy.SubjectEntity
import com.subreax.schedule.data.local.entitiy.asExternalModel
import com.subreax.schedule.data.local.subject_name.SubjectNameLocalDataSource
import com.subreax.schedule.data.local.teacher_name.TeacherNameLocalDataSource
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.data.model.SubjectType
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.ms2min
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Date

class SubjectRepositoryImpl(
    private val subjectNameLocalDataSource: SubjectNameLocalDataSource,
    private val teacherNameLocalDataSource: TeacherNameLocalDataSource,
    private val subjectDao: SubjectDao,
    private val defaultDispatcher: CoroutineDispatcher
) : SubjectRepository {
    override suspend fun replaceSubjects(
        localScheduleId: Int,
        networkSubjects: List<NetworkSubject>,
        clearFrom: Date
    ) {
        val fromMins = (clearFrom.time / 60000L).toInt()

        // объяснение +1
        // пусть обновление происходит в 13:09. тогда удалится текущий предмет, который кончается в 13:10 и позже
        // пусть обновление происходит в 13:10. тогда предмет, который кончается в эту минуту, останется. а все предметы позже удалятся
        subjectDao.deleteSubjects(localScheduleId, fromMins + 1)

        subjectDao.insertSubjects(networkSubjects.map { it.asEntity(localScheduleId) })
    }

    override suspend fun getSubjects(localScheduleId: Int): List<Subject> =
        withContext(defaultDispatcher) {
            subjectDao.getSubjects(localScheduleId).map { it.asExternalModel() }
        }

    override suspend fun getSubjectById(id: Long): Subject? {
        return subjectDao.findSubjectById(id)?.asExternalModel()
    }

    override suspend fun clearSubjects(localScheduleId: Int, fromMins: Int) {
        subjectDao.deleteSubjects(localScheduleId, fromMins)
    }

    override suspend fun setSubjectNameAlias(name: String, alias: String): Resource<Unit> {
        return subjectNameLocalDataSource.setNameAlias(name, alias)
    }

    private suspend fun NetworkSubject.asEntity(localScheduleId: Int): SubjectEntity {
        return SubjectEntity(
            id = 0L,
            typeId = SubjectType.fromId(type).id,
            scheduleId = localScheduleId,
            subjectNameId = subjectNameLocalDataSource.getEntryByName(name).id,
            place = place,
            teacherNameId = teacherNameLocalDataSource.getEntryByName(teacher ?: "").id,
            beginTimeMins = beginTime.time.ms2min(),
            endTimeMins = endTime.time.ms2min(),
            rawGroups = SubjectEntity.buildRawGroups(groups)
        )
    }
}