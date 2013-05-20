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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
    implements OnTouchListener, OnCheckedChangeListener
{
    // Button ids

    static final int buttons[][] =
    {{R.id.button_22, R.id.button_23,
      R.id.button_24, R.id.button_25,
      R.id.button_26, R.id.button_27,
      R.id.button_28, R.id.button_29,
      R.id.button_30, R.id.button_31},
     {R.id.button_11, R.id.button_12,
      R.id.button_13, R.id.button_14,
      R.id.button_15, R.id.button_16,
      R.id.button_17, R.id.button_18,
      R.id.button_19, R.id.button_20,
      R.id.button_21},
     {R.id.button_1, R.id.button_2,
      R.id.button_3, R.id.button_4,
      R.id.button_5, R.id.button_6,
      R.id.button_7, R.id.button_8,
      R.id.button_9, R.id.button_10}};

    // Bass button ids

    static final int basses[] =
    {R.id.bass_1, R.id.bass_2,
     R.id.bass_3, R.id.bass_4,
     R.id.bass_5, R.id.bass_6,
     R.id.bass_7, R.id.bass_8,
     R.id.bass_9, R.id.bass_10,
     R.id.bass_11, R.id.bass_12};

    // List of key offset values

    static final int keyvals[][] =
    {{ 3, -2, -7},  // F/Bb/Eb
     { 5,  0, -5},  // G/C/F
     { 7,  2, -3},  // A/D/G
     { 7,  2,  1},  // C#/D/G
     { 1,  0, -1},  // B/C/C#
     { 1,  0, -1},  // C System
     { 2,  0, -2}}; // B System

    //	    Eb	Bb   F	 C   G	 D   A
    //	   { 3, -2,  5,	 0, -5,	 2, -3};

    // Types

    static final int DIATONIC = 0;
    static final int CHROMATIC = 1;

    static final int types[] =
    {DIATONIC, DIATONIC, DIATONIC, DIATONIC,
     DIATONIC, CHROMATIC, CHROMATIC};

    // Midi notes for C Diatonic, G Chromatic

    static final byte notes[][][] =
    {{{52, 57}, // C Diatonic
      {55, 59},
      {60, 62},
      {64, 65},
      {67, 69},
      {72, 71},
      {76, 74},
      {79, 77},
      {84, 81},
      {88, 83},
      {91, 86}},
     {{55, 55}, // G Chromatic
      {58, 58},
      {61, 61},
      {64, 64},
      {67, 67},
      {70, 70},
      {73, 73},
      {76, 76},
      {79, 79},
      {82, 82},
      {85, 85}}};

    // Chords

    static final byte chords[][][][] =
    {
	// F/Bb/Eb

	{{{41, 53}, {36, 48}}, {{65, 60}, {60, 67}},  //  F/C
	 {{38, 50}, {43, 55}}, {{62, 69}, {67, 62}},  //  D/G
	 {{46, 58}, {41, 53}}, {{70, 65}, {65, 60}},  // Bb/F
	 {{43, 55}, {36, 48}}, {{67, 62}, {60, 67}},  //  G/C
	 {{39, 51}, {46, 58}}, {{63, 70}, {70, 65}},  // Eb/Bb
	 {{44, 56}, {44, 56}}, {{68, 63}, {68, 63}}}, // Ab/Ab

	// G/C/F

	{{{43, 55}, {38, 50}}, {{67, 62}, {62, 69}},  //  G/D
	 {{40, 52}, {45, 57}}, {{64, 71}, {69, 64}},  //  E/A
	 {{36, 48}, {43, 55}}, {{60, 67}, {67, 62}},  //  C/G
	 {{45, 57}, {38, 50}}, {{69, 64}, {62, 69}},  //  A/D
	 {{41, 53}, {36, 48}}, {{65, 60}, {60, 67}},  //  F/C
	 {{46, 58}, {46, 58}}, {{70, 65}, {70, 65}}}, // Bb/Bb

	// A/D/G

	{{{45, 57}, {40, 52}}, {{69, 64}, {64, 71}},  //  A/E
	 {{42, 54}, {47, 59}}, {{66, 61}, {71, 66}},  // F#/B
	 {{38, 50}, {45, 57}}, {{62, 69}, {69, 64}},  //  D/A
	 {{47, 59}, {40, 52}}, {{71, 66}, {64, 71}},  //  B/E
	 {{43, 55}, {38, 50}}, {{67, 62}, {62, 69}},  //  G/D
	 {{36, 48}, {36, 48}}, {{60, 67}, {60, 67}}}, //  C/C

	// C#/D/G

	{{{45, 57}, {40, 52}}, {{69, 64}, {64, 71}},  //  A/E
	 {{42, 54}, {47, 59}}, {{66, 61}, {71, 66}},  // F#/B
	 {{38, 50}, {45, 57}}, {{62, 69}, {69, 64}},  //  D/A
	 {{47, 59}, {40, 52}}, {{71, 66}, {64, 71}},  //  B/E
	 {{43, 55}, {38, 50}}, {{67, 62}, {62, 69}},  //  G/D
	 {{36, 48}, {36, 48}}, {{60, 67}, {60, 67}}}, //  C/C

	// B/C/C#

	{{{42, 54}, {42, 54}}, {{47, 59}, {47, 59}},  // F#/B
	 {{40, 52}, {40, 52}}, {{45, 57}, {45, 57}},  //  E/A
	 {{38, 50}, {38, 50}}, {{43, 55}, {43, 55}},  //  D/G
	 {{36, 48}, {36, 48}}, {{41, 53}, {41, 53}},  //  C/F
	 {{46, 58}, {46, 58}}, {{39, 51}, {39, 51}},  // Bb/Eb
	 {{44, 56}, {44, 56}}, {{37, 49}, {37, 49}}}, // Ab/Db

	// C System

	{{{42, 54}, {42, 54}}, {{47, 59}, {47, 59}},  // F#/B
	 {{40, 52}, {40, 52}}, {{45, 57}, {45, 57}},  //  E/A
	 {{38, 50}, {38, 50}}, {{43, 55}, {43, 55}},  //  D/G
	 {{36, 48}, {36, 48}}, {{41, 53}, {41, 53}},  //  C/F
	 {{46, 58}, {46, 58}}, {{39, 51}, {39, 51}},  // Bb/Eb
	 {{44, 56}, {44, 56}}, {{37, 49}, {37, 49}}}, // Ab/Db

	// B System

	{{{42, 54}, {42, 54}}, {{47, 59}, {47, 59}},  // F#/B
	 {{40, 52}, {40, 52}}, {{45, 57}, {45, 57}},  //  E/A
	 {{38, 50}, {38, 50}}, {{43, 55}, {43, 55}},  //  D/G
	 {{36, 48}, {36, 48}}, {{41, 53}, {41, 53}},  //  C/F
	 {{46, 58}, {46, 58}}, {{39, 51}, {39, 51}},  // Bb/Eb
	 {{44, 56}, {44, 56}}, {{37, 49}, {37, 49}}}, // Ab/Db
    };

    // Keyboard notes

    static final String notetops[][][] =
    {
	// F/Bb/Eb

	{{"G", "Bb", "Eb", "G", "Bb", "Eb", "G", "Bb", "Eb", "G"},
	 {"D", "F", "Bb", "D", "F", "Bb", "D", "F", "Bb", "D", "F"},
	 {"C", "F", "A", "C", "F", "A", "C", "F", "A", "C"}},

	// G/C/F

	{{"A", "C", "F", "A", "C", "F", "A", "C", "F", "A"},
	 {"E", "G", "C", "E", "G", "C", "E", "G", "C", "E", "G"},
	 {"D", "G", "B", "D", "G", "B", "D", "G", "B", "D"}},

	// A/D/G

	{{"B", "D", "G", "B", "D", "G", "B", "D", "G", "B"},
	 {"F#", "A", "D", "F#", "A", "D", "F#", "A", "D", "F#", "A"},
	 {"E", "A", "C#", "E", "A", "C#", "E", "A", "C#", "E"}},

	// C#/D/G

	{{"B", "D", "G", "B", "D", "G", "B", "D", "G", "B"},
	 {"F#", "A", "D", "F#", "A", "D", "F#", "A", "D", "F#", "A"},
	 {"G#", "C#", "F", "G#", "C#", "F", "G#", "C#", "F", "G#"}},

	// B/C/C#

	{{"F", "G#", "C#", "F", "G#", "C#", "F", "G#", "C#", "F"},
	 {"E", "G", "C", "E", "G", "C", "E", "G", "C", "E", "G"},
	 {"F#", "B", "D#", "F#", "B", "D#", "F#", "B", "D#", "F#"}},

	// C System

	{{"Ab", "B", "D", "F", "Ab", "B", "D", "F", "Ab", "B"},
	 {"G", "Bb", "C#", "E", "G", "Bb", "C#", "E", "G", "Bb", "C#"},
	 {"A", "C", "Eb", "F#", "A", "C", "Eb", "F#", "A", "C"}},

	// B System

	{{"A", "C", "Eb", "F#", "A", "C", "Eb", "F#", "A", "C"},
	 {"G", "Bb", "C#", "E", "G", "Bb", "C#", "E", "G", "Bb", "C#"},
	 {"Ab", "B", "D", "F", "Ab", "B", "D", "F", "Ab", "B"}}
    };

    // Hilites

    static final boolean hilites[][][] =
    {
	// F/Bb/Eb

	{},

	// G/C/F

	{},

	// A/D/G

	{},

	// C#/D/G

	{},

	// B/C/C#

	{},

	// C System

	{{true, false, false, false, true, false, false, false, true, false},
	 {false, true, true, false, false, true, true, false, false, true, true},
	 {false, false, true, true, false, false, true, true, false, false}},

	// B system

	{{false, false, true, true, false, false, true, true, false, false},
	 {false, true, true, false, false, true, true, false, false, true, true},
	 {true, false, false, false, true, false, false, false, true, false}}
    };

    // Midi codes

    static final int noteOff = 0x80;
    static final int noteOn  = 0x90;
    static final int change  = 0xc0;

    // Preferences

    final static String PREF_INSTRUMENT = "pref_instrument";
    final static String PREF_REVERSE = "pref_reverse";
    final static String PREF_FASCIA = "pref_fascia";
    final static String PREF_KEY = "pref_key";

    // Fascias

    final static int fascias[] =
    {R.drawable.bg_onyx, R.drawable.bg_cherry,
     R.drawable.bg_rosewood,  R.drawable.bg_olivewood};

    // Button states

    boolean buttonStates[][] =
    {{false, false, false, false, false, false,
      false, false, false, false, false},
     {false, false, false, false, false, false,
      false, false, false, false, false},
     {false, false, false, false, false, false,
      false, false, false, false, false}};

    boolean bassStates[] =
    {false, false, false, false, false, false,
     false, false, false, false, false, false};

    boolean bellows = false;
    boolean reverse = false;

    // Status

    int instrument;
    int volume;
    int fascia;
    int type;
    int key;

    // Audio

    Audio audio;

    // Views

    TextView keyView;
    Switch revView;
    Toast toast;

    // On create

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	// Add custom view to action bar

	ActionBar actionBar = getActionBar();
	actionBar.setCustomView(R.layout.text_view);
	actionBar.setDisplayShowCustomEnabled(true);

	keyView = (TextView)actionBar.getCustomView();

	// Get preferences

	getPreferences();

	// Create audio

	audio = new Audio();

	setListener();
    }

    // On create option menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    // On resume

    @Override
    protected void onResume()
    {
	super.onResume();

	// Get preferences

	getPreferences();

	// Start audio

	if (audio != null)
	    audio.start();
    }

    // On pause

    @Override
    protected void onPause()
    {
	super.onPause();

	// Save preferences

	savePreferences();

	// Stop audio

	if (audio != null)
	    audio.stop();
    }

    // On options item

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	// Get id

	int id = item.getItemId();
	switch (id)
	{
	    // Settings

	case R.id.settings:
	    Intent intent = new Intent(this, SettingsActivity.class);
	    startActivity(intent);

	    return true;

	default:
	    return false;
	}
    }

    // On touch

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
	int action = event.getAction();
	int id = v.getId();

	switch (action)
	{
	    // Down

	case MotionEvent.ACTION_DOWN:
	    switch (id)
	    {
	    case R.id.bellows:
		return onBellowsDown(v, event);

	    default:
		return onButtonDown(v, event);
	    }

	    // Up

	case MotionEvent.ACTION_UP:
	    switch (id)
	    {
	    case R.id.bellows:
		return onBellowsUp(v, event);

	    default:
		return onButtonUp(v, event);
	    }

	default:
	    return false;
	}
    }

    // On checked changed

    @Override
    public void onCheckedChanged(CompoundButton button,
				 boolean isChecked)
    {
	int id = button.getId();

	switch (id)
	{
	    // Reverse switch

	case R.id.reverse:
	    reverse = isChecked;

	    if (reverse)
		showToast(R.string.buttons_reversed);

	    else
		showToast(R.string.buttons_normal);
	    break;

	default:
	    return;
	}
    }

    // Save preferences

    void savePreferences()
    {
	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(this);

	Editor editor = preferences.edit();

	editor.putBoolean(PREF_REVERSE, reverse);

	editor.commit();
    }

    // Get preferences

    void getPreferences()
    {
	// Load preferences

	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(this);

	// Set preferences

	instrument =
	    Integer.parseInt(preferences.getString(PREF_INSTRUMENT, "21"));
	fascia =
	    Integer.parseInt(preferences.getString(PREF_FASCIA, "0"));
	key =
	    Integer.parseInt(preferences.getString(PREF_KEY, "2"));

	// Set type from key

	type = types[key];

	// Set key text

	Resources resources = getResources();
	String keys[] = resources.getStringArray(R.array.pref_key_entries);

	keyView.setText(keys[key]);

	// Set reverse

	reverse = preferences.getBoolean(PREF_REVERSE, false);

	// Set reverse switch

	if (revView != null)
	    revView.setChecked(reverse);

	// Set fascia

	View v = findViewById(R.id.fascia);

	if (v != null)
	    v.setBackgroundResource(fascias[fascia]);
    }

    // On bellows down

    private boolean onBellowsDown(View v, MotionEvent event)
    {
	if (!bellows)
	{
	    bellows = true;

	    // Change all notes

	    for (int i = 0; i < buttons.length; i++)
	    {
		for (int j = 0; j < buttons[i].length; j++)
		{
		    if (buttonStates[i][j])
		    {
			int k = 0;

			switch (i)
			{
			case 0:
			    k = (reverse)? buttons[i].length - j - 2: j;
			    break;

			case 1:
			    k = (reverse)? buttons[i].length - j - 1: j;
			    break;

			case 2:
			    k = (reverse)? buttons[i].length - j - 1: j + 1;
			    break;
			}

			int note = notes[type][k][!bellows? 1: 0] +
			    keyvals[key][i];

			// Stop note

			audio.writeNote(noteOff + i, note, volume);

			note = notes[type][k][bellows? 1: 0] +
			    keyvals[key][i];

			// Play note

			audio.writeNote(noteOn + i, note, volume);
		    }
		}
	    }

	    for (int i = 0; i < basses.length; i++)
	    {
		if (bassStates[i])
		{
		    // Play chord

		    int k = (reverse)? basses.length - i - 1: i;

		    int note =	chords[key][k][!bellows? 1: 0][0];
		    audio.writeNote(noteOff + 3, note, volume);

		    note =  chords[key][k][!bellows? 1: 0][1];
		    audio.writeNote(noteOff + 3, note, volume);

		    note =  chords[key][k][bellows? 1: 0][0];
		    audio.writeNote(noteOn + 3, note, volume);

		    note =  chords[key][k][bellows? 1: 0][1];
		    audio.writeNote(noteOn + 3, note, volume);
		}
	    }
	}

	return false;
    }

    private boolean onBellowsUp(View v, MotionEvent event)
    {
	if (bellows)
	{
	    bellows = false;

	    // Change all notes

	    for (int i = 0; i < buttons.length; i++)
	    {
		for (int j = 0; j < buttons[i].length; j++)
		{
		    if (buttonStates[i][j])
		    {
			int k = 0;

			switch (i)
			{
			case 0:
			    k = (reverse)? buttons[i].length - j - 2: j;
			    break;

			case 1:
			    k = (reverse)? buttons[i].length - j - 1: j;
			    break;

			case 2:
			    k = (reverse)? buttons[i].length - j - 1: j + 1;
			    break;
			}

			int note = notes[type][k][!bellows? 1: 0] +
			    keyvals[key][i];

			// Stop note

			audio.writeNote(noteOff + i, note, volume);

			note = notes[type][k][bellows? 1: 0] +
			    keyvals[key][i];

			// Play note

			audio.writeNote(noteOn + i, note, volume);
		    }
		}
	    }

	    for (int i = 0; i < basses.length; i++)
	    {
		if (bassStates[i])
		{
		    // Play chord

		    int k = (reverse)? basses.length - i - 1: i;

		    int note =	chords[key][k][!bellows? 1: 0][0];
		    audio.writeNote(noteOff + 3, note, volume);

		    note =  chords[key][k][!bellows? 1: 0][1];
		    audio.writeNote(noteOff + 3, note, volume);

		    note =  chords[key][k][bellows? 1: 0][0];
		    audio.writeNote(noteOn + 3, note, volume);

		    note =  chords[key][k][bellows? 1: 0][1];
		    audio.writeNote(noteOn + 3, note, volume);
		}
	    }
	}

	return false;
    }

    private boolean onButtonDown(View v, MotionEvent event)
    {
	int id = v.getId();

	// Check melody buttons

	for (int i = 0; i < buttons.length; i++)
	{
	    for (int j = 0; j < buttons[i].length; j++)
	    {
		if (id == buttons[i][j] && !buttonStates[i][j])
		{
		    buttonStates[i][j] = true;

		    // Play note

		    int k = 0;

		    switch (i)
		    {
		    case 0:
			k = (reverse)? buttons[i].length - j - 1: j;
			break;

		    case 1:
			k = (reverse)? buttons[i].length - j - 1: j;
			break;

		    case 2:
			k = (reverse)? buttons[i].length - j: j + 1;
			break;
		    }

		    int note =	notes[type][k][bellows? 1: 0] + keyvals[key][i];
		    audio.writeNote(noteOn + i, note, volume);
		    return false;
		}
	    }
	}

	// Check bass buttons

	for (int i = 0; i < basses.length; i++)
	{
	    if (id == basses[i] && !bassStates[i])
	    {
		bassStates[i] = true;

		// Play chord

		int k = (reverse)? basses.length - i - 1: i;

		int note = chords[key][k][bellows? 1: 0][0];
		audio.writeNote(noteOn + 3, note, volume);

		note = chords[key][k][bellows? 1: 0][1];
		audio.writeNote(noteOn + 3, note, volume);

		return false;
	    }
	}

	return false;
    }

    private boolean onButtonUp(View v, MotionEvent event)
    {
	int id = v.getId();

	for (int i = 0; i < buttons.length; i++)
	{
	    for (int j = 0; j < buttons[i].length; j++)
	    {
		if (id == buttons[i][j] && buttonStates[i][j])
		{
		    buttonStates[i][j] = false;

		    // Stop note

		    int k = 0;

		    switch (i)
		    {
		    case 0:
			k = (reverse)? buttons[i].length - j - 1: j;
			break;

		    case 1:
			k = (reverse)? buttons[i].length - j - 1: j;
			break;

		    case 2:
			k = (reverse)? buttons[i].length - j: j + 1;
			break;
		    }

		    int note =	notes[type][k][bellows? 1: 0] + keyvals[key][i];
		    audio.writeNote(noteOff + i, note, 0);

		    return false;
		}
	    }
	}

	// Check bass buttons

	for (int i = 0; i < basses.length; i++)
	{
	    if (id == basses[i] && bassStates[i])
	    {
		bassStates[i] = false;

		// Stop chord

		int k = (reverse)? basses.length - i - 1: i;

		int note = chords[key][k][bellows? 1: 0][0];
		audio.writeNote(noteOff + 3, note, volume);

		note = chords[key][k][bellows? 1: 0][1];
		audio.writeNote(noteOff + 3, note, volume);

		return false;
	    }
	}

	return false;
    }

    // Show toast.

    void showToast(int key)
    {
	Resources resources = getResources();
	String text = resources.getString(key);

	showToast(text);
    }

    void showToast(String text)
    {
	// Cancel the last one

	if (toast != null)
	    toast.cancel();

	// Make a new one

	toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();
    }

    // Set listener

    private void setListener()
    {
	View v;

	// Set listener for all buttons

	for (int i = 0; i < buttons.length; i++)
	{
	    for (int j = 0; j < buttons[i].length; j++)
	    {
		v = findViewById(buttons[i][j]);
		if (v != null)
		    v.setOnTouchListener(this);
	    }
	}

	// Bass buttons

	for (int i = 0; i < basses.length; i++)
	{
	    v = findViewById(basses[i]);
	    if (v != null)
		v.setOnTouchListener(this);
	}

	// Bellows

	v = findViewById(R.id.bellows);
	if (v != null)
	    v.setOnTouchListener(this);

	// Reverse switch

	revView = (Switch)findViewById(R.id.reverse);
	if (revView != null)
	    revView.setOnCheckedChangeListener(this);
    }

    // Audio

    protected class Audio implements Runnable
    {
	static final int SAMPLE_RATE = 22050;
	static final int BUFFER_SIZE = 4096;

	protected Thread thread;
	protected AudioTrack audioTrack;

	protected short shortArray[];
	protected int config[];

	private byte changeMsg[];
	private byte noteMsg[];

	// Constructor

	protected Audio()
	{
	    // Set volume, let the user adjust the volume with the
	    // android volume buttons

	    volume = 127;

	    // Create midi message buffers, we need two because the
	    // Sonivox synthesizer won't accept three byte program
	    // change messages

	    changeMsg = new byte[2];
	    noteMsg = new byte[3];
	}

	// Start audio

	protected void start()
	{
	    // Start the thread

	    thread = new Thread(this, "Audio");
	    thread.start();
	}

	// Run

	@Override
	public void run()
	{
	    processAudio();
	}

	// Stop

	protected void stop()
	{
	    Thread t = thread;
	    thread = null;

	    // Wait for the thread to exit

	    while (t != null && t.isAlive())
		Thread.yield();
	}

	// Process Audio

	protected void processAudio()
	{
	    int status = 0;
	    int size = 0;

	    // Init midi

	    if ((size = midiInit()) == 0)
		return;

	    shortArray = new short[size];

	    // Create audio track

	    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT,
					BUFFER_SIZE, AudioTrack.MODE_STREAM);
	    if (audioTrack == null)
		return;

	    // Play track

	    audioTrack.play();

	    // Set instrument

	    for (int i = 0; i <= buttons.length; i++)
		writeChange(change + i, instrument);

	    // Keep running until stopped

	    while (thread != null)
	    {
		// Render the ausio

		if (midiRender(shortArray) == 0)
		    break;

		// Write audio to audiotrack

		status = audioTrack.write(shortArray, 0, shortArray.length);

		if (status < 0)
		    break;
	    }

	    // Render and write the last bit of audio

	    if (status > 0)
	    {
		if (midiRender(shortArray) > 0)
		    audioTrack.write(shortArray, 0, shortArray.length);
	    }

	    // Shut down audio

	    midiShutdown();
	    audioTrack.release();
	}

	// Write program change message, two bytes

	boolean writeChange(int m, int i)
	{
	    changeMsg[0] = (byte)m;
	    changeMsg[1] = (byte)i;

	    return midiWrite(changeMsg);
	}

	// Write note message, three bytes

	boolean writeNote(int m, int n, int v)
	{
	    noteMsg[0] = (byte)m;
	    noteMsg[1] = (byte)n;
	    noteMsg[2] = (byte)v;

	    return midiWrite(noteMsg);
	}
    }

    // Native midi methods

    protected native int     midiInit();
    protected native int     midiRender(short a[]);
    protected native boolean midiWrite(byte a[]);
    protected native boolean midiShutdown();

    // Load midi library

    static
    {
	System.loadLibrary("midi");
    }

}
