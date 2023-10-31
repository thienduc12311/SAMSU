package com.ftalk.samsu.utils.event;

import java.util.Arrays;

public class EventUtils {
    private static final String DOMAIN_S3 = "https://";
    private static final String SPLIT_KEYWORD = "$$$";
    public static boolean validateFileUrlsS3(String fileUrls){
        return !Arrays.stream(fileUrls.split(SPLIT_KEYWORD)).anyMatch((s) -> !validateUrlS3(s));
    }

    public static boolean validateUrlS3(String url){
        return url.startsWith(DOMAIN_S3);
    }
}
