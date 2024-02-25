package com.pg.fetchlocation

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.pg.fetchlocation.ui.theme.FetchLocationTheme


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionsForTiramisu = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )
    private val requestPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if ("xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)) {
//            val autostartIntent = Intent()
//            autostartIntent.setComponent(
//                ComponentName(
//                    "com.miui.securitycenter",
//                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
//                )
//            )
//            startActivity(autostartIntent)
//        }
        startPowerSaverIntent(this)
        setContent {
            FetchLocationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            if (applicationContext.hasLocationPermission() && applicationContext.hasNotificationPermission()) {
                                if (isGpsEnabled()) {
                                    LocationService.stopService = false
                                    Intent(applicationContext, LocationService::class.java).apply {
                                        action = LocationService.ACTION_START
                                        startService(this)
                                    }
                                } else {
                                    showGpsEnableDialog()
                                }
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestPermissionLauncher.launch(requestPermissionsForTiramisu)
                                } else {
                                    requestPermissionLauncher.launch(requestPermissions)
                                }
                            }
                        }) {
                            Text(text = "Start track location")
                        }
                        Button(onClick = {
                            Intent(applicationContext, LocationService::class.java).apply {
                                action = LocationService.ACTION_STOP
                                startService(this)
                            }
                        }) {
                            Text(text = "Stop track location")
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.all { it.value }) {
            // All permissions granted, proceed with actions
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            if (isGpsEnabled()) {
               startServiceViaWorker()
            } else {
                showGpsEnableDialog()
            }
        } else {
            // Handle denied permissions (e.g., display rationale, disable features)
            Toast.makeText(this, "some permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGpsEnableDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Services")
            .setMessage("Location services are disabled. Would you like to enable them?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { _, _ ->
                // Handle the case where user declines
            }
            .show()
    }

    private fun startServiceViaWorker() {
        val workManager =  WorkManager.getInstance(this)
        val startServiceRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorkManager::class.java)
            .build()
        workManager.enqueue(startServiceRequest)
    }

    override fun onDestroy() {
        Log.d("******** MainActivity", " destroyed")
        super.onDestroy()
    }

    override fun onStop() {
        Log.d("******** MainActivity", " onStop")
        super.onStop()
    }

    override fun isFinishing(): Boolean {
        Log.d("******** MainActivity", " isFinishing")
        return super.isFinishing()
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FetchLocationTheme {
    }
}