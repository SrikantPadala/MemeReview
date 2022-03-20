package com.ztute.memereview.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ztute.memereview.domain.usecase.GetMemesFromNetworkUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import timber.log.Timber

class RefreshMemesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val getMemesFromNetworkUseCase: GetMemesFromNetworkUseCase
) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.d("Started refresh memes from worker")
        return try {
            getMemesFromNetworkUseCase()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

    /**
     * class annotate with @AssistedFactory will available in the dependency graph, you don't need
     * additional binding from [HelloWorldWorker_Factory_Impl] to [Factory].
     */
    @AssistedFactory
    interface Factory {
        fun create(
            appContext: Context,
            params: WorkerParameters
        ): RefreshMemesWorker
    }

    companion object {
        const val WORK_NAME = "RefreshMemesWorker"
    }
}