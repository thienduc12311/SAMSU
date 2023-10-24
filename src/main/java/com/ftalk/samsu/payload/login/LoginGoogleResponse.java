package com.ftalk.samsu.payload.login;

import com.ftalk.samsu.payload.login.JwtAuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginGoogleResponse  {
    private JwtAuthenticationResponse jwtToken;
    private String email;
    private boolean firstTime;

    public LoginGoogleResponse(JwtAuthenticationResponse jwtAuthenticationResponse, String email) {
        this.jwtToken = jwtAuthenticationResponse;
        this.email = email;
        this.firstTime = false;
    }

    public LoginGoogleResponse(String email) {
        this.jwtToken = null;
        this.email = email;
        this.firstTime = true;
    }
}
