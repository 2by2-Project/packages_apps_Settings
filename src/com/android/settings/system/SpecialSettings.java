/*
 * Copyright (C) 2025 The 2by2 Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import java.util.Arrays;

import jp.project2by2.settings.preferences.KeyboxDataPreference;

@SearchIndexable
public class SpecialSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "SpecialSettings";

    private Toast mToast;

    private static final String KEYBOX_DATA_KEY = "spoofing_keybox_data_setting";

    private ActivityResultLauncher<Intent> mKeyboxFilePickerLauncher;
    private KeyboxDataPreference mKeyboxDataPreference;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mKeyboxFilePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    Preference pref = findPreference(KEYBOX_DATA_KEY);
                    if (pref instanceof KeyboxDataPreference) {
                        ((KeyboxDataPreference) pref).handleFileSelected(uri);
                    }
                }
            }
        );
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mKeyboxDataPreference = findPreference(KEYBOX_DATA_KEY);
        if (mKeyboxDataPreference != null) {
            mKeyboxDataPreference.setFilePickerLauncher(mKeyboxFilePickerLauncher);
        }
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
