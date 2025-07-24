/*
 * Copyright (C) 2025 The 2by2 Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.system;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import java.util.Arrays;

@SearchIndexable
public class SpecialSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "SpecialSettings";

    private Toast mToast;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.custom_settings_special;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    private synchronized void showToast(int msgId) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getContext(), msgId, Toast.LENGTH_LONG);
        mToast.show();
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.custom_settings_special);
}
