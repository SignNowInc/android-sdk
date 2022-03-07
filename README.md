# SignNow SDK for Android

Simple SDK to integrate SignNow in your application. The current version of SDK allows you to fetch documents metadata and setup the signing process. Whole signing flow handled under the hood of SDK and you don't need to handle it by yourself. Can be used with kotlin and java projects.

### <a name="table-of-contents"></a>Table of Contents

1. [Requirements](#requirements)
2. [Credentials Needed](#credentials)
3. [Setup](#setup)
4. [Starting session](#starting-session)
5. [Document fetching](#document-fetching)
6. [Document preview loading](#document-preview-loading)
7. [Signing flow](#signing-flow)
      
## <a name="requirements"></a>Requirements 

Minimum supported Android API is 21

## <a name="credentials"></a>Credentials
Before getting started, a `clientId` and `clientSecret` must be created. The SDK cannot be used without these.
Visit https://app.signnow.com/webapp/api-dashboard/keys to obtain them. 

More info: https://docs.signnow.com/docs/signnow/ZG9jOjM3OTI3NTY3-account

## <a name="setup"></a>Setup

### Setting up the dependency
The Signnow SDK can be added as a Gradle dependency:

```groovy
implementation "com.signnow:signnow-android-sdk:0.0.72"
```

### Setting up the SDK
The SDK can be initialized in your `Application` class with one string of code.

```kotlin
SignnowSDK.init(applicationContext, clientId, clientSecret)
```

After the SDK was initialized you must authorize it, otherwise, the session won’t be available to start. To authorize you will need to pass `grantType` parameter and an instance of `SNResultCallback<AccessToken>`.
Where `grantType` is one of the possible implementations of `SNGrantType` class. 

The `SNGrantType.Credentials` can be used ONLY by application owners to obtain access tokens for their accounts:

```kotlin
val grantType = SNGrantType.Credentials("app.owner@mail.com", "password")
```

To get access to resources of other signNow accounts, use `SNGrantType.AuthCode`.
The `SNGrantType.AuthCode` requires authorization code. The authorization code can be obtained like this:

```kotlin
SignnowSDK.obtainAuthorizeCode(object : SNResultCallback<String> {
        override fun onResult(value: String) {
            //authorization flow...
        }
        override fun onError(error: Throwable?) {
            //Handle error...
        }
})
```

❗During authorization must be used credentials of the Signnow account which contains documents(templates) which will be used in your application flow

The authorization process itself described in code block bellow:

```kotlin
SignnowSDK.authorize(grantType, onAccessTokenReceived)
```

If authorization was successful an `SNAccessToken` data class will be received in the `onResult` method of the `SNResultCallback<AccessToken>`. If any exception occurred during authorization you will be notified about it in the `onError` method of the foregoing callback.

> The authorizing process can be started only once. And data received in result can be used during all following sessions until the access token will be refreshed. But the SDK init method must be called each time an application starts otherwise an exception will be thrown.


## <a name="starting-session"></a>Starting Session
After successful authorization session can be started as described below:

```kotlin
SignnowSession.startSession(accessToken, refreshToken)
```

Where `accessToken` and `refreshToken` were received during authorization. By default `AccessToken` will be refreshed automatically. If you want to know when `AccessToken` was refreshed you can add a listener:

```kotlin
SignnowSession.addOnTokenRefreshListener(onTokenRefreshListener)
```

> If you wish to refresh `AccessToken` manually you can do this by calling `SignnowSession.refreshToken` method.

## <a name="document-fetching"></a>Document Fetching
After the session has been started you will be available to fetch documents(templates) existing in the account used during the Authorization process. To do so you need to use `DocumentProvider` which is available in `SignnowSDK` object.

The `DocumentProvider` allows you to fetch documents(templates) in several ways or receive a metadata of the particular document using its `id`.

## <a name="document-preview-loading"></a>Document Preview Loading
If you need to get a document preview, you need to use the url stored in `SNDocument.thumbnail`. To load an image using the url get from that parameter you need to configure your image loading library with a custom interceptor. When adding your custom interceptor the only thing you need it’s to add a header. The Header data can be received using `SignnowSDK.getImageLoadingHeader` method, which will return `name` and `value` for the header.
As example will be used [Coil](https://coil-kt.github.io/coil/) image loading library and its `ImageLoader`:

```kotlin
ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .okHttpClient {
                val client = OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .addInterceptor { chain ->
                        val (name, value) = SignnowSDK.getImageLoadingHeader(accessToken)
                        val newRequest = chain.request().newBuilder()
                            .addHeader(name, value)
                            .build()
                        chain.proceed(newRequest)
                    }
                    .build()
                client
            }
            .build()
```

## <a name="signing-flow"></a>Signing Flow
After you fetch the documents(templates) you can start the signing session for each of them. The `SigningSession` object is responsible for starting the signing flow.
The signing flow can be started as described below:

```kotlin
SigningSession.startSigning(activityLauncher, documentId, recipients, fieldValues)
```

Where `activityLauncher` is an instance of `ActivityResultLauncher` which is a part of new [ActivityResultAPI](https://developer.android.com/training/basics/intents/result). The `documentId` is the id of the document for which the signing session will be started. The recipients is a list of `SNRecipient` objects. `SNRecipient` object contains email of a person who is involved in the signing session. The signing flow will be constructed for recipients in the order they were placed.
The SDK allows you to pre-fill(fill fields with your data) fields before signing session starts. The `fieldValues` parameter is responsible for this. It’s just a list with `SNFieldValue` objects. Each object contains a key-value pair, where the key is a unique field name and value is data that will be pre-filled. These values will be set to the fields before signing session and will be visible for the signer(this parameter is not mandatory).

> Email used during SDK authorization cannot be used in a signing session as a recipient.

The result of signing(successful or not) will be received by the `activityLauncher` instance used in the `SignnowSession.startSigning` method.

> If you don't want to use `ActivityResultLauncher` you still have the possibility to use the old approach with request codes. If the old way is used you will get the result in `onActivityResult` method.
