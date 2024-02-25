package com.pg.fetchlocation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters


class MyWorkManager(private  val context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {
    private val TAG = "MyWorker"

    override suspend fun doWork(): Result {
        if (!LocationService.isServiceRunning) {
            Log.d(TAG, "starting service from doWork")
            val intent = Intent(context, LocationService::class.java)
            intent.action = LocationService.ACTION_START
            ContextCompat.startForegroundService(context, intent)
        }
        return  Result.success()
    }
}