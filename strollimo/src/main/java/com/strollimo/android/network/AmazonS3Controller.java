package com.strollimo.android.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Date;

public class AmazonS3Controller {
    private final static String TAG = AmazonS3Controller.class.getSimpleName();

    private final static String AWSAccessKeyId = "AKIAIO5MQ5HJN3NINXVQ";
    private final static String AWSSecretKey = "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";

    public void uploadFile(String bucket, File file) throws AmazonS3Exception {
        uploadFile(bucket, file.getName(), file);
    }

    public void uploadFile(String bucket, String key, File file) throws AmazonS3Exception {
        try {
            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(AWSAccessKeyId, AWSSecretKey));
            PutObjectRequest por = new PutObjectRequest(bucket, key, file);
            s3Client.putObject(por);
        } catch (Exception ex) {
            Log.e(TAG, "Upload to amazon failed", ex);
            throw new AmazonS3Exception("Upload to amazon failed", ex);
        }
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

    public String getUrl(String s) {
        AmazonUrl amazonUrl;
        try {
            amazonUrl = AmazonUrl.fromUrl(s);
        } catch (ParseException e) {
            Log.e(TAG, "Wrong amazon URL", e);
            return null;
        }
        URL url = getUrl(amazonUrl);
        return url.toString();
    }
}
