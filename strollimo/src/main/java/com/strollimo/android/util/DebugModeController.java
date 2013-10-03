package com.strollimo.android.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.StrollimoPreferences;
import com.strollimo.android.view.MainActivity;

public class DebugModeController {

    public static final int DEBUG_MODE_COUNT = 10;
    public static final int DEBUG_MODE_WARNING = 3;
    private final View mDebugView;
    private final Context mContext;
    private int mClickCount = 0;

    public DebugModeController(View view, Context context) {
        mDebugView = view;
        mContext = context;
        mDebugView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickCount++;
                if (mClickCount > DEBUG_MODE_COUNT - DEBUG_MODE_WARNING) {
                    if (mClickCount >= DEBUG_MODE_COUNT) {
                        StrollimoApplication.getService(StrollimoPreferences.class).setDebugModeOn(true);
                        Toast.makeText(mContext, "You're in debug mode", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, String.format("%d click to debug mode", DEBUG_MODE_COUNT - mClickCount), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
