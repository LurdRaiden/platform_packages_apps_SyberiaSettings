/*
 * Copyright © 2018-2019 Syberia Project
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

package com.syberia.settings;

import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.syberia.SyberiaUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.dashboard.DashboardFragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class SyberiaSettings extends SettingsPreferenceFragment {

    private int mDashBoardStyle;

    protected CollapsingToolbarLayout mCollapsingToolbarLayout;


    public void onResume() {
        super.onResume();
        hideToolbar();
        setSigmaDashboardStyle();
    }

    private void hideToolbar() {
        if (mCollapsingToolbarLayout == null) {
            mCollapsingToolbarLayout = getActivity().findViewById(R.id.collapsing_toolbar);
        }
        if (mCollapsingToolbarLayout != null) {
            mCollapsingToolbarLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        hideToolbar();
        setSigmaDashboardStyle();
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.syberia_settings;
    }

   private void setSigmaDashboardStyle() {
        int mDashBoardStyle = geSettingstDashboardStyle();
        final PreferenceScreen mScreen = getPreferenceScreen();
        final int mCount = mScreen.getPreferenceCount();
        for (int i = 0; i < mCount; i++) {
            final Preference mPreference = mScreen.getPreference(i);

            String mKey = mPreference.getKey();

            if (mKey == null) continue;

            if (mKey.equals("syberia_settings_header")) {
                            mPreference.setLayoutResource(R.layout.syberia_settings_header);
                            continue;
                        }
            if (mDashBoardStyle == 0) {
                    if (mKey.equals("top_level_about_device")){
                mPreference.setLayoutResource(R.layout.top_about);
                    }else {
                mPreference.setLayoutResource(R.layout.top_level_card);
                    }
                } else if (mDashBoardStyle == 1 || mDashBoardStyle == 2){
               if (mKey.equals("system_category")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_top);
                } else if (mKey.equals("about_team")) {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_bottom);
                } else {
                    mPreference.setLayoutResource(R.layout.dot_dashboard_preference_middle); 
                }  
            }
        }
    }

    private int geSettingstDashboardStyle() {
        return Settings.System.getIntForUser(getContext().getContentResolver(),
                Settings.System.SETTINGS_DASHBOARD_STYLE, 2, UserHandle.USER_CURRENT);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SYBERIA;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.syberia_settings);
}