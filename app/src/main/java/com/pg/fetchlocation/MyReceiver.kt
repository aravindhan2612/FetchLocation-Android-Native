package com.pg.fetchlocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class MyReceiver(): BroadcastReceiver() {
    private val TAG = "MyReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive called")

        // We are starting MyService via a worker and not directly because since Android 7
        // (but officially since Lollipop!), any process called by a BroadcastReceiver
        // (only manifest-declared receiver) is run at low priority and hence eventually
        // killed by Android.
        val workManager = context?.let { WorkManager.getInstance(it) }
        val startServiceRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorkManager::class.java)
            .build()
        workManager?.enqueue(startServiceRequest)
    }
}