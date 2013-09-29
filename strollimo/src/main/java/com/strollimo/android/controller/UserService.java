package com.strollimo.android.controller;

import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.model.Secret;

import java.util.HashSet;
import java.util.Set;

public class UserService {
    private final StrollimoPreferences mPrefs;
    private Set<String> mCapturedSecrets;

    public UserService(StrollimoPreferences prefs) {
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
