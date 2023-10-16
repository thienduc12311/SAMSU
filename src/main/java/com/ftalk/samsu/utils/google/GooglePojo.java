package com.ftalk.samsu.utils.google;

import lombok.Data;

@Data
public class GooglePojo {
    private String sub;
    private String email;
    private boolean email_verified;
    private String name;
    private String given_name;
    private String family_name;
    private String link;
    private String picture;
    private String locale;
    private String hd;
}
