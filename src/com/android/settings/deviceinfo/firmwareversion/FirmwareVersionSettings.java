/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.widget.LayoutPreference;

@SearchIndexable
public class FirmwareVersionSettings extends DashboardFragment {

    private final String buildVersion = SystemProperties.get("ro.2by2.version");
    private final String maintainerName = SystemProperties.get("ro.2by2.maintainer");

    private LayoutPreference logoLayoutPref;

    private TextView buildVersionTextView;
    private TextView maintainerNameTextView;

    @Override
    public @Nullable String getPreferenceScreenBindingKey(@NonNull Context context) {
        return FirmwareVersionScreen.KEY;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.firmware_version;
    }

    @Override
    protected String getLogTag() {
        return "FirmwareVersionSettings";
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DIALOG_FIRMWARE_VERSION;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoLayoutPref = findPreference("logo_2by2");

        if (logoLayoutPref != null) {
            buildVersionTextView = logoLayoutPref.findViewById(R.id.logo_version_text);
            maintainerNameTextView = logoLayoutPref.findViewById(R.id.logo_maintainer_text);

            if (buildVersionTextView != null) {
                buildVersionTextView.setText("#" + buildVersion);
            }

            if (maintainerNameTextView != null) {
                maintainerNameTextView.setText("by " + maintainerName);
            }
        }
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.firmware_version);
}
