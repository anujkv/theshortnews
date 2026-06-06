package com.a3solution.theshortnews.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.a3solution.theshortnews.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncWorker", "Background sync started")
        
        return try {
            val repository = NewsViewModel.createDefaultRepository(applicationContext)
            // Fetch top articles and update DB
            repository.getTopArticles().first()
            
            Log.d("SyncWorker", "Background sync completed successfully")
            
            // Re-schedule the work for the next interval
            scheduleNext(applicationContext)
            
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Background sync failed", e)
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "SyncNewsWork"
        
        // Use 5 minutes for testing as requested. Change to 10 for production.
        private const val SYNC_INTERVAL_MINUTES = 5L

        fun startWork(context: Context) {
            scheduleNext(context, immediate = true)
        }

        fun stopWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d("SyncWorker", "Background sync stopped")
        }

        private fun scheduleNext(context: Context, immediate: Boolean = false) {
            val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInitialDelay(if (immediate) 0 else SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                workRequest
            )
            Log.d("SyncWorker", "Scheduled next sync in $SYNC_INTERVAL_MINUTES minutes")
        }
    }
}
