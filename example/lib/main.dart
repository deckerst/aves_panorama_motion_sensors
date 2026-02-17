import 'package:flutter/material.dart';
import 'package:motion_sensors/motion_sensors.dart';
import 'package:vector_math/vector_math_64.dart' hide Colors;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final Vector3 _accelerometer = Vector3.zero();
  final Vector3 _gyroscope = Vector3.zero();
  final Vector3 _magnetometer = Vector3.zero();
  final Vector3 _userAccelerometer = Vector3.zero();
  final Vector3 _orientation = Vector3.zero();
  final Vector3 _absoluteOrientation = Vector3.zero();
  final Vector3 _absoluteOrientation2 = Vector3.zero();
  double? _screenOrientation = 0;

  int _groupValue = 1;

  @override
  void initState() {
    super.initState();
    motionSensors.gyroscope.listen((event) {
      setState(() {
        _gyroscope.setValues(event.x, event.y, event.z);
      });
    });
    motionSensors.accelerometer.listen((event) {
      setState(() {
        _accelerometer.setValues(event.x, event.y, event.z);
      });
    });
    motionSensors.userAccelerometer.listen((event) {
      setState(() {
        _userAccelerometer.setValues(event.x, event.y, event.z);
      });
    });
    motionSensors.magnetometer.listen((event) {
      setState(() {
        _magnetometer.setValues(event.x, event.y, event.z);
        var matrix = motionSensors.getRotationMatrix(_accelerometer, _magnetometer);
        _absoluteOrientation2.setFrom(motionSensors.getOrientation(matrix));
      });
    });
    motionSensors.isOrientationAvailable().then((available) {
      if (available) {
        motionSensors.orientation.listen((event) {
          setState(() {
            _orientation.setValues(event.yaw, event.pitch, event.roll);
          });
        });
      }
    });
    motionSensors.absoluteOrientation.listen((event) {
      setState(() {
        _absoluteOrientation.setValues(event.yaw, event.pitch, event.roll);
      });
    });
    motionSensors.screenOrientation.listen((event) {
      setState(() {
        _screenOrientation = event.angle;
      });
    });
  }

  void _setUpdateInterval(int groupValue, int interval) {
    motionSensors.accelerometerUpdateInterval = interval;
    motionSensors.userAccelerometerUpdateInterval = interval;
    motionSensors.gyroscopeUpdateInterval = interval;
    motionSensors.magnetometerUpdateInterval = interval;
    motionSensors.orientationUpdateInterval = interval;
    motionSensors.absoluteOrientationUpdateInterval = interval;
    setState(() {
      _groupValue = groupValue;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Motion Sensors'),
        ),
        body: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Text('Update Interval'),
              RadioGroup<int>(
                groupValue: _groupValue,
                onChanged: (value) {
                  if (value != null) {
                    _setUpdateInterval(value, Duration.microsecondsPerSecond ~/ value);
                  }
                },
                child: const Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Radio(value: 1),
                    Text('1 FPS'),
                    Radio(value: 30),
                    Text('30 FPS'),
                    Radio(value: 60),
                    Text('60 FPS'),
                  ],
                ),
              ),
              const Text('Accelerometer'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(_accelerometer.x.toStringAsFixed(4)),
                  Text(_accelerometer.y.toStringAsFixed(4)),
                  Text(_accelerometer.z.toStringAsFixed(4)),
                ],
              ),
              const Text('Magnetometer'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(_magnetometer.x.toStringAsFixed(4)),
                  Text(_magnetometer.y.toStringAsFixed(4)),
                  Text(_magnetometer.z.toStringAsFixed(4)),
                ],
              ),
              const Text('Gyroscope'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(_gyroscope.x.toStringAsFixed(4)),
                  Text(_gyroscope.y.toStringAsFixed(4)),
                  Text(_gyroscope.z.toStringAsFixed(4)),
                ],
              ),
              const Text('User Accelerometer'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(_userAccelerometer.x.toStringAsFixed(4)),
                  Text(_userAccelerometer.y.toStringAsFixed(4)),
                  Text(_userAccelerometer.z.toStringAsFixed(4)),
                ],
              ),
              const Text('Orientation'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(degrees(_orientation.x).toStringAsFixed(4)),
                  Text(degrees(_orientation.y).toStringAsFixed(4)),
                  Text(degrees(_orientation.z).toStringAsFixed(4)),
                ],
              ),
              const Text('Absolute Orientation'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(degrees(_absoluteOrientation.x).toStringAsFixed(4)),
                  Text(degrees(_absoluteOrientation.y).toStringAsFixed(4)),
                  Text(degrees(_absoluteOrientation.z).toStringAsFixed(4)),
                ],
              ),
              const Text('Orientation (accelerometer + magnetometer)'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(degrees(_absoluteOrientation2.x).toStringAsFixed(4)),
                  Text(degrees(_absoluteOrientation2.y).toStringAsFixed(4)),
                  Text(degrees(_absoluteOrientation2.z).toStringAsFixed(4)),
                ],
              ),
              const Text('Screen Orientation'),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: <Widget>[
                  Text(_screenOrientation!.toStringAsFixed(4)),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
