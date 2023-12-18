package com.ftalk.samsu.utils.grade;

import com.ftalk.samsu.utils.AppConstants;

public class GradeTicketUtils {
    private static String linkUrl = AppConstants.APP_URL + "/guarantorVerify/";

    public static String genInfoSenderEmail(String email, String code) {
        String link = linkUrl + code;
        String rs = "<!doctype html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width\">\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <title>Simple Responsive HTML Email With Button</title>\n" +
                "  <style>\n" +
                "@media only screen and (max-width: 620px) {\n" +
                "  table[class=body] h1 {\n" +
                "    font-size: 28px !important;\n" +
                "    margin-bottom: 10px !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] p,\n" +
                "table[class=body] ul,\n" +
                "table[class=body] ol,\n" +
                "table[class=body] td,\n" +
                "table[class=body] span,\n" +
                "table[class=body] a {\n" +
                "    font-size: 16px !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .wrapper,\n" +
                "table[class=body] .article {\n" +
                "    padding: 10px !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .content {\n" +
                "    padding: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .container {\n" +
                "    padding: 0 !important;\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .main {\n" +
                "    border-left-width: 0 !important;\n" +
                "    border-radius: 0 !important;\n" +
                "    border-right-width: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .btn table {\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .btn a {\n" +
                "    width: 100% !important;\n" +
                "  }\n" +
                "\n" +
                "  table[class=body] .img-responsive {\n" +
                "    height: auto !important;\n" +
                "    max-width: 100% !important;\n" +
                "    width: auto !important;\n" +
                "  }\n" +
                "}\n" +
                "@media all {\n" +
                "  .ExternalClass {\n" +
                "    width: 100%;\n" +
                "  }\n" +
                "\n" +
                "  .ExternalClass,\n" +
                ".ExternalClass p,\n" +
                ".ExternalClass span,\n" +
                ".ExternalClass font,\n" +
                ".ExternalClass td,\n" +
                ".ExternalClass div {\n" +
                "    line-height: 100%;\n" +
                "  }\n" +
                "\n" +
                "  .apple-link a {\n" +
                "    color: inherit !important;\n" +
                "    font-family: inherit !important;\n" +
                "    font-size: inherit !important;\n" +
                "    font-weight: inherit !important;\n" +
                "    line-height: inherit !important;\n" +
                "    text-decoration: none !important;\n" +
                "  }\n" +
                "\n" +
                "  .btn-primary table td:hover {\n" +
                "    background-color: #185DCF !important;\n" +
                "  }\n" +
                "\n" +
                "  .btn-primary a:hover {\n" +
                "    background-color: #185DCF !important;\n" +
                "    border-color: #185DCF !important;\n" +
                "  }\n" +
                "}\n" +
                "</style></head>\n" +
                "  <body class style=\"background-color: #eaebed; font-family: sans-serif; -webkit-font-smoothing: antialiased; font-size: 14px; line-height: 1.4; margin: 0; padding: 0; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">\n" +
                "    <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"body\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; background-color: #eaebed; width: 100%;\" width=\"100%\" bgcolor=\"#eaebed\">\n" +
                "      <tr>\n" +
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>\n" +
                "        <td class=\"container\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; display: block; max-width: 580px; padding: 10px; width: 580px; Margin: 0 auto;\" width=\"580\" valign=\"top\">\n" +
                "          <div class=\"header\" style=\"padding: 20px 0;\">\n" +
                "            <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; width: 100%;\">\n" +
                "              <tr>\n" +
                "                <td class=\"align-center\" width=\"100%\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; text-align: center;\" valign=\"top\" align=\"center\">\n" +
                "                  <a href=\"https://samsu-fpt.software\" style=\"color: #185DCF; text-decoration: underline;\"><img src=\"https://samsu-fpt.software/assets/images/logo-modified-small.png\" height=\"40\" alt=\"Postdrop\" style=\"border: none; -ms-interpolation-mode: bicubic; max-width: 100%;\"></a>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </div>\n" +
                "          <div class=\"content\" style=\"box-sizing: border-box; display: block; Margin: 0 auto; max-width: 580px; padding: 10px;\">\n" +
                "\n" +
                "            <!-- START CENTERED WHITE CONTAINER -->\n" +
                "            <span class=\"preheader\" style=\"color: transparent; display: none; height: 0; max-height: 0; max-width: 0; opacity: 0; overflow: hidden; mso-hide: all; visibility: hidden; width: 0;\">This is preheader text. Some clients will show this text as a preview.</span>\n" +
                "            <table role=\"presentation\" class=\"main\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; background: #ffffff; border-radius: 3px; width: 100%;\" width=\"100%\">\n" +
                "\n" +
                "              <!-- START MAIN CONTENT AREA -->\n" +
                "              <tr>\n" +
                "                <td class=\"wrapper\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; box-sizing: border-box; padding: 20px;\" valign=\"top\">\n" +
                "                  <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; width: 100%;\" width=\"100%\">\n" +
                "                    <tr>\n" +
                "                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Dear "+email + ",</p>\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">We hope this email finds you well. You have been granted access to review a Grade Ticket submitted by a user. Please use the following link to access the Grade Ticket and provide your valuable feedback:</p>\n" +
                "\n" +
                "                        <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"btn btn-primary\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: 100%; box-sizing: border-box; width: 100%;\" width=\"100%\">\n" +
                "                          <tbody>\n" +
                "                            <tr>\n" +
                "                              <td align=\"center\" style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; padding-bottom: 15px;\" valign=\"top\">\n" +
                "                                <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: separate; mso-table-lspace: 0pt; mso-table-rspace: 0pt; min-width: auto; width: auto;\">\n" +
                "                                  <tbody>\n" +
                "                                    <tr>\n" +
                "                                      <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top; border-radius: 5px; text-align: center; background-color: #185DCF;\" valign=\"top\" align=\"center\" bgcolor=\"#185DCF\"> <a href=\""+link+"\" target=\"_blank\" style=\"border: solid 1px #185DCF; border-radius: 5px; box-sizing: border-box; cursor: pointer; display: inline-block; font-size: 14px; font-weight: bold; margin: 0; padding: 12px 25px; text-decoration: none; text-transform: capitalize; background-color: #185DCF; border-color: #185DCF; color: #ffffff;\">Access grade ticket</a> </td>\n" +
                "                                    </tr>\n" +
                "                                  </tbody>\n" +
                "                                </table>\n" +
                "                              </td>\n" +
                "                            </tr>\n" +
                "                          </tbody>\n" +
                "                        </table>\n" +
                "                        <p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">This link is unique to you and should not be shared with others. If you encounter any issues or have questions, please do not hesitate to contact us.</p>\n" +
                "<p style=\"font-family: sans-serif; font-size: 14px; font-weight: normal; margin: 0; margin-bottom: 15px;\">Thank you for your prompt attention to this matter.</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "\n" +
                "            <!-- END MAIN CONTENT AREA -->\n" +
                "            </table>\n" +
                "\n" +
                "            <!-- START FOOTER -->\n" +
                "        \n" +
                "            <!-- END FOOTER -->\n" +
                "\n" +
                "          <!-- END CENTERED WHITE CONTAINER -->\n" +
                "          </div>\n" +
                "        </td>\n" +
                "        <td style=\"font-family: sans-serif; font-size: 14px; vertical-align: top;\" valign=\"top\">&nbsp;</td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </body>\n" +
                "</html>";

        return rs;
    }
}