package finaldev.motion_sensors

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

// translate from https://github.com/fluttercommunity/plus_plugins/tree/main/packages/sensors_plus
/** MotionSensorsPlugin */
class MotionSensorsPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    companion object {
        private const val METHOD_CHANNEL_NAME = "motion_sensors/method"
        private const val ACCELEROMETER_CHANNEL_NAME = "motion_sensors/accelerometer"
        private const val GYROSCOPE_CHANNEL_NAME = "motion_sensors/gyroscope"
        private const val MAGNETOMETER_CHANNEL_NAME = "motion_sensors/magnetometer"
        private const val USER_ACCELEROMETER_CHANNEL_NAME = "motion_sensors/user_accelerometer"
        private const val ORIENTATION_CHANNEL_NAME = "motion_sensors/orientation"
        private const val ABSOLUTE_ORIENTATION_CHANNEL_NAME = "motion_sensors/absolute_orientation"
        private const val SCREEN_ORIENTATION_CHANNEL_NAME = "motion_sensors/screen_orientation"
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var messenger: BinaryMessenger

    private var methodChannel: MethodChannel? = null
    private var accelerometerChannel: EventChannel? = null
    private var gyroscopeChannel: EventChannel? = null
    private var magnetometerChannel: EventChannel? = null
    private var userAccelerometerChannel: EventChannel? = null
    private var orientationChannel: EventChannel? = null
    private var absoluteOrientationChannel: EventChannel? = null
    private var screenOrientationChannel: EventChannel? = null

    private var accelerationStreamHandler: StreamHandlerImpl? = null
    private var gyroScopeStreamHandler: StreamHandlerImpl? = null
    private var magnetometerStreamHandler: StreamHandlerImpl? = null
    private var userAccelerationStreamHandler: StreamHandlerImpl? = null
    private var orientationStreamHandler: RotationVectorStreamHandler? = null
    private var absoluteOrientationStreamHandler: RotationVectorStreamHandler? = null
    private var screenOrientationStreamHandler: ScreenOrientationStreamHandler? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        sensorManager = binding.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        messenger = binding.binaryMessenger
        setupEventChannels()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        teardownEventChannels()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        setupEventChannelsForActivity(binding.activity)
    }

    override fun onDetachedFromActivity() {
        teardownEventChannelsForActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        setupEventChannelsForActivity(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        teardownEventChannelsForActivity()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "isSensorAvailable" -> result.success(sensorManager.getSensorList(call.arguments as Int).isNotEmpty())
            "setSensorUpdateInterval" -> setSensorUpdateInterval(call.argument<Int>("sensorType")!!, call.argument<Int>("interval")!!)
            else -> result.notImplemented()
        }
    }

    private fun setupEventChannels() {
        methodChannel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        methodChannel!!.setMethodCallHandler(this)

        accelerometerChannel = EventChannel(messenger, ACCELEROMETER_CHANNEL_NAME)
        accelerationStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_ACCELEROMETER)
        accelerometerChannel!!.setStreamHandler(accelerationStreamHandler!!)

        userAccelerometerChannel = EventChannel(messenger, USER_ACCELEROMETER_CHANNEL_NAME)
        userAccelerationStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_LINEAR_ACCELERATION)
        userAccelerometerChannel!!.setStreamHandler(userAccelerationStreamHandler!!)

        gyroscopeChannel = EventChannel(messenger, GYROSCOPE_CHANNEL_NAME)
        gyroScopeStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_GYROSCOPE)
        gyroscopeChannel!!.setStreamHandler(gyroScopeStreamHandler!!)

        magnetometerChannel = EventChannel(messenger, MAGNETOMETER_CHANNEL_NAME)
        magnetometerStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_MAGNETIC_FIELD)
        magnetometerChannel!!.setStreamHandler(magnetometerStreamHandler!!)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            orientationChannel = EventChannel(messenger, ORIENTATION_CHANNEL_NAME)
            orientationStreamHandler = RotationVectorStreamHandler(sensorManager, Sensor.TYPE_GAME_ROTATION_VECTOR)
            orientationChannel!!.setStreamHandler(orientationStreamHandler!!)
        }

        absoluteOrientationChannel = EventChannel(messenger, ABSOLUTE_ORIENTATION_CHANNEL_NAME)
        absoluteOrientationStreamHandler = RotationVectorStreamHandler(sensorManager, Sensor.TYPE_ROTATION_VECTOR)
        absoluteOrientationChannel!!.setStreamHandler(absoluteOrientationStreamHandler!!)
    }

    private fun teardownEventChannels() {
        methodChannel?.setMethodCallHandler(null)
        accelerometerChannel?.setStreamHandler(null)
        userAccelerometerChannel?.setStreamHandler(null)
        gyroscopeChannel?.setStreamHandler(null)
        magnetometerChannel?.setStreamHandler(null)
        orientationChannel?.setStreamHandler(null)
        absoluteOrientationChannel?.setStreamHandler(null)
    }

    private fun setupEventChannelsForActivity(activity: Activity) {
        screenOrientationChannel = EventChannel(messenger, SCREEN_ORIENTATION_CHANNEL_NAME)
        screenOrientationStreamHandler = ScreenOrientationStreamHandler(activity, sensorManager, Sensor.TYPE_ACCELEROMETER)
        screenOrientationChannel!!.setStreamHandler(screenOrientationStreamHandler)
    }

    private fun teardownEventChannelsForActivity() {
        screenOrientationChannel?.setStreamHandler(null)
    }

    private fun setSensorUpdateInterval(sensorType: Int, interval: Int) {
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> accelerationStreamHandler!!.setUpdateInterval(interval)
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerStreamHandler!!.setUpdateInterval(interval)
            Sensor.TYPE_GYROSCOPE -> gyroScopeStreamHandler!!.setUpdateInterval(interval)
            Sensor.TYPE_LINEAR_ACCELERATION -> userAccelerationStreamHandler!!.setUpdateInterval(interval)
            Sensor.TYPE_GAME_ROTATION_VECTOR -> orientationStreamHandler!!.setUpdateInterval(interval)
            Sensor.TYPE_ROTATION_VECTOR -> absoluteOrientationStreamHandler!!.setUpdateInterval(interval)
        }
    }
}
