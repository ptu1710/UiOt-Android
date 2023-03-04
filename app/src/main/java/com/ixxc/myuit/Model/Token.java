package com.ixxc.myuit.Model;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access_token")
    public String access_token;
    @SerializedName("expires_in")
    public String expires_in;
    @SerializedName("scope")
    public String scope;
}
