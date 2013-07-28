package com.strollimo.android.network;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonUrl {
    private String mBucket;
    private String mFile;

    public AmazonUrl(String bucket, String file) {
        this.mBucket = bucket;
        this.mFile = file;
    }

    public static AmazonUrl fromUrl(String url) throws ParseException {
        Matcher matcher = Pattern.compile("amazon:(.*)/(.*)").matcher(url);
        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new ParseException("Expected format: amazon:bucket/file", 0);
        }
        String bucket = matcher.group(1);
        String file = matcher.group(2);
        return new AmazonUrl(bucket, file);
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
        return "amazon:" + mBucket + "/" + mFile;
    }
}
