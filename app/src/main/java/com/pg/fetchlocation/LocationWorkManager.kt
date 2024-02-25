package com.pg.fetchlocation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class LocationWorkManager(private  val context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {


    override suspend fun doWork(): Result {

        return Result.success()
    }
}