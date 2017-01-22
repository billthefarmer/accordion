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

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

// SettingsFragment
public class SettingsFragment extends PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String KEY_PREF_INSTRUMENT = "pref_instrument";
    private static final String KEY_PREF_LAYOUT = "pref_layout";
    private static final String KEY_PREF_FASCIA = "pref_fascia";
    private static final String KEY_PREF_ABOUT = "pref_about";
    private static final String KEY_PREF_KEY = "pref_key";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);

	// Load the preferences from an XML resource
	addPreferencesFromResource(R.xml.preferences);

	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(getActivity());

	preferences.registerOnSharedPreferenceChangeListener(this);

	ListPreference preference =
	    (ListPreference)findPreference(KEY_PREF_INSTRUMENT);
	preference.setSummary(preference.getEntry());

	preference = (ListPreference)findPreference(KEY_PREF_LAYOUT);
	preference.setSummary(preference.getEntry());

	preference = (ListPreference)findPreference(KEY_PREF_KEY);
	preference.setSummary(preference.getEntry());

	preference = (ListPreference)findPreference(KEY_PREF_FASCIA);
	preference.setSummary(preference.getEntry());

	// Get about summary
	Preference about = findPreference(KEY_PREF_ABOUT);
	String sum = (String) about.getSummary();

	// Set version in text view
	if (about != null)
	{
	    String s = String.format(sum, BuildConfig.VERSION_NAME);
	    about.setSummary(s);
	}
    }

    // onPause
    @Override
    public void onPause()
    {
	super.onPause();

	SharedPreferences preferences =
	    PreferenceManager.getDefaultSharedPreferences(getActivity());

	preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    // On preference tree click
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
					 Preference preference)
    {
    	boolean result =
	    super.onPreferenceTreeClick(preferenceScreen, preference);

    	if (preference instanceof PreferenceScreen)
    	{
	    Dialog dialog = ((PreferenceScreen)preference).getDialog();
	    ActionBar actionBar = dialog.getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(false);
    	}

    	return result;
    }

    // On shared preference changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
					  String key)
    {
	if (key.equals(KEY_PREF_INSTRUMENT) || key.equals(KEY_PREF_KEY) ||
	    key.equals(KEY_PREF_LAYOUT) || key.equals(KEY_PREF_FASCIA))
	{
	    ListPreference preference = (ListPreference)findPreference(key);

	    // Set summary to be the user-description for the selected value
	    preference.setSummary(preference.getEntry());
	}

	if (key.equals(KEY_PREF_LAYOUT))
	{
	    SettingsActivity activity = (SettingsActivity) getActivity();
	    activity.layoutChanged = true;
	}
    }
}
