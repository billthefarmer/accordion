package org.billthefarmer.accordion;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MainActivity extends Activity
    implements OnTouchListener
{
    // List of midi instruments

    static final String instruments[] =
    {" Acoustic Grand Piano", " Bright Acoustic Piano",
     " Electric Grand Piano", " Honky-tonk Piano",
     " Electric Piano 1", " Electric Piano 2", " Harpsichord",
     " Clavi", " Celesta", " Glockenspiel", " Music Box",
     " Vibraphone", " Marimba", " Xylophone", " Tubular Bells",
     " Dulcimer", " Drawbar Organ", " Percussive Organ",
     " Rock Organ", " Church Organ", " Reed Organ",
     " Accordion", " Harmonica", " Tango Accordion",
     " Acoustic Guitar (nylon)", " Acoustic Guitar (steel)",
     " Electric Guitar (jazz)", " Electric Guitar (clean)",
     " Electric Guitar (muted)", " Overdriven Guitar",
     " Distortion Guitar", " Guitar harmonics",
     " Acoustic Bass", " Electric Bass (finger)",
     " Electric Bass (pick)", " Fretless Bass",
     " Slap Bass 1", " Slap Bass 2", " Synth Bass 1",
     " Synth Bass 2", " Violin", " Viola", " Cello",
     " Contrabass", " Tremolo Strings", " Pizzicato Strings",
     " Orchestral Harp", " Timpani", " String Ensemble 1",
     " String Ensemble 2", " SynthStrings 1", " SynthStrings 2",
     " Choir Aahs", " Voice Oohs", " Synth Voice",
     " Orchestra Hit", " Trumpet", " Trombone", " Tuba",
     " Muted Trumpet", " French Horn", " Brass Section",
     " SynthBrass 1", " SynthBrass 2", " Soprano Sax",
     " Alto Sax", " Tenor Sax", " Baritone Sax", " Oboe",
     " English Horn", " Bassoon", " Clarinet", " Piccolo",
     " Flute", " Recorder", " Pan Flute", " Blown Bottle",
     " Shakuhachi", " Whistle", " Ocarina", " Lead 1 (square)",
     " Lead 2 (sawtooth)", " Lead 3 (calliope)", " Lead 4 (chiff)",
     " Lead 5 (charang)", " Lead 6 (voice)", " Lead 7 (fifths)",
     " Lead 8 (bass + lead)", " Pad 1 (new age)", " Pad 2 (warm)",
     " Pad 3 (polysynth)", " Pad 4 (choir)", " Pad 5 (bowed)",
     " Pad 6 (metallic)", " Pad 7 (halo)", " Pad 8 (sweep)",
     " FX 1 (rain)", " FX 2 (soundtrack)", " FX 3 (crystal)",
     " FX 4 (atmosphere)", " FX 5 (brightness)", " FX 6 (goblins)",
     " FX 7 (echoes)", " FX 8 (sci-fi)", " Sitar", " Banjo",
     " Shamisen", " Koto", " Kalimba", " Bag pipe", " Fiddle",
     " Shanai", " Tinkle Bell", " Agogo", " Steel Drums",
     " Woodblock", " Taiko Drum", " Melodic Tom", " Synth Drum",
     " Reverse Cymbal", " Guitar Fret Noise", " Breath Noise",
     " Seashore", " Bird Tweet", " Telephone Ring",
     " Helicopter", " Applause", " Gunshot"};

    // Button ids

    static final int buttons[][] =
    {{R.id.button_1, R.id.button_2,
      R.id.button_3, R.id.button_4,
      R.id.button_5, R.id.button_6,
      R.id.button_7, R.id.button_8,
      R.id.button_9, R.id.button_10,
      0},
     {R.id.button_11, R.id.button_12,
      R.id.button_13, R.id.button_14,
      R.id.button_15, R.id.button_16,
      R.id.button_17, R.id.button_18,
      R.id.button_19, R.id.button_20,
      R.id.button_21},
     {R.id.button_22, R.id.button_23,
      R.id.button_24, R.id.button_25,
      R.id.button_26, R.id.button_27,
      R.id.button_28, R.id.button_29,
      R.id.button_30, R.id.button_31,
      0}};

    // Bass button ids

    static final int basses[] =
    {R.id.bass_1, R.id.bass_2,
     R.id.bass_3, R.id.bass_4,
     R.id.bass_5, R.id.bass_6,
     R.id.bass_7, R.id.bass_8,
     R.id.bass_9, R.id.bass_10,
     R.id.bass_11, R.id.bass_12};

    // List of keys and offset values

    static final String keys[] =
    {" F/Bb/Eb", " G/C/F", " A/D/G", " C#/D/G", " B/C/C#",
     " C System", " B System"};

    static final int keyvals[][] =
    {{ 3, -2, -7},  // F/Bb/Eb
     { 5,  0, -5},  // G/C/F
     { 7,  2, -3},  // A/D/G
     { 7,  2,  1},  // C#/D/G
     { 1,  0, -1},  // B/C/C#
     { 1,  0, -1},  // C System
     { 2,  0, -2}}; // B System

    //      Eb  Bb   F   C   G   D   A
    //     { 3, -2,  5,  0, -5,  2, -3};

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

	// b system

	{{false, false, true, true, false, false, true, true, false, false},
	 {false, true, true, false, false, true, true, false, false, true, true},
	 {true, false, false, false, true, false, false, false, true, false}}
    };

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

    boolean bellowsState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	setListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
	int action = event.getAction();
	int id = v.getId();

	switch (action)
	{
	case MotionEvent.ACTION_DOWN:
	    switch (id)
	    {
	    case R.id.bellows:
		break;
	    case R.id.button_1:
		break;
	    }
	    break;

	case MotionEvent.ACTION_UP:
	    switch (id)
	    {
	    case R.id.bellows:
		break;
	    case R.id.button_1:
		break;
	    }
	}
	return false;
    }

    private void setListener()
    {
	View v;

	v = findViewById(R.id.bellows);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_1);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_2);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_3);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_4);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_5);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_6);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_7);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_8);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_9);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_10);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_11);
	v.setOnTouchListener(this);

	v = findViewById(R.id.bass_12);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_1);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_2);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_3);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_4);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_5);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_6);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_7);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_8);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_9);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_10);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_11);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_12);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_13);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_14);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_15);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_16);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_17);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_18);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_19);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_20);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_21);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_22);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_23);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_24);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_25);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_26);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_27);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_28);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_29);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_30);
	v.setOnTouchListener(this);

	v = findViewById(R.id.button_31);
	v.setOnTouchListener(this);

    }
}
