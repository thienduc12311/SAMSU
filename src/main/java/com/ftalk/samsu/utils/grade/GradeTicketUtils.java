package com.ftalk.samsu.utils.grade;

import com.ftalk.samsu.utils.AppConstants;

public class GradeTicketUtils {
    private static String linkUrl = AppConstants.APP_URL + "/guarantorVerify/";

    public static String genInfoSenderEmail(String email, String code) {
        String link = linkUrl + code;
        String rs = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Access Your Grade Ticket</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>Dear " + email + ",</p>\n" +
                "    <p>We hope this email finds you well. You have been granted access to review a Grade Ticket submitted by a user. Please use the following link to access the Grade Ticket and provide your valuable feedback:</p>\n" +
                "\n" +
                "    <a href=\"+" + link + "\" target=\"_blank\">Access Grade Ticket</a>\n" +
                "\n" +
                "    <p>This link is unique to you and should not be shared with others. If you encounter any issues or have questions, please do not hesitate to contact us.</p>\n" +
                "\n" +
                "    <p>Thank you for your prompt attention to this matter.</p>\n" +
                "\n" +
                "    <p>Best regards,<br>[Your Organization's Name]<br>srohcm@fe.edu.vn</p>\n" +
                "</body>\n" +
                "</html>";
        return rs;
    }
}
