package com.ftalk.samsu.payload.achievement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateResponse {
    private String rollnumber;
    private String certificateUrl;

}
