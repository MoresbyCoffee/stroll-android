package org.moresbycoffee.stroll.android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;

public class MainActivity extends Activity {
    private ZXingLibConfig zxingLibConfig;
    private Handler handler;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zxingLibConfig = new ZXingLibConfig();
        zxingLibConfig.useFrontLight = true;
        handler = new Handler();
        textView = (TextView)findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onScanClicked(View view) {
        IntentIntegrator.initiateScan(this, zxingLibConfig);
    }

    public void onMapClicked(View view) {
        Intent intent = new Intent(this, StrollMapActivity.class);
        onActivityResult(1, 5, null);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                final String result = scanResult.getContents();
                Log.i("BB", result);
                if (result != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(result);
                        }
                    });
                }
                break;
            default:
        }
    }

}
