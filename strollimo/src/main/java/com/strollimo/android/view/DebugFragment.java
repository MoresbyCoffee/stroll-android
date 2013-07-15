package com.strollimo.android.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.strollimo.android.AwsActivity;
import com.strollimo.android.R;

public class DebugFragment extends Fragment {
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.debug_layout, container, false);
        } else {
            ViewGroup parentViewGroup = (ViewGroup) mView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
        mView.findViewById(R.id.aws_test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), AwsActivity.class));
            }
        });
        return mView;
    }
}
