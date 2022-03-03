package com.signnow.sample_java;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.signnow.sampletokenstorage.TokenStorage;
import com.signnow.sdk.SignnowSDK;
import com.signnow.sdk.SignnowSession;
import com.signnow.sdk.data.external.SNGrantType;
import com.signnow.sdk.data.external.SNResultCallback;
import com.signnow.sdk.data.external.token.SNAccessToken;

import org.jetbrains.annotations.Nullable;

public class SampleJavaApp extends Application {

    private final String clientId = "f8597038e3dba482ede025b56cb7ba26";
    private final String clientSecret = "3cca133277777930457ec9266e26266c";


    @Override
    public void onCreate() {
        super.onCreate();
        TokenStorage tokenStorage = new TokenStorage(this);
        SNGrantType grantType = new SNGrantType.Credentials("pavliuk.roman@pdffiller.team", "123123");

        SNResultCallback<SNAccessToken> onAccessTokenReceived = new SNResultCallback<SNAccessToken>() {
            @Override
            public void onResult(SNAccessToken value) {
                tokenStorage.setAccessToken(value.getToken());
                tokenStorage.setRefreshToken(value.getRefreshToken());
                SignnowSession.startSession(value.getToken(), value.getRefreshToken());
            }

            @Override
            public void onError(@Nullable Throwable error) {
                Log.e(SignnowSDK.LOG_TAG, "Failed to authorize", error);
            }
        };

        SignnowSDK.init(getApplicationContext(), clientId, clientSecret);

        if (TextUtils.isEmpty(tokenStorage.getAccessToken()) || TextUtils.isEmpty(tokenStorage.getRefreshToken())) {
            SignnowSDK.authorize(grantType, onAccessTokenReceived);
        }
    }
}
