package finaldev.motion_sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel

class RotationVectorStreamHandler(
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
        val matrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(matrix, event!!.values)
        if (matrix[7] > 1.0f) matrix[7] = 1.0f
        if (matrix[7] < -1.0f) matrix[7] = -1.0f
        val orientation = FloatArray(3)
        SensorManager.getOrientation(matrix, orientation)
        val sensorValues = listOf(-orientation[0], -orientation[1], orientation[2])
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
