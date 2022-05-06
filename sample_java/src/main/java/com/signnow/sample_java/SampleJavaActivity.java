package com.signnow.sample_java;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.signnow.sampletokenstorage.TokenStorage;
import com.signnow.sdk.SigningSession;
import com.signnow.sdk.SignnowSDK;
import com.signnow.sdk.SignnowSession;
import com.signnow.sdk.data.external.SNRecipient;
import com.signnow.sdk.data.external.SNResultCallback;
import com.signnow.sdk.data.external.SNSigningSessionResult;
import com.signnow.sdk.data.external.document.SNDocument;
import com.signnow.sdk.data.external.token.SNAccessToken;
import com.signnow.sdk.network.listeners.OnTokenRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SampleJavaActivity extends AppCompatActivity implements OnTokenRefreshListener {

    private TokenStorage tokenStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_java);
        tokenStorage = new TokenStorage(this);

        String accessToken = tokenStorage.getAccessToken();
        String refreshToken = tokenStorage.getRefreshToken();

        if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(refreshToken)) {
            SignnowSession.startSession(accessToken, refreshToken);
            SignnowSession.addOnTokenRefreshListener(this);
        }

        findViewById(R.id.btn).setOnClickListener(v -> {
            String documentId = "6eaa641e394647dba16acad4f74f4825229bf6da";
            SignnowSDK.getDocumentsProvider().getDocumentMetadata(documentId, null, new SNResultCallback<SNDocument>() {
                @Override
                public void onResult(SNDocument value) {
                    String roleName = value.getFields().get(0).getRole();
                    SNRecipient recipient = new SNRecipient("sckalper@gmail.com", roleName);
                    ArrayList<SNRecipient> recipients = new ArrayList<>();
                    recipients.add(recipient);
                    //start session
                }

                @Override
                public void onError(@Nullable Throwable error) {
                    Log.e(SignnowSDK.LOG_TAG, "Error thrown while trying to get document", error);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        SignnowSession.removeTokenRefreshListener();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(SignnowSDK.LOG_TAG, "ResultCode: " + resultCode);
    }

    @Override
    public void onTokenRefreshed(@NotNull SNAccessToken accessToken) {
        tokenStorage.setAccessToken(accessToken.getToken());
        tokenStorage.setRefreshToken(accessToken.getRefreshToken());
    }

    @Override
    public void onTokenRefreshFailed(@Nullable Throwable error) {
        Log.e(SignnowSDK.LOG_TAG, "Failed to refresh token", error);
    }
}