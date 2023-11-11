package com.ftalk.samsu.utils.event;

import com.ftalk.samsu.model.event.EventProposal;
import com.ftalk.samsu.payload.event.EventProposalResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventUtils {
    private static final String DOMAIN_S3 = "https://samsu.sgp1.cdn.digitaloceanspaces.com/";
    private static final String SPLIT_KEYWORD = "$$$";
    public static boolean validateFileUrlsS3(String fileUrls){
        return !Arrays.stream(fileUrls.split(SPLIT_KEYWORD)).anyMatch((s) -> !validateUrlS3(s));
    }

    public static boolean validateUrlS3(String url){
        return url.startsWith(DOMAIN_S3);
    }

    public static List<EventProposalResponse> listToList(List<EventProposal> eventProposals) {
        return eventProposals.parallelStream()
                .map(EventProposalResponse::new)
                .collect(Collectors.toList());
    }
}