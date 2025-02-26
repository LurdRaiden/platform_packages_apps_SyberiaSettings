/*
 * Copyright © 2018-2021 Syberia Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syberia.settings.fragments;

import static android.os.UserHandle.USER_CURRENT;
import static android.os.UserHandle.USER_SYSTEM;

import android.content.Context;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.R;
import android.net.Uri;

import com.android.settings.dashboard.DashboardFragment;
import com.android.internal.util.syberia.SyberiaUtils;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.logging.nano.MetricsProto;

import com.syberia.settings.preference.SystemSettingListPreference;
import com.android.settings.development.OverlayCategoryPreferenceController;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import android.provider.Settings;
import android.os.UserHandle;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class UiThemingSettings extends DashboardFragment implements OnPreferenceChangeListener {

    private static final String TAG = "UiThemingSettings";
    private static final String KEY_DASHBOARD_STYLE = "settings_dashboard_style";

    private String MONET_ENGINE_COLOR_OVERRIDE = "monet_engine_color_override";
    private String MONET_ENGINE_BGCOLOR_OVERRIDE = "monet_engine_bgcolor_override";

    private ColorPickerPreference mMonetColor;
    private ColorPickerPreference mMonetBgColor;
    private ListPreference mDashBoardStyle;

    private Handler mHandler;
    private IOverlayManager mOverlayManager;
    private IOverlayManager mOverlayService;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        final PreferenceScreen prefScreen = getPreferenceScreen();
        mDashBoardStyle = (ListPreference) prefScreen.findPreference(KEY_DASHBOARD_STYLE);
        mDashBoardStyle.setOnPreferenceChangeListener(this);

        final PreferenceScreen screen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mMonetColor = (ColorPickerPreference) screen.findPreference(MONET_ENGINE_COLOR_OVERRIDE);
        int intColor = Settings.Secure.getInt(resolver, MONET_ENGINE_COLOR_OVERRIDE, 0xFF1B6EF3);
        String hexColor = String.format("#%08x", (0xffffff & intColor));
        mMonetColor.setNewPreviewColor(intColor);
        mMonetColor.setSummary(hexColor);
        mMonetColor.setOnPreferenceChangeListener(this);

        mMonetBgColor = (ColorPickerPreference) screen.findPreference(MONET_ENGINE_BGCOLOR_OVERRIDE);
        int intBgColor = Settings.Secure.getInt(resolver, MONET_ENGINE_BGCOLOR_OVERRIDE, 0xFF1B6EF3);
        String hexBgColor = String.format("#%08x", (0xffffff & intColor));
        mMonetBgColor.setNewPreviewColor(intBgColor);
        mMonetBgColor.setSummary(hexBgColor);
        mMonetBgColor.setOnPreferenceChangeListener(this);

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.ui_theming;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMonetColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_COLOR_OVERRIDE, intHex);
            return true;
        }
        if (preference == mMonetBgColor) {
            String hexbg = ColorPickerPreference.convertToARGB(Integer
                .parseInt(String.valueOf(newValue)));
            preference.setSummary(hexbg);
            int intBgHex = ColorPickerPreference.convertToColorInt(hexbg);
            Settings.Secure.putInt(resolver,
                MONET_ENGINE_BGCOLOR_OVERRIDE, intBgHex);
            return true;
        }
        if (preference == mDashBoardStyle) {
            SyberiaUtils.showSettingsRestartDialog(getContext());
            return true;
        }
        return false;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.ui_theming) {

            };
}