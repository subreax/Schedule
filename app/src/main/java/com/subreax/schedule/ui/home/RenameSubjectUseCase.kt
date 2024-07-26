package com.subreax.schedule.ui.home

/*
class RenameSubjectUseCase(private val localSubjectNameDataSource: LocalSubjectNameDataSource) {
    var alias by mutableStateOf("")
        private set

    var originalName by mutableStateOf("")
        private set

    var targetName by mutableStateOf<String?>(null)
        private set

    suspend fun startRenaming(name: String) {
        localSubjectNameDataSource.getEntryByName(name).mapResult {
            originalName = it.value
            this.alias = it.alias
            targetName = name
        }
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
                localSubjectNameDataSource.setNameAlias(subjectId, alias).also {
                    if (it is Resource.Failure) {
                        Log.e("RenameSubjectUseCase", "Failed to rename subject: ${it.message}")
                    }
                }
                cancelRenaming()
            }
        }
    }
}*/
