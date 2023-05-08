package finaldev.motion_sensors

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.view.Display
import android.view.Surface
import io.flutter.plugin.common.EventChannel

class ScreenOrientationStreamHandler(
    private val activity: Activity,
    private val sensorManager: SensorManager,
    sensorType: Int,
    private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL,
) : EventChannel.StreamHandler, SensorEventListener {
    private val sensor = sensorManager.getDefaultSensor(sensorType)
    private var eventSink: EventChannel.EventSink? = null
    private var lastRotation: Double = -1.0

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
        sensorManager.registerListener(this, sensor, interval)
    }

    override fun onCancel(arguments: Any?) {
        sensorManager.unregisterListener(this)
        eventSink = null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val rotation = getScreenOrientation()
        if (rotation != lastRotation) {
            eventSink?.success(rotation)
            lastRotation = rotation
        }
    }

    private fun getScreenOrientation(): Double {
        return when (activity.getDisplayCompat()?.rotation) {
            Surface.ROTATION_0 -> 0.0
            Surface.ROTATION_90 -> 90.0
            Surface.ROTATION_180 -> 180.0
            Surface.ROTATION_270 -> -90.0
            else -> 0.0
        }
    }

    private fun Activity.getDisplayCompat(): Display? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("deprecation")
            windowManager.defaultDisplay
        }
    }
}