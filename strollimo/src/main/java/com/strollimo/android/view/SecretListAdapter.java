package com.strollimo.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.strollimo.android.R;
import com.strollimo.android.StrollimoApplication;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;

import java.util.List;

public class SecretListAdapter extends BaseAdapter {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private final Context mContext;
    private List<Secret> mSecrets;
    private ImageManager mImageManager;

    public SecretListAdapter(Context context, Mystery mystery) {
        mSecrets = mystery.getSecrets();
        mContext = context;
        mImageManager = StrollimoApplication.getService(ImageManager.class);
    }

    @Override
    public int getCount() {
        return mSecrets.size();
    }

    @Override
    public Secret getItem(int i) {
        return mSecrets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.secret_list_item, viewGroup, false);
        }
        TextView secretTitle = ((TextView)view.findViewById(R.id.secret_title));
        secretTitle.setText(mSecrets.get(i).getName());
        ImageView secretPhoto = ((ImageView)view.findViewById(R.id.secret_photo));

        ImageTagFactory imageTagFactory = ImageTagFactory.newInstance(WIDTH, HEIGHT, R.drawable.closed);
        ImageTag tag = imageTagFactory.build(mSecrets.get(i).getImgUrl(), mContext);
        secretPhoto.setTag(tag);
        mImageManager.getLoader().load(secretPhoto);

        return view;
    }

}
