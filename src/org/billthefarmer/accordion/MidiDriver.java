////////////////////////////////////////////////////////////////////////////////
//
//  Accordion - An Android Accordion written in Java.
//
//  Copyright (C) 2013	Bill Farmer
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

// MidiDriver

public class MidiDriver
{
    private OnMidiStartListener listener;

    // Constructor

    public MidiDriver()
    {
    }

    // Start midi

    public void start()
    {
	if (init() != true)
	    return;

	// Call listener

	if (listener != null)
	    listener.onMidiStart();
    }

    // Write program change message, two bytes

    public boolean writeChange(int m, int i)
    {
	byte changeMsg[] = new byte[2];

	changeMsg[0] = (byte)m;
	changeMsg[1] = (byte)i;

	write(changeMsg);
	return true;
    }

    // Write note message, three bytes

    public boolean writeNote(int m, int n, int v)
    {
	byte noteMsg[] = new byte[3];

	noteMsg[0] = (byte)m;
	noteMsg[1] = (byte)n;
	noteMsg[2] = (byte)v;

	write(noteMsg);
	return true;
    }

    // Stop

    public void stop()
    {
	shutdown();
    }

    // Set listener

    public void setOnMidiStartListener(OnMidiStartListener l)
    {
	listener = l;
    }

    // Listener interface

    public interface OnMidiStartListener
    {
	public abstract void onMidiStart();
    }

    // Native midi methods

    private native boolean init();
    private native boolean write(byte a[]);
    private native boolean shutdown();

    // Load midi library

    static
    {
	System.loadLibrary("midi");
    }
}
