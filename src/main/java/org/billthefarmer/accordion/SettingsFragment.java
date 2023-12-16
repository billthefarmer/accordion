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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

// SettingsFragment
@SuppressWarnings("deprecation")
public class SettingsFragment extends android.preference.PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    // onCreate
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(getActivity());

        ListPreference preference = (ListPreference)
            findPreference(MainActivity.PREF_INSTRUMENT);
        preference.setSummary(preference.getEntry());

        preference = (ListPreference)
            findPreference(MainActivity.PREF_LAYOUT);
        preference.setSummary(preference.getEntry());

        preference = (ListPreference)
            findPreference(MainActivity.PREF_FASCIA);
        preference.setSummary(preference.getEntry());

        preference = (ListPreference)
            findPreference(MainActivity.PREF_THEME);
        preference.setSummary(preference.getEntry());

        preference = (ListPreference)
            findPreference(MainActivity.PREF_KEY);
        preference.setSummary(preference.getEntry());

        // Get about summary
        Preference about = findPreference(MainActivity.PREF_ABOUT);
        String sum = about.getSummary().toString();

        // Set version in text view
        String s = String.format(sum, BuildConfig.VERSION_NAME);
        about.setSummary(s);
    }

    // on Resume
    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
    }

    // on Pause
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
            Dialog dialog = ((PreferenceScreen) preference).getDialog();
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
        if (key.equals(MainActivity.PREF_INSTRUMENT) ||
            key.equals(MainActivity.PREF_LAYOUT) ||
            key.equals(MainActivity.PREF_FASCIA) ||
            key.equals(MainActivity.PREF_THEME) ||
            key.equals(MainActivity.PREF_KEY))
        {
            ListPreference preference = (ListPreference) findPreference(key);

            // Set summary to be the user-description for the selected value
            preference.setSummary(preference.getEntry());
        }

        if (key.equals(MainActivity.PREF_THEME))
        {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                getActivity().recreate();
        }
    }
}
