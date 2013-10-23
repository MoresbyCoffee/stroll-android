package com.strollimo.android.utils.gson.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapTypeAdapter extends TypeAdapter<Bitmap> {

    @Override
    public void write(JsonWriter writer, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            writer.nullValue();
            return;
        }
//        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
//        bitmap.copyPixelsToBuffer(buffer);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
        writer.value(imageEncoded);
        //writer.value(Base64.encodeToString(buffer.array(), Base64.DEFAULT));
    }

    @Override
    public Bitmap read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String stringBitmap = reader.nextString();
        byte[] b = Base64.decode(stringBitmap, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}