package com.ftalk.samsu.payload.notification;

import lombok.Data;

@Data
public class TokenResponse {
    private Integer id;
    private String token;

    public TokenResponse(Integer id, String token) {
        this.id = id;
        this.token = token;
    }
}
