package com.pg.fetchlocation

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private var wakeLock: PowerManager.WakeLock? = null


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        acquireLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startLocationTracking()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun startLocationTracking() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location")
            .setContentText("Location: ---")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        locationClient.getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()

                val updateNotification =
                    notification.setContentText("Location: (lat:${lat}, long:${long})")
                notificationManager.notify(1, updateNotification.build())
            }.launchIn(serviceScope)
        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        isServiceRunning = false;
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        isServiceRunning = false;
        serviceScope.cancel()
        val broadcastIntent = Intent(this, MyReceiver::class.java)
        sendBroadcast(broadcastIntent)
        super.onTaskRemoved(rootIntent)
    }

    @SuppressLint("WakelockTimeout")
    private fun acquireLock() {
        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationService::lock").apply {
                acquire()
            }
        }
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        var isServiceRunning = false
        var stopService = false
    }
}