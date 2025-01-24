package com.subreax.schedule.data.repository.analytics.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.subreax.schedule.data.local.cache.LocalCache
import com.subreax.schedule.data.repository.analytics.AnalyticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FirebaseAnalyticsRepository(
    private val localCache: LocalCache,
    private val externalScope: CoroutineScope
) : AnalyticsRepository {
    private val analytics: FirebaseAnalytics = Firebase.analytics

    override fun sendUserScheduleId(scheduleId: String) {
        if (scheduleId.isBlank()) {
            Log.w(TAG, "schedule id is empty")
            return
        }

        externalScope.launch {
            val sentScheduleId = localCache.get(KEY_SENT_SCHEDULE_ID) ?: ""
            if (sentScheduleId != scheduleId) {
                try {
                    analytics.setUserProperty("schedule_id", scheduleId)
                    localCache.set(KEY_SENT_SCHEDULE_ID, scheduleId)
                    Log.d(TAG, "schedule id is sent: $scheduleId")
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to send user schedule id", ex)
                }
            }
        }
    }

    companion object {
        private const val TAG = "FARepository"
        private const val KEY_SENT_SCHEDULE_ID = "FirebaseAnalyticsRepository/sentScheduleId"
    }
}