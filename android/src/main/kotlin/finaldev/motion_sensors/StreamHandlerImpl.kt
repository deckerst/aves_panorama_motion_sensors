package finaldev.motion_sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel

class StreamHandlerImpl(
    private val sensorManager: SensorManager,
    sensorType: Int,
    private var interval: Int = SensorManager.SENSOR_DELAY_NORMAL,
) : EventChannel.StreamHandler, SensorEventListener {
    private val sensor = sensorManager.getDefaultSensor(sensorType)
    private var eventSink: EventChannel.EventSink? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        if (sensor != null) {
            eventSink = events
            sensorManager.registerListener(this, sensor, interval)
        }
    }

    override fun onCancel(arguments: Any?) {
        sensorManager.unregisterListener(this)
        eventSink = null
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val sensorValues = listOf(event!!.values[0], event.values[1], event.values[2])
        eventSink?.success(sensorValues)
    }

    fun setUpdateInterval(interval: Int) {
        this.interval = interval
        if (eventSink != null) {
            sensorManager.unregisterListener(this)
            sensorManager.registerListener(this, sensor, interval)
        }
    }
}
