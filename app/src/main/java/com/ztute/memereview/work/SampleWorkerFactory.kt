package com.sample.daggerworkmanagersample

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ztute.memereview.work.RefreshMemesWorker
import javax.inject.Inject

/**
 * If there is no worker found, return null to use the default behaviour of [WorkManager]
 * (create worker using refection)
 *
 * In addition you can use dagger multi-binding to avoid manual check the workerClassName
 * but if you not familiar dagger multi-binding then it better to do it manually since
 * it easier to understand.
 *
 * Check out earlier commit to see the dagger multi-binding solution!
 *
 * @see WorkerFactory.createWorkerWithDefaultFallback
 */
//https://github.com/nlgtuankiet/dagger-workmanager
class SampleWorkerFactory @Inject constructor(
    private val refreshMemesWorkerFactory: RefreshMemesWorker.Factory,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            RefreshMemesWorker::class.java.name ->
                refreshMemesWorkerFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}