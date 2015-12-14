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
    protected static final float EPSILON = 0.5f;

    private GyroscopeListener listener;
    private SensorManager sensorManager;
    private Sensor sensor;

    // Constructor

    public Gyroscope(Context context)
    {
	sensorManager =
	    (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
	sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    // Start

    public void start()
    {
	if (sensor != null)
	    sensorManager.registerListener(this, sensor,
					   SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Has gyro

    public boolean hasGyro()
    {
	if (sensor != null)
	    return true;

	return false;
    }

    // Stop

    public void stop()
    {
	sensorManager.unregisterListener(this);
    }

    // On sensor changed

    @Override
    public void onSensorChanged(SensorEvent event)
    {
	// Axis of the rotation sample, not normalized yet.
	float axisX = event.values[0];
	float axisY = event.values[1];
	float axisZ = event.values[2];

	// Calculate the angular speed of the sample
	float omegaMagnitude =
	    (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

	if (omegaMagnitude > EPSILON)
	{
	    // Log.d(TAG, "Omega: " + omegaMagnitude);
	    // Log.d(TAG, "Gyro: " + axisX + ", " + axisY + ", " + axisZ);

	    // Call listener

	    if (listener != null)
		listener.onGyroChange(event.values);
	}
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
