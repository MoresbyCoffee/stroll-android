package com.strollimo.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.strollimo.android.R;
import com.strollimo.android.model.Mystery;
import com.strollimo.android.model.Secret;
import com.strollimo.android.util.BitmapUtils;

import java.io.File;
import java.util.List;

public class SecretListAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Secret> mSecrets;

    public SecretListAdapter(Context context, Mystery mystery) {
        mSecrets = mystery.getSecrets();
        mContext = context;
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
        secretTitle.setText(mSecrets.get(i).getTitle());
        ImageView secretPhoto = ((ImageView)view.findViewById(R.id.secret_photo));
        File imageFile = mSecrets.get(i).getImageFile();
        if (imageFile != null) {
            secretPhoto.setImageBitmap(BitmapUtils.getBitmapFromFile(imageFile, 800, 600));
        }
        return view;
    }

}
