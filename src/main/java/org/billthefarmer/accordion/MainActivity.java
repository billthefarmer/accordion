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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.Locale;

// MainActivity
public class MainActivity extends Activity
    implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener,
    MidiDriver.OnMidiStartListener
{
    // Button ids
    private static final int buttons[][] =
    {
        {
            R.id.button_22, R.id.button_23,
            R.id.button_24, R.id.button_25,
            R.id.button_26, R.id.button_27,
            R.id.button_28, R.id.button_29,
            R.id.button_30, R.id.button_31
        },
        {
            R.id.button_11, R.id.button_12,
            R.id.button_13, R.id.button_14,
            R.id.button_15, R.id.button_16,
            R.id.button_17, R.id.button_18,
            R.id.button_19, R.id.button_20,
            R.id.button_21
        },
        {
            R.id.button_1, R.id.button_2,
            R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6,
            R.id.button_7, R.id.button_8,
            R.id.button_9, R.id.button_10
        }
    };

    // Bass button ids
    private static final int basses[][] =
    {
        {
            R.id.bass_1, R.id.bass_2,
            R.id.bass_3, R.id.bass_4,
            R.id.bass_5, R.id.bass_6
        },
        {
            R.id.bass_7, R.id.bass_8,
            R.id.bass_9, R.id.bass_10,
            R.id.bass_11, R.id.bass_12
        }
    };

    // List of key offset values
    private static final int keyvals[][] =
    {
        {3, -2, -7},  // F/Bb/Eb
        {5, 0, -5},  // G/C/F
        {7, 2, -3},  // A/D/G
        {7, 2, 1},  // C#/D/G
        {1, 0, -1},  // B/C/C#
        {1, 0, -1},  // C System
        {2, 0, -2}
    }; // B System

    //	    Eb	Bb   F	 C   G	 D   A
    //	   { 3, -2,  5,	 0, -5,	 2, -3};

    // Types
    private static final int DIATONIC = 0;
    private static final int CHROMATIC = 1;

    private static final int types[] =
    {
        DIATONIC, DIATONIC, DIATONIC, DIATONIC,
        DIATONIC, CHROMATIC, CHROMATIC
    };

    // Midi notes for C Diatonic, G Chromatic
    private static final byte notes[][][] =
    {
        {   {52, 57}, // C Diatonic
            {55, 59},
            {60, 62},
            {64, 65},
            {67, 69},
            {72, 71},
            {76, 74},
            {79, 77},
            {84, 81},
            {88, 83},
            {91, 86}
        },
        {   {55, 55}, // G Chromatic
            {58, 58},
            {61, 61},
            {64, 64},
            {67, 67},
            {70, 70},
            {73, 73},
            {76, 76},
            {79, 79},
            {82, 82},
            {85, 85}
        }
    };

    // Chords
    private static final byte chords[][][][] =
    {
        // F/Bb/Eb
        {   {{41, 53}, {36, 48}}, {{65, 60}, {60, 67}},  //  F/C
            {{38, 50}, {43, 55}}, {{62, 69}, {67, 62}},  //  D/G
            {{46, 58}, {41, 53}}, {{70, 65}, {65, 60}},  // Bb/F
            {{43, 55}, {36, 48}}, {{67, 62}, {60, 67}},  //  G/C
            {{39, 51}, {46, 58}}, {{63, 70}, {70, 65}},  // Eb/Bb
            {{44, 56}, {44, 56}}, {{68, 63}, {68, 63}}
        }, // Ab/Ab

        // G/C/F
        {   {{43, 55}, {38, 50}}, {{67, 62}, {62, 69}},  //  G/D
            {{40, 52}, {45, 57}}, {{64, 71}, {69, 64}},  //  E/A
            {{36, 48}, {43, 55}}, {{60, 67}, {67, 62}},  //  C/G
            {{45, 57}, {38, 50}}, {{69, 64}, {62, 69}},  //  A/D
            {{41, 53}, {36, 48}}, {{65, 60}, {60, 67}},  //  F/C
            {{46, 58}, {46, 58}}, {{70, 65}, {70, 65}}
        }, // Bb/Bb

        // A/D/G
        {   {{45, 57}, {40, 52}}, {{69, 64}, {64, 71}},  //  A/E
            {{42, 54}, {47, 59}}, {{66, 61}, {71, 66}},  // F#/B
            {{38, 50}, {45, 57}}, {{62, 69}, {69, 64}},  //  D/A
            {{47, 59}, {40, 52}}, {{71, 66}, {64, 71}},  //  B/E
            {{43, 55}, {38, 50}}, {{67, 62}, {62, 69}},  //  G/D
            {{36, 48}, {36, 48}}, {{60, 67}, {60, 67}}
        }, //  C/C

        // C#/D/G
        {   {{45, 57}, {40, 52}}, {{69, 64}, {64, 71}},  //  A/E
            {{42, 54}, {47, 59}}, {{66, 61}, {71, 66}},  // F#/B
            {{38, 50}, {45, 57}}, {{62, 69}, {69, 64}},  //  D/A
            {{47, 59}, {40, 52}}, {{71, 66}, {64, 71}},  //  B/E
            {{43, 55}, {38, 50}}, {{67, 62}, {62, 69}},  //  G/D
            {{36, 48}, {36, 48}}, {{60, 67}, {60, 67}}
        }, //  C/C

        // B/C/C#
        {   {{42, 54}, {42, 54}}, {{47, 59}, {47, 59}},  // F#/B
            {{40, 52}, {40, 52}}, {{45, 57}, {45, 57}},  //  E/A
            {{38, 50}, {38, 50}}, {{43, 55}, {43, 55}},  //  D/G
            {{36, 48}, {36, 48}}, {{41, 53}, {41, 53}},  //  C/F
            {{46, 58}, {46, 58}}, {{39, 51}, {39, 51}},  // Bb/Eb
            {{44, 56}, {44, 56}}, {{37, 49}, {37, 49}}
        }, // Ab/Db

        // C System
        {   {{42, 54}, {42, 54}}, {{47, 59}, {47, 59}},  // F#/B
            {{40, 52}, {40, 52}}, {{45, 57}, {45, 57}},  //  E/A
            {{38, 50}, {38, 50}}, {{43, 55}, {43, 55}},  //  D/G
            {{36, 48}, {36, 48}}, {{41, 53}, {41, 53}},  //  C/F
            {{46, 58}, {46, 58}}, {{39, 51}, {39, 51}},  // Bb/Eb
            {{44, 56}, {44, 56}}, {{37, 49}, {37, 49}}
        }, // Ab/Db

        // B System
        {   {{42, 54}, {42, 54}}, {{47, 59}, {47, 59}},  // F#/B
            {{40, 52}, {40, 52}}, {{45, 57}, {45, 57}},  //  E/A
            {{38, 50}, {38, 50}}, {{43, 55}, {43, 55}},  //  D/G
            {{36, 48}, {36, 48}}, {{41, 53}, {41, 53}},  //  C/F
            {{46, 58}, {46, 58}}, {{39, 51}, {39, 51}},  // Bb/Eb
            {{44, 56}, {44, 56}}, {{37, 49}, {37, 49}}
        }, // Ab/Db
    };

    // Hilites
    private static final boolean hilites[][][] =
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
        {   {true, false, false, false, true, false, false, false, true, false},
            {false, true, true, false, false, true, true, false, false, true, true},
            {false, false, true, true, false, false, true, true, false, false}
        },

        // B system
        {   {false, false, true, true, false, false, true, true, false, false},
            {false, true, true, false, false, true, true, false, false, true, true},
            {true, false, false, false, true, false, false, false, true, false}
        }
    };

    // Midi codes
    private static final int noteOff = 0x80;
    private static final int noteOn = 0x90;
    private static final int change = 0xc0;

    // Preferences
    private final static String PREF_INSTRUMENT = "pref_instrument";
    private final static String PREF_REVERSE = "pref_reverse";
    private final static String PREF_LAYOUT = "pref_layout";
    private final static String PREF_FASCIA = "pref_fascia";
    private final static String PREF_KEY = "pref_key";

    // Layouts
    private final static int layouts[] =
    {
        R.layout.activity_main,
        R.layout.activity_main_lower,
        R.layout.activity_main_upper
    };

    // Fascias
    private final static int fascias[] =
    {
        R.drawable.bg_onyx, R.drawable.bg_teak,
        R.drawable.bg_cherry, R.drawable.bg_rosewood,
        R.drawable.bg_olivewood
    };

    // Volume
    private static final int VOLUME = 96;

    // Button states
    private boolean buttonStates[][] =
    {
        {
            false, false, false, false, false, false,
            false, false, false, false, false
        },
        {
            false, false, false, false, false, false,
            false, false, false, false, false
        },
        {
            false, false, false, false, false, false,
            false, false, false, false, false
        }
    };

    private boolean bassStates[][] =
    {
        {false, false, false, false, false, false},
        {false, false, false, false, false, false}
    };

    private boolean bellows = false;
    private boolean reverse = false;

    // Status
    private int instrument;
    private int volume;
    private int layout;
    private int fascia;
    private int type;
    private int key;

    // MidiDriver
    private MidiDriver midi;

    // Views
    private TextView keyView;
    private Switch revView;
    private Toast toast;

    // On create
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        getPreferences();

        // Set layout
        setContentView(layouts[layout]);

        // Add custom view to action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setCustomView(R.layout.text_view);
            actionBar.setDisplayShowCustomEnabled(true);
            keyView = (TextView) actionBar.getCustomView();
        }

        // Set fascia
        View view = findViewById(R.id.fascia);
        if (view != null)
            view.setBackgroundResource(fascias[fascia]);

        // Create midi
        midi = new MidiDriver();

        // Set listeners
        setListeners();

        // Set volume, let the user adjust the volume with the
        // android volume buttons
        volume = VOLUME;
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

        // Start midi
        if (midi != null)
            midi.start();
    }

    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Save preferences
        savePreferences();

        // Stop midi
        if (midi != null)
            midi.stop();
    }

    // onOptionsItemSelected
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
    @SuppressLint("ClickableViewAccessibility")
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

            // Show toast
            if (reverse)
                showToast(R.string.buttons_reversed);

            else
                showToast(R.string.buttons_normal);

            // Set button hilites
            setButtonHilites();
            break;

        default:
            break;
        }
    }

    @Override
    public void onMidiStart()
    {
        // Set instrument
        for (int i = 0; i <= buttons.length; i++)
            writeChange(change + i, instrument);
    }

    // Save preferences
    private void savePreferences()
    {
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_REVERSE, reverse);
        editor.apply();
    }

    // Get preferences
    private void getPreferences()
    {
        // Load preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get instrument
        instrument =
            Integer.parseInt(preferences.getString(PREF_INSTRUMENT, "21"));

        // Get layout
        int layout =
            Integer.parseInt(preferences.getString(PREF_LAYOUT, "0"));

        // Layout changed
        if (layout != this.layout)
        {
            this.layout = layout;
            setContentView(layouts[layout]);
            setListeners();

            // Set fascia
            View view = findViewById(R.id.fascia);
            if (view != null)
                view.setBackgroundResource(fascias[fascia]);
        }

        // Get fascia
        int fascia =
            Integer.parseInt(preferences.getString(PREF_FASCIA, "0"));

        // Fascia changed
        if (fascia != this.fascia)
        {
            this.fascia = fascia;
            View view = findViewById(R.id.fascia);
            if (view != null)
                view.setBackgroundResource(fascias[fascia]);
        }

        // Get key
        key = Integer.parseInt(preferences.getString(PREF_KEY, "2"));

        // Set type from key
        type = types[key];

        // Set status text
        Resources resources = getResources();
        String keys[] = resources.getStringArray(R.array.pref_key_entries);
        String layouts[] =
            resources.getStringArray(R.array.pref_layout_entries);
        String format = resources.getString(R.string.format);
        String status = String.format(Locale.getDefault(), format,
                                      keys[key], layouts[layout]);
        if (keyView != null)
            keyView.setText(status);

        // Set reverse
        reverse = preferences.getBoolean(PREF_REVERSE, false);

        // Set reverse switch
        if (revView != null)
            revView.setChecked(reverse);

        // Set button hilites
        setButtonHilites();
    }

    // Set button hilites
    private void setButtonHilites()
    {
        View v;
        boolean large = false;

        // If the first or last button in the middle row isn't there
        // we must be using large buttons
        if (((v = findViewById(buttons[1][0])) == null) ||
                ((v = findViewById(buttons[1][buttons[1].length - 1])) == null))
            large = true;

        // Diatonic, set all buttons normal
        if (type == DIATONIC)
        {
            for (int[] row : buttons)
            {
                for (int buttonId : row)
                {
                    ImageButton button =
                        findViewById(buttonId);

                    if(button == null)
                    {
                        continue;
                    }

                    if (large)
                        button.setImageResource(R.drawable.ic_button_large);

                    else
                        button.setImageResource(R.drawable.ic_button);
                }
            }
        }

        // Chromatic, set dark buttons
        else
        {
            for (int i = 0; i < hilites[key].length; i++)
            {
                for (int j = 0; j < hilites[key][i].length; j++)
                {
                    int k = reverse ? buttons[i].length - j - 1 : j;

                    ImageButton button =
                        findViewById(buttons[i][k]);

                    if(button == null)
                    {
                        continue;
                    }

                    if (large)
                    {
                        if (!hilites[key][i][j])
                            button.
                            setImageResource(R.drawable.ic_button_large);

                        else
                            button.
                            setImageResource(R.drawable.ic_button_black_large);
                    }
                    else
                    {
                        if (!hilites[key][i][j])
                            button.setImageResource(R.drawable.ic_button);

                        else
                            button.
                            setImageResource(R.drawable.ic_button_black);
                    }
                }
            }
        }
    }

    // On bellows down
    private boolean onBellowsDown(View v, MotionEvent event)
    {
        if (bellows == true)
        {
            return false;
        }

        bellows = true;

        // Change all notes
        for (int i = 0; i < buttons.length; i++)
        {
            for (int j = 0; j < buttons[i].length; j++)
            {
                if (buttonStates[i][j] == false)
                {
                    continue;
                }

                int k = 0;

                switch (i)
                {
                case 0:
                    k = (reverse) ? buttons[i].length - j - 2 : j;
                    break;

                case 1:
                    k = (reverse) ? buttons[i].length - j - 1 : j;
                    break;

                case 2:
                    k = (reverse) ? buttons[i].length - j - 1 : j + 1;
                    break;
                }

                int note = notes[type][k][!bellows ? 1 : 0] +
                           keyvals[key][i];

                // Stop note
                writeNote(noteOff + i, note, volume);

                note = notes[type][k][bellows ? 1 : 0] +
                       keyvals[key][i];

                // Play note
                writeNote(noteOn + i, note, volume);
            }
        }

        for (int i = 0; i < basses.length; i++)
        {
            for (int j = 0; j < basses[i].length; j++)
            {
                if (bassStates[i][j] == false)
                {
                    continue;
                }

                int k = 0;

                switch (i)
                {
                case 0:
                    k = (reverse) ? basses[0].length - j - 1 : j;
                    break;

                case 1:
                    k = (reverse) ? (basses[0].length * 2) - j - 1 :
                        basses[0].length + j;
                    break;
                }

                // Play chord
                int note = chords[key][k][!bellows ? 1 : 0][0];
                writeNote(noteOff + 3, note, volume);

                note = chords[key][k][!bellows ? 1 : 0][1];
                writeNote(noteOff + 3, note, volume);

                note = chords[key][k][bellows ? 1 : 0][0];
                writeNote(noteOn + 3, note, volume);

                note = chords[key][k][bellows ? 1 : 0][1];
                writeNote(noteOn + 3, note, volume);
            }
        }

        return true;
    }

    private boolean onBellowsUp(View v, MotionEvent event)
    {
        if (bellows == false)
        {
            return false;
        }

        bellows = false;

        // Change all notes
        for (int i = 0; i < buttons.length; i++)
        {
            for (int j = 0; j < buttons[i].length; j++)
            {
                if (buttonStates[i][j] == false)
                {
                    continue;
                }

                int k = 0;

                switch (i)
                {
                case 0:
                    k = (reverse) ? buttons[i].length - j - 2 : j;
                    break;

                case 1:
                    k = (reverse) ? buttons[i].length - j - 1 : j;
                    break;

                case 2:
                    k = (reverse) ? buttons[i].length - j - 1 : j + 1;
                    break;
                }

                int note = notes[type][k][!bellows ? 1 : 0] +
                           keyvals[key][i];

                // Stop note
                writeNote(noteOff + i, note, volume);

                note = notes[type][k][bellows ? 1 : 0] +
                       keyvals[key][i];

                // Play note
                writeNote(noteOn + i, note, volume);
            }
        }

        for (int i = 0; i < basses.length; i++)
        {
            for (int j = 0; j < basses[i].length; j++)
            {
                if (bassStates[i][j] == false)
                {
                    continue;
                }

                int k = 0;

                switch (i)
                {
                case 0:
                    k = (reverse) ? basses[0].length - j - 1 : j;
                    break;

                case 1:
                    k = (reverse) ? (basses[0].length * 2) - j - 1 :
                        basses[0].length + j;
                    break;
                }

                // Play chord
                int note = chords[key][k][!bellows ? 1 : 0][0];
                writeNote(noteOff + 3, note, volume);

                note = chords[key][k][!bellows ? 1 : 0][1];
                writeNote(noteOff + 3, note, volume);

                note = chords[key][k][bellows ? 1 : 0][0];
                writeNote(noteOn + 3, note, volume);

                note = chords[key][k][bellows ? 1 : 0][1];
                writeNote(noteOn + 3, note, volume);
            }
        }

        return true;
    }

    private boolean onButtonDown(View v, MotionEvent event)
    {
        int id = v.getId();

        // Check melody buttons
        for (int i = 0; i < buttons.length; i++)
        {
            for (int j = 0; j < buttons[i].length; j++)
            {
                if (id != buttons[i][j])
                {
                    continue;
                }

                if (buttonStates[i][j] == true)
                {
                    continue;
                }

                buttonStates[i][j] = true;

                // Play note
                int k = 0;

                switch (i)
                {
                case 0:
                    k = (reverse) ? buttons[i].length - j - 1 : j;
                    break;

                case 1:
                    k = (reverse) ? buttons[i].length - j - 1 : j;
                    break;

                case 2:
                    k = (reverse) ? buttons[i].length - j : j + 1;
                    break;
                }

                int note = notes[type][k][bellows ? 1 : 0] + keyvals[key][i];
                writeNote(noteOn + i, note, volume);
                return false;
            }
        }

        // Check bass buttons
        for (int i = 0; i < basses.length; i++)
        {
            for (int j = 0; j < basses[i].length; j++)
            {
                if (id != basses[i][j])
                {
                    continue;
                }

                if (bassStates[i][j] == true)
                {
                    continue;
                }

                int k = 0;

                bassStates[i][j] = true;

                switch (i)
                {
                case 0:
                    k = (reverse) ? basses[0].length - j - 1 : j;
                    break;

                case 1:
                    k = (reverse) ? (basses[0].length * 2) - j - 1 :
                        basses[0].length + j;
                    break;
                }

                // Play chord
                int note = chords[key][k][bellows ? 1 : 0][0];
                writeNote(noteOn + 3, note, volume);

                note = chords[key][k][bellows ? 1 : 0][1];
                writeNote(noteOn + 3, note, volume);

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
                if (id != buttons[i][j])
                {
                    continue;
                }

                if (buttonStates[i][j] == false)
                {
                    continue;
                }

                buttonStates[i][j] = false;

                // Stop note
                int k = 0;

                switch (i)
                {
                case 0:
                    k = (reverse) ? buttons[i].length - j - 1 : j;
                    break;

                case 1:
                    k = (reverse) ? buttons[i].length - j - 1 : j;
                    break;

                case 2:
                    k = (reverse) ? buttons[i].length - j : j + 1;
                    break;
                }

                int note = notes[type][k][bellows ? 1 : 0] + keyvals[key][i];
                writeNote(noteOff + i, note, 0);

                return false;
            }
        }

        // Check bass buttons
        for (int i = 0; i < basses.length; i++)
        {
            for (int j = 0; j < basses[i].length; j++)
            {
                if(id != basses[i][j])
                {
                    continue;
                }

                if(bassStates[i][j] == false)
                {
                    continue;
                }

                int k = 0;

                bassStates[i][j] = false;

                switch (i)
                {
                case 0:
                    k = (reverse) ? basses[0].length - j - 1 : j;
                    break;

                case 1:
                    k = (reverse) ? (basses[0].length * 2) - j - 1 :
                        basses[0].length + j;
                    break;
                }

                // Stop chord
                int note = chords[key][k][bellows ? 1 : 0][0];
                writeNote(noteOff + 3, note, volume);

                note = chords[key][k][bellows ? 1 : 0][1];
                writeNote(noteOff + 3, note, volume);

                return false;
            }
        }

        return false;
    }

    // Show toast.
    private void showToast(int key)
    {
        Resources resources = getResources();
        String text = resources.getString(key);

        showToast(text);
    }

    private void showToast(String text)
    {
        // Cancel the last one
        if (toast != null)
            toast.cancel();

        // Make a new one
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // Set listeners
    private void setListeners()
    {
        View v;

        // Set listener for all buttons
        for (int[] button : buttons)
        {
            for (int j = 0; j < button.length; j++)
            {
                v = findViewById(button[j]);
                if (v != null)
                    v.setOnTouchListener(this);
            }
        }

        // Bass buttons
        for (int[] bass : basses)
        {
            for (int j = 0; j < bass.length; j++)
            {
                v = findViewById(bass[j]);
                if (v != null)
                    v.setOnTouchListener(this);
            }
        }

        // Bellows
        v = findViewById(R.id.bellows);
        if (v != null)
            v.setOnTouchListener(this);

        // Reverse switch
        revView = findViewById(R.id.reverse);
        if (revView != null)
            revView.setOnCheckedChangeListener(this);

        // Midi start
        if (midi != null)
            midi.setOnMidiStartListener(this);
    }

    // Write program change message, two bytes
    public boolean writeChange(int m, int i)
    {
        byte changeMsg[] = new byte[2];

        changeMsg[0] = (byte) m;
        changeMsg[1] = (byte) i;

        midi.write(changeMsg);
        return true;
    }

    // Write note message, three bytes
    public boolean writeNote(int m, int n, int v)
    {
        byte noteMsg[] = new byte[3];

        noteMsg[0] = (byte) m;
        noteMsg[1] = (byte) n;
        noteMsg[2] = (byte) v;

        midi.write(noteMsg);
        return true;
    }
}
