package com.sensormanager;

import android.hardware.Sensor;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

public class DeviceMotionRecord extends SensorRecord {

    private static final String DEVICEMOTION_FIELD_KEY = "deviceMotion";
    private static final String DEVICEMOTION_EVENT_KEY = "DeviceMotionData";

    public DeviceMotionRecord(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "DeviceMotion";
    }

    @Override
    protected int getSensorType() {
        return Sensor.TYPE_GAME_ROTATION_VECTOR;
    }

    @Override
    protected String getEventNameKey() {
        return DEVICEMOTION_EVENT_KEY;
    }

    @Override
    protected String getDataMapKey() {
        return DEVICEMOTION_FIELD_KEY;
    }

    protected double[][] getMFromRotationVector(double[] rotationVector) {
        double sinThetaSur2 = MatrixUtils.createRealMatrix(rotationVector).getNorm();
        double cosThetaSur2 = Math.sqrt(1 - sinThetaSur2 * sinThetaSur2);

        double[] q = [
            cosThetaSur2,
            rotationVector[0],
            rotationVector[1],
            rotationVector[2]
        ];

        double s = 1 / MatrixUtils.createRealMatrix(q).getNorm();
        double qr = q[0];
        double qi = q[1];
        double qj = q[2];
        double qk = q[3];

        double R11 = 1 - 2 * s * (qj * qj + qk * qk);
        double R12 = 2 * s * (qi * qj - qk * qr);
        double R13 = 2 * s * (qi * qk + qj * qr);

        double R21 = 2 * s * (qi * qj + qk * qr);
        double R22 = 1 - 2 * s * (qi * qi + qk * qk);
        double R23 = 2 * s * (qj * qk - qi * qr);

        double R31 = 2 * s * (qi * qk - qj * qr);
        double R32 = 2 * s * (qj * qk + qi * qr);
        double R33 = 1 - 2 * s * (qi * qi + qj * qj);

        double[][] matrixData = {
            {R11,R12,R13},
            {R21,R22,R23},
            {R31,R32,R33}
        };

        return MatrixUtils.createRealMatrix(matrixData).transpose().getData();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == getSensorType()) {
            WritableMap map = Arguments.createMap();
            WritableMap rotationMap = Arguments.createMap();

            double[][] currentRotationMatrix = getMFromRotationVector(sensorEvent.values);

            rotationMap.putDouble("m11", currentRotationMatrix[0][0]);
            rotationMap.putDouble("m12", currentRotationMatrix[0][1]);
            rotationMap.putDouble("m13", currentRotationMatrix[0][2]);

            rotationMap.putDouble("m21", currentRotationMatrix[1][0]);
            rotationMap.putDouble("m22", currentRotationMatrix[1][1]);
            rotationMap.putDouble("m23", currentRotationMatrix[1][2]);

            rotationMap.putDouble("m31", currentRotationMatrix[2][0]);
            rotationMap.putDouble("m32", currentRotationMatrix[2][1]);
            rotationMap.putDouble("m33", currentRotationMatrix[2][2]);

            map.putMap(getDataMapKey(), rotationMap);

            sendEvent(getEventNameKey(), map);
        }
    }

    /**
     * @param delaySeconds Delay in seconds and/or fractions of a second.
     */
    @ReactMethod
    public void setDeviceMotionUpdateInterval(double delaySeconds) {
        setUpdateDelay(delaySeconds);
    }

    /**
     * @return true if DeviceMotion exists on device, false if it does not exist and so could not be started.
     */
    @ReactMethod
    public void startDeviceMotionUpdates(Callback onStarted) {
        startUpdates(onStarted);
    }

    @ReactMethod
    public void stopDeviceMotionUpdates() {
        stopUpdates();
    }
}
