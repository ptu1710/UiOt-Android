package com.ixxc.uiot;

public class GlobalVars {
    public static String client = "openremote";
    public static String baseUrl = "https://ngocrongvsp.fun/";
//    public static String baseUrl = "https://uiot.ixxc.dev/";

    // encodedBaseUrl is used for OAuth2 redirect
    private static final String encodedBaseUrl = baseUrl.replace(":","%3A").replace("/","%2F");
    public static String redirectUrl = baseUrl + "swagger/oauth2-redirect.html";
    public static String getCodeUrl = baseUrl + "auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=" + client + "&redirect_uri=" + encodedBaseUrl + "swagger%2Foauth2-redirect.html&state=AAAA";
    public static String resetPwdUrl = baseUrl + "auth/realms/master/login-actions/reset-credentials?client_id=openremote";
    public static String signUpUrl = baseUrl + "auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=" + client + "&redirect_uri=" + encodedBaseUrl + "swagger%2Foauth2-redirect.html&state=AAAA";
    public static String authType = "authorization_code";
    public static String LOG_TAG = "API LOG";

}
