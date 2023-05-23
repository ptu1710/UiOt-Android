package com.ixxc.uiot;

public class GlobalVars {
     public static String baseUrl = "https://orssl.switzerlandnorth.cloudapp.azure.com/";
    public static String baseUrl1 = "https://myordemo.northeurope.cloudapp.azure.com/";
    public static String oauth2Redirect = baseUrl + "swagger/oauth2-redirect.html";
    public static String redirect_url = baseUrl.replace(":", "%3A").replace("/", "%2F");
    public static String getCodeUrl = baseUrl + "auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=openremote&redirect_uri=" + redirect_url + "swagger%2Foauth2-redirect.html&state=AAAA";
    public static String signUpUrl = baseUrl + "auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=openremote&redirect_uri=https%3A%2F%2Forssl.switzerlandnorth.cloudapp.azure.com%2Fswagger%2Foauth2-redirect.html&state=AAAA";
    public static String authType = "authorization_code";
    public static String client = "openremote";
    public static String LOG_TAG = "API LOG";

}
