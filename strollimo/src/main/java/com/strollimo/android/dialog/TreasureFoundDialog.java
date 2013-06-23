package com.strollimo.android.dialog;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.strollimo.android.R;

public class TreasureFoundDialog extends DialogFragment {

    private final int mPlacesFound;
    private final int mAllPlaces;
    private final int mCoins;

    public TreasureFoundDialog(int placesFound, int allPlaces, int coins) {
        mPlacesFound = placesFound;
        mAllPlaces = allPlaces;
        mCoins = coins;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.treasure_found_dialog, container);
        view.findViewById(R.id.full_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView placesFound = (TextView)view.findViewById(R.id.places_discovered_text);
        placesFound.setText(getString(R.string.places_discovered_text, mPlacesFound, mAllPlaces));
        TextView coinValue = (TextView)view.findViewById(R.id.coins_found_text);
        coinValue.setText(getString(R.string.coin_value, mCoins));

        return view;
    }

}
