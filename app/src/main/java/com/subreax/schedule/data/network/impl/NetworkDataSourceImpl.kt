package com.subreax.schedule.data.network.impl

import android.util.Log
import com.subreax.schedule.data.model.PersonName
import com.subreax.schedule.data.network.NetworkDataSource
import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.model.NetworkSubject
import com.subreax.schedule.data.network.model.RetrofitSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(
    private val service: RetrofitService
) : NetworkDataSource {
    override suspend fun getSubjects(scheduleOwner: String): List<NetworkSubject> {
        return withContext(Dispatchers.IO) {
            val info = service.getDates(scheduleOwner)
            if (info.error != null) {
                return@withContext emptyList()
            }

            service.getSubjects(scheduleOwner, info.scheduleOwnerType)
                .map(::toNetworkSubject)
        }
    }

    override suspend fun isScheduleOwnerExists(scheduleOwner: String): Boolean {
        return withContext(Dispatchers.IO) {
            service.getDates(scheduleOwner).error == null
        }
    }

    override suspend fun getScheduleOwnerHints(scheduleOwner: String): List<String> {
        return withContext(Dispatchers.IO) {
            service.getDictionaries(scheduleOwner).map { it.value }
        }
    }

    private fun toNetworkSubject(it: RetrofitSubject): NetworkSubject {
        val (beginTime, endTime) = DateTimeUtils.parseTimeRange(it.DATE_Z, it.TIME_Z)
        val teacher = PersonName.parse(it.PREP ?: "")

        val note: String? = it.GROUPS.firstOrNull()?.PRIM?.let {
            if (it == "пр. Ленина - 84") {
                return@let null
            }

            it.trim().ifEmpty { null }
        }

        return NetworkSubject(
            name = it.transformSubjectName(),
            place = it.AUD,
            beginTime = beginTime,
            endTime = endTime,
            teacher = teacher,
            type = it.CLASS,
            kow = it.KOW,
            note = note
        )
    }

    private fun RetrofitSubject.transformSubjectName(): String {
        return try {
            if (DISCIP == "Иностранный язык") {
                transformSubjectNameAsForeignLang()
            } else if (DISCIP.startsWith("Введение в матема")) {
                "Математический анал"
            } else if (PREP?.startsWith("Юрков") == true) {
                "Душный дед"
            } else if (DISCIP.startsWith("Физическая") && CLASS == "lecture") {
                "Спортивное сидение на лавках"
            } else {
                DISCIP
            }
        } catch (th: Throwable) {
            Log.e("NetworkDataSourceImpl", "Error occurred while parsing specific subject name", th)
            DISCIP
        }
    }

    private fun RetrofitSubject.transformSubjectNameAsForeignLang(): String {
        val ob = KOW.lastIndexOf('(')
        return if (ob != -1) {
            val cb = KOW.indexOf(')', ob)
            val lang = KOW
                .substring(ob + 1, cb)
                .replaceFirstChar { it.uppercaseChar() }

            "$lang язык"
        } else {
            DISCIP
        }
    }
}