package com.subreax.schedule.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.subreax.schedule.data.repository.subjectname.SubjectNameRepository
import com.subreax.schedule.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RenameSubjectUseCase(private val subjectNameRepository: SubjectNameRepository) {
    var name by mutableStateOf("")
        private set

    var originalName by mutableStateOf("")
        private set

    var subjectToRename by mutableStateOf<Long?>(null)
        private set

    suspend fun startRenaming(subjectId: Long) {
        subjectNameRepository.getNameBySubjectId(subjectId).mapResult {
            originalName = it.value
            name = it.alias
            subjectToRename = subjectId
        }
    }

    fun cancelRenaming() {
        subjectToRename = null
    }

    fun updateName(newName: String) {
        name = newName
    }

    suspend fun finishRenaming() {
        withContext(Dispatchers.Default) {
            subjectToRename?.let { subjectId ->
                subjectNameRepository.renameSubject(subjectId, name).also {
                    if (it is Resource.Failure) {
                        Log.e("RenameSubjectUseCase", "Failed to rename subject: ${it.message}")
                    }
                }
                cancelRenaming()
            }
        }
    }
}