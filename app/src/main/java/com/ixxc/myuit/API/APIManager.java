package com.ixxc.myuit.API;

import android.util.Log;

import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.Interface.APIInterface;
import com.ixxc.myuit.Model.Token;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class APIManager {
    private static final APIInterface AI = APIClient.getClient().create(APIInterface.class);

    public static void getToken(boolean isPublic, String code) {
        Call<Token> call =  AI.getToken(GlobalVars.authType, code, GlobalVars.client, GlobalVars.redirectUrl);
        try {
            Response<Token> response = call.execute();
            if (response.isSuccessful()) {
                Token token = response.body();
                if (isPublic) {
                    APIClient.PublicToken = token.access_token;
                } else {
                    APIClient.UserToken = token.access_token;
                }
            }
            else { Log.d("API LOG", "getToken: Not Successful"); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
