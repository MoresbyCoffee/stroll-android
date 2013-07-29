package com.strollimo.android.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class AmazonS3Controller {
    private final static String AWSAccessKeyId = "AKIAIO5MQ5HJN3NINXVQ";
    private final static String AWSSecretKey = "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";

    public void uploadFile(String bucket, File file) {
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey));
        PutObjectRequest por = new PutObjectRequest(bucket, file.getName(), file);
        s3Client.putObject(por);
    }

    public void uploadFile(String bucket, String key, File file) {
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey));
        PutObjectRequest por = new PutObjectRequest(bucket, key, file);
        s3Client.putObject(por);
    }

    public Bitmap downloadImage(AmazonUrl amazonUrl) throws IOException {
        return downloadImage(amazonUrl.getBucket(), amazonUrl.getPath());
    }

    public Bitmap downloadImage(String bucket, String file) throws IOException {
        URL url = getUrl(bucket, file);
        InputStream is = null;
        URLConnection urlConn = new URL(url.toString()).openConnection();
        is = urlConn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }

    public URL getUrl(String bucket, String file) {
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey));
        ResponseHeaderOverrides override = new ResponseHeaderOverrides();
        override.setContentType("image/jpeg");
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucket, file);
        urlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600000));  // Added an hour's worth of milliseconds to the current time.
        urlRequest.setResponseHeaders(override);
        return s3Client.generatePresignedUrl(urlRequest);
    }

    public URL getUrl(AmazonUrl amazonUrl) {
        return getUrl(amazonUrl.getBucket(), amazonUrl.getPath());
    }

    public static AmazonUrl mysteryUrl(String filename) {
        return new AmazonUrl("strollimo1", "mystery", filename);
    }
}
