////////////////////////////////////////////////////////////////////////////////
//
//  Accordion - An Android Accordion written in Java.
//
//  Copyright (C) 2015	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.accordion;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

// Gyroscope

public class Gyroscope implements SensorEventListener
{
    private static final String TAG = "Gyroscope";
    private static final float EPSILON = 1.0f;

    private GyroscopeListener listener;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Activity main;

    private float timestamp;

    // Constructor

    public Gyroscope(Activity m)
    {
	main = m;
	sensorManager =
	    (SensorManager)main.getSystemService(Context.SENSOR_SERVICE);
	sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void start()
    {
	sensorManager.registerListener(this, sensor,
				       SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop()
    {
	sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event)
    {
	// This timestep's delta rotation to be multiplied by the current
	// rotation after computing it from the gyro sample data.
	if (timestamp != 0)
	{
	    // Axis of the rotation sample, not normalized yet.
	    float axisX = event.values[0];
	    float axisY = event.values[1];
	    float axisZ = event.values[2];

	    // Calculate the angular speed of the sample
	    float omegaMagnitude =
		(float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

	    // Normalize the rotation vector if it's big enough to get the
	    // axis (that is, EPSILON should represent your maximum allowable
	    // margin of error)
	    if (omegaMagnitude > EPSILON)
	    {
		// Log.d(TAG, "Omega Magnitude: " + omegaMagnitude);
		// Log.d(TAG, "Gyro: " + axisX + ", " + axisY + ", " + axisZ);

		if (listener != null)
		    listener.onGyroChange(event.values);
	    }
	}

	timestamp = event.timestamp;
    }

    // Unused

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Set gyroscope listener

    public void setGyroscopeListener(GyroscopeListener l)
    {
	listener = l;
    }

    // Listener interface

    public interface GyroscopeListener
    {
	public abstract void onGyroChange(float rotation[]);
    }
}
