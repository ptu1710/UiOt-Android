package com.ixxc.uiot;

public class GlobalVars {
    // TODO: Remove some useless variables
     public static String baseUrl = "https://uiot.ixxc.dev/";
    public static String redirect_url = baseUrl.replace(":", "%3A").replace("/", "%2F");
    public static String signUpUrl = baseUrl + "auth/realms/master/protocol/openid-connect/registrations?client_id=openremote&response_type=code&redirect_uri=" + redirect_url + "manager%2F";
    public static String authType = "password";
    public static String client = "openremote";
    public static String WIDGET_KEY = "WIDGET";
    public static String LOG_TAG = "API_LOG";
}
