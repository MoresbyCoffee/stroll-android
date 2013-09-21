package com.strollimo.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.strollimo.android.R;
import com.strollimo.android.controller.VolleyImageLoader;

/**
 * Created by marcoc on 14/09/2013.
 */
public class ProgressNetworkImageView extends NetworkImageView {

    private static final int FADE_IN_TIME_MS = 250;

    private View mProgressView;

    public ProgressNetworkImageView(Context context) {
        this(context, null);
    }

    public ProgressNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String url) {
        setImageUrl(url, VolleyImageLoader.getInstance());
    }


    public void setImageUrl(String url, final View progressView) {
        setImageUrl(url, VolleyImageLoader.getInstance());
        mProgressView = progressView;
        mProgressView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onImageResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        if (mProgressView != null && response.getBitmap() != null) {
            mProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(android.R.color.transparent),
                new BitmapDrawable(getContext().getResources(), bm)
        });
        td.setCrossFadeEnabled(true);
        setImageDrawable(td);
        td.startTransition(FADE_IN_TIME_MS);
    }
}
