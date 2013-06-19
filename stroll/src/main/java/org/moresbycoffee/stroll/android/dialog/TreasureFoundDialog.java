package org.moresbycoffee.stroll.android.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.moresbycoffee.stroll.android.R;

public class TreasureFoundDialog extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.treasure_found_dialog, container);
        getDialog().setTitle("Hello");

        return view;
    }}
