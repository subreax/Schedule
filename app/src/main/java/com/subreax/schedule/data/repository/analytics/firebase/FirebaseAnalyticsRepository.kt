package com.subreax.schedule.data.repository.analytics.firebase

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.repository.analytics.AnalyticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseAnalyticsRepository @Inject constructor(
    private val analytics: FirebaseAnalytics,
    private val localCache: LocalCache,
    private val externalScope: CoroutineScope
) : AnalyticsRepository {
    override fun sendUserScheduleId(userScheduleId: String) {
        externalScope.launch {
            val isSent = localCache.get(KEY_IS_USER_SCHEDULE_ID_SENT)
            if (isSent != "1") {
                try {
                    analytics.setUserProperty("schedule_id", userScheduleId)
                    localCache.set(KEY_IS_USER_SCHEDULE_ID_SENT, "1")
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to send user schedule id", ex)
                }
            }
        }
    }

    companion object {
        private const val TAG = "FARepository"
        private const val KEY_IS_USER_SCHEDULE_ID_SENT = "FirebaseAnalyticsRepository/isUserScheduleIdSent"
    }
}