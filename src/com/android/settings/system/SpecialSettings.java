/*
 * Copyright (C) 2025 The 2by2 Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.system;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.android.internal.logging.nano.MetricsProto;
import com.android.server.custom.KeyboxService;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;

import java.util.Arrays;

@SearchIndexable
public class SpecialSettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "SpecialSettings";

    private Resources res;

    private Toast mToast;

    private static final String PREF_IMPORT_KEYBOX = "pref_import_keybox";

    private Preference keyboxPref;

    private static final String KEYBOX_DATA_FILE = "2by2/gms_certified_keybox.xml";

    private File keyboxDataFile;

    private KeyboxService keyboxService;

    private static final int REQUEST_CODE_PICK_KEYBOX = 10000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        res = getContext().getResources();

        keyboxDataFile = new File(Environment.getDataMiscDirectory(), KEYBOX_DATA_FILE);

        keyboxService = KeyboxService.getInstance();

        updateKeyboxPrefStatus();
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

    private boolean loadKeyboxFromFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/xml");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"application/xml", "text/xml"});
        intent.putExtra(Intent.EXTRA_TITLE, res.getString(R.string.spoofing_pif_import_keybox_pref_title));
        startActivityForResult(intent, REQUEST_CODE_PICK_KEYBOX);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_KEYBOX && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (uri != null && getContext() != null) {
                try (InputStream in = getContext().getContentResolver().openInputStream(uri);
                     OutputStream out = new FileOutputStream(keyboxDataFile)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    if (keyboxService != null) {
                        keyboxService.reset();
                        keyboxService.load();
                    }
                    showToast(R.string.spoofing_pif_import_keybox_mes_success);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to import keybox file", e);
                    showToast(R.string.spoofing_pif_import_keybox_mes_failed);
                }
            }
        }

        updateKeyboxPrefStatus();
    }

    private boolean removeExistingKeybox() {
        new AlertDialog.Builder(getActivity())
		    .setMessage(res.getString(R.string.spoofing_pif_remove_keybox_mes_confirm))
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        keyboxDataFile.delete();
                        updateKeyboxPrefStatus();
                        if (keyboxService != null) {
                            keyboxService.reset();
                        }
                        showToast(R.string.spoofing_pif_remove_keybox_mes_success);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to remove keybox file", e);
                        showToast(R.string.spoofing_pif_remove_keybox_mes_failed);
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .show();

        return true;
    }

    private void updateKeyboxPrefStatus() {
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            Log.e(TAG, "Failed to get preference screen");
            return;
        }

        keyboxPref = screen.findPreference(PREF_IMPORT_KEYBOX);
        if (keyboxPref == null) {
            Log.e(TAG, "Failed to get keybox preference");
            return;
        }

        boolean isKeyboxAvailable = keyboxDataFile.exists();

        if (!isKeyboxAvailable) {
            keyboxPref.setTitle(res.getString(R.string.spoofing_pif_import_keybox_pref_title));
            keyboxPref.setSummary(res.getString(R.string.spoofing_pif_import_keybox_pref_summary));
            keyboxPref.setOnPreferenceClickListener(pref -> loadKeyboxFromFile());
        } else {
            keyboxPref.setTitle(res.getString(R.string.spoofing_pif_remove_keybox_pref_title));
            keyboxPref.setSummary(res.getString(R.string.spoofing_pif_remove_keybox_pref_summary));
            keyboxPref.setOnPreferenceClickListener(pref -> removeExistingKeybox());
        }
    }
}
