Welcome to the Conviva ExoPlayer Unified Sensor Integration!

# Initialization

This integration requires Conviva Customer Key and Gateway URL. Please reach out to Conviva account team to retrieve your key and URL.

## 1. Incorporating Conviva Android SDK
Conviva Exoplayer Library is built upon Conviva Android SDK. Please download the latest Android SDK and Exoplayer module and include it in your application. Please download the latest Android SDK and ExoPlayer module and include it in your application.

☞ NOTE: Add jcenter() repository in your root file to automatically install via Gradle.
Add Conviva core SDK as shown below.

   implementation 'com.conviva.sdk:conviva-core-sdk:4.0+'
   implementation 'com.conviva.sdk:conviva-exoplayer-sdk:4.0+'

## 2. Configuration
Initialize ConvivaAnalytics and use init() method to configure the settings.

   public static void init(Context applicationContext, String customerKey);
   public static void init(Context context, String customerKey, Map<string, object> settings);



## 3. Initializing Conviva Video Analytics




