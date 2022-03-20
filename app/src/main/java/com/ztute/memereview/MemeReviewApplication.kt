package com.ztute.memereview

import android.app.Application
import android.os.Build
import androidx.work.*
import com.sample.daggerworkmanagersample.SampleWorkerFactory
import com.ztute.memereview.work.RefreshMemesWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MemeReviewApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: SampleWorkerFactory

    val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfig)
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            //uncomment to test
            //setOneTimeWorkRequest()
            setupReccuringWork()
        }
    }

    private fun setupReccuringWork() {
        val constraintsPeriodic = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshMemesWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraintsPeriodic)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshMemesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    private fun setOneTimeWorkRequest() {
        val constraintsOneTime = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<RefreshMemesWorker>()
            .setConstraints(constraintsOneTime)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(oneTimeRequest)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build();
    }
}