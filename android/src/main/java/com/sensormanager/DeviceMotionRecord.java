package com.sensormanager;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.MatrixUtils;

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
