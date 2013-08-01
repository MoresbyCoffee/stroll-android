package com.strollimo.android.network;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonUrl {
    public static final String AMAZON_BUCKET = "strollimo1";
    public static final String MYSTERY_FOLDER_NAME = "mystery";
    public static final String JPEG_EXTENSION = ".jpeg";
    private String mBucket;
    private String mFile;
    private String mFolder;

    public AmazonUrl(String bucket, String folder, String file) {
        this.mBucket = bucket;
        this.mFile = file;
        this.mFolder = folder;
    }

    public static AmazonUrl createMysteryUrl(String mysteryId) {
        return new AmazonUrl(AMAZON_BUCKET, MYSTERY_FOLDER_NAME, mysteryId + JPEG_EXTENSION);
    }

    public static AmazonUrl fromUrl(String url) throws ParseException {
        Matcher matcher = Pattern.compile("amazon:(.*)/(.*)/(.*)").matcher(url);
        if (!matcher.find() || matcher.groupCount() != 3) {
            throw new ParseException("Expected format: amazon:bucket/file", 0);
        }
        String bucket = matcher.group(1);
        String folder = matcher.group(2);
        String file = matcher.group(3);
        return new AmazonUrl(bucket, folder, file);
    }

    public String getFolder() {
        return mFolder;
    }

    public String getBucket() {
        return mBucket;
    }

    public void setBucket(String bucket) {
        this.mBucket = bucket;
    }

    public String getFile() {
        return mFile;
    }

    public void setFile(String file) {
        this.mFile = file;
    }

    public String getUrl() {
        return "amazon:" + mBucket + "/" + mFolder + "/" + mFile;
    }

    public String getPath() {
        return mFolder + "/" + mFile;
    }
}
