package com.strollimo.android.core;

import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import com.squareup.tape.FileObjectQueue;
import com.squareup.tape.FileObjectQueue.Converter;
import com.squareup.tape.ObjectQueue;
import com.squareup.tape.TaskQueue;
import com.strollimo.android.services.ImageUploadTaskService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class ImageUploadTaskQueue extends TaskQueue<ImageUploadTask> {
    private static final String FILENAME = "image_upload_task_queue";

    private final Context mContext;

    private ImageUploadTaskQueue(ObjectQueue<ImageUploadTask> delegate, Context context) {
        super(delegate);
        mContext = context;


        if (size() > 0) {
            startService();
        }
    }

    private void startService() {
        mContext.startService(new Intent(mContext, ImageUploadTaskService.class));
    }

    @Override
    public void add(ImageUploadTask entry) {
        super.add(entry);
        startService();
    }

    public static ImageUploadTaskQueue create(Context context, Gson gson) {
        Converter<ImageUploadTask> converter = new GsonConverter<ImageUploadTask>(gson, ImageUploadTask.class);
        File queueFile = new File(context.getFilesDir(), FILENAME);
        FileObjectQueue<ImageUploadTask> delegate;
        try {
            delegate = new FileObjectQueue<ImageUploadTask>(queueFile, converter);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create file queue.", e);
        }
        return new ImageUploadTaskQueue(delegate, context);
    }


    /**
     * Use GSON to serialize classes to a bytes.
     * <p/>
     * Note: This will only work when concrete classes are specified for {@code T}. If you want to specify an interface for
     * {@code T} then you need to also include the concrete class name in the serialized byte array so that you can
     * deserialize to the appropriate type.
     */
    private static class GsonConverter<T> implements FileObjectQueue.Converter<T> {
        private final Gson gson;
        private final Class<T> type;

        public GsonConverter(Gson gson, Class<T> type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public T from(byte[] bytes) {
            Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
            return gson.fromJson(reader, type);
        }

        @Override
        public void toStream(T object, OutputStream bytes) throws IOException {
            Writer writer = new OutputStreamWriter(bytes);
            gson.toJson(object, writer);
            writer.close();
        }
    }
}
