package com.subreax.schedule.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.subreax.schedule.data.repository.schedule.ScheduleRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RenameSubjectUseCase(private val scheduleRepository: ScheduleRepository) {
    var alias by mutableStateOf("")
        private set

    var originalName by mutableStateOf("")
        private set

    var targetName by mutableStateOf<String?>(null)
        private set

    fun startRenaming(name: String, alias: String) {
        this.originalName = name
        this.alias = alias
        this.targetName = name
    }

    fun cancelRenaming() {
        targetName = null
    }

    fun updateName(newName: String) {
        alias = newName
    }

    suspend fun finishRenaming() {
        withContext(Dispatchers.Default) {
            targetName?.let { subjectId ->
                scheduleRepository.setSubjectNameAlias(subjectId, alias).also {
                    if (it is Resource.Failure) {
                        Log.e("RenameSubjectUseCase", "Failed to rename subject: ${it.message}")
                    }
                }
                cancelRenaming()
            }
        }
    }
}
