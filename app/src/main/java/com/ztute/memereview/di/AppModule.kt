package com.ztute.memereview.di

import android.content.Context
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ztute.memereview.DefaultDispatchers
import com.ztute.memereview.DispatcherProvider
import com.ztute.memereview.common.BASE_URL
import com.ztute.memereview.common.DATABASE_NAME
import com.ztute.memereview.database.MemeDao
import com.ztute.memereview.database.MemeDatabase
import com.ztute.memereview.domain.repository.MemeRepository
import com.ztute.memereview.network.DefaultMemeRepository
import com.ztute.memereview.network.MemeReviewApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private lateinit var memeDatabase: MemeDatabase

    @Provides
    fun provideAppContext(@ApplicationContext applicationContext: Context): Context {
        return applicationContext
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton //providing tokenInterceptor like this
    fun provideOkhttp(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okhttpBuilder =
            OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
        return okhttpBuilder.build()
    }

    @Provides
    @Singleton
    fun provideMemeReviewApiService(retrofit: Retrofit): MemeReviewApiService =
        retrofit.create(MemeReviewApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(context: Context): MemeDatabase {
        synchronized(MemeDatabase::class.java) {
            if (!::memeDatabase.isInitialized) {
                memeDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    MemeDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return memeDatabase
        }
    }

    @Provides
    @Singleton
    fun provideMemeDao(memeDatabase: MemeDatabase) = memeDatabase.memeDao

    @Provides
    @Singleton
    fun provideMemeRepository(
        memeDao: MemeDao,
        memeReviewApiService: MemeReviewApiService
    ) = DefaultMemeRepository(memeDao, memeReviewApiService) as MemeRepository

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider {
        return DefaultDispatchers()
    }
}