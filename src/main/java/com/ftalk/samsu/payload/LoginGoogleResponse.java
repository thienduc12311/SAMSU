package com.ftalk.samsu.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginGoogleResponse  {
    private JwtAuthenticationResponse jwtAuthenticationResponse;
    private String email;
    private boolean firstTime;

    public LoginGoogleResponse(JwtAuthenticationResponse jwtAuthenticationResponse, String email) {
        this.jwtAuthenticationResponse = jwtAuthenticationResponse;
        this.email = email;
        this.firstTime = false;
    }

    public LoginGoogleResponse(String email) {
        this.jwtAuthenticationResponse = null;
        this.email = email;
        this.firstTime = true;
    }
}
