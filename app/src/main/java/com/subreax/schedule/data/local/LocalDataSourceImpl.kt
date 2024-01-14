package com.subreax.schedule.data.local

import com.subreax.schedule.data.local.entitiy.LocalExpandedSubject
import com.subreax.schedule.data.local.entitiy.LocalOwner
import com.subreax.schedule.data.local.entitiy.LocalSubject
import com.subreax.schedule.data.local.entitiy.LocalSubjectName
import com.subreax.schedule.data.model.Subject
import com.subreax.schedule.utils.toMinutes
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    database: ScheduleDatabase
) : LocalDataSource {
    private val subjectDao = database.subjectDao
    private val subjectNameDao = database.subjectNameDao
    private val ownerDao = database.ownerDao

    override suspend fun saveSchedule(scheduleOwner: String, schedule: List<Subject>) {
        subjectDao.deleteSubjects(getOwnerId(scheduleOwner), System.currentTimeMillis().toMinutes())

        subjectDao.insert(
            schedule
                .sortedBy { it.timeRange.start }
                .map { it.toLocal(scheduleOwner) })
    }

    private suspend fun insertSubjectNameIfNotExist(name: String): Int {
        subjectNameDao.addNameIfNotExist(LocalSubjectName(0, name, name))
        return subjectNameDao.getNameId(name)
    }

    private suspend fun getOwnerId(name: String): Int {
        return ownerDao.findOwnerByNetworkId(name)!!.id
    }

    override suspend fun loadSchedule(scheduleOwner: String): List<LocalExpandedSubject> {
        return subjectDao.findSubjectsByOwnerId(getOwnerId(scheduleOwner))
    }

    override suspend fun findSubjectById(id: Int): LocalExpandedSubject? {
        return subjectDao.findSubjectById(id)
    }

    override suspend fun addScheduleOwner(owner: String): Boolean {
        return try {
            ownerDao.addOwner(LocalOwner(0, owner))
            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun getScheduleOwners(): Flow<List<LocalOwner>> {
        return ownerDao.getOwners()
    }

    override suspend fun getFirstScheduleOwner(): LocalOwner? {
        return ownerDao.getFirstOwner()
    }

    override suspend fun removeScheduleOwnerByName(ownerName: String) {
        ownerDao.removeOwnerByNetworkId(ownerName)
    }

    override suspend fun updateScheduleOwnerName(id: String, name: String) {
        ownerDao.updateOwnerNameByNetworkId(id, name.trim())
    }

    private suspend fun Subject.toLocal(scheduleOwner: String): LocalSubject {
        return LocalSubject(
            id = 0,
            typeId = type.id,
            ownerId = getOwnerId(scheduleOwner),
            nameId = insertSubjectNameIfNotExist(name),
            place = place,
            teacherName = teacherName?.full() ?: "",
            beginTimeMins = timeRange.start.time.toMinutes(),
            endTimeMins = timeRange.end.time.toMinutes(),
            note = note ?: ""
        )
    }
}
