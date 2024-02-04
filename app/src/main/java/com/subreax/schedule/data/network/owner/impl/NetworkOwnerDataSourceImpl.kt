package com.subreax.schedule.data.network.owner.impl

import com.subreax.schedule.data.network.RetrofitService
import com.subreax.schedule.data.network.owner.NetworkOwnerDataSource
import com.subreax.schedule.utils.Resource
import com.subreax.schedule.utils.UiText
import java.io.IOException
import javax.inject.Inject

class NetworkOwnerDataSourceImpl @Inject constructor(
    private val service: RetrofitService
) : NetworkOwnerDataSource {
    override suspend fun getOwnerHints(ownerId: String): Resource<List<String>> {
        return try {
            val ids = service.getDictionaries(ownerId).map { it.value }
            Resource.Success(ids)
        } catch (ex: IOException) {
            Resource.Failure(UiText.hardcoded("Не удалось получить список идентификаторов из-за сетевой ошибки"))
        } catch (ex: Exception) {
            Resource.Failure(UiText.hardcoded("Не удалось получить список идентификаторов"))
        }
    }

    override suspend fun isOwnerExist(ownerId: String): Resource<Boolean> {
        return try {
            val result = service.getDates(ownerId).error == null
            Resource.Success(result)
        } catch (ex: Exception) {
            Resource.Failure(UiText.hardcoded("Не удалось проверить существование идентификатора"))
        }
    }

    override suspend fun getOwnerType(ownerId: String): Resource<String> {
        return try {
            val info = service.getDates(ownerId)
            if (info.error != null) {
                Resource.Failure(UiText.hardcoded("Неизвестный идентификатор"))
            } else {
                Resource.Success(info.scheduleOwnerType)
            }
        } catch (ex: Exception) {
            Resource.Failure(UiText.hardcoded("Не удалось получить информацию об идентификаторе"))
        }
    }
}