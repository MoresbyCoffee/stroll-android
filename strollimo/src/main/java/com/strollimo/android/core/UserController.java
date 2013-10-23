package com.strollimo.android.core;

import com.strollimo.android.models.Secret;

import java.util.HashSet;
import java.util.Set;

public class UserController {
    private final PreferencesController mPrefs;
    private Set<String> mCapturedSecrets;

    public UserController(PreferencesController prefs) {
        mPrefs = prefs;
        mCapturedSecrets = new HashSet<String>();
    }

    public void loadCapturedSecrets() {
        mCapturedSecrets = mPrefs.getCapturedSecrets();
    }

    public void reset() {
        mCapturedSecrets.clear();
        mPrefs.clearCapturedSecrets();
    }

    public void captureSecret(Secret secret) {
        mCapturedSecrets.add(secret.getId());
    }

}
