Welcome to the Conviva ExoPlayer Unified Sensor Integration!

# Initialization

This integration requires Conviva Customer Key and Gateway URL. Please reach out to Conviva account team to retrieve your key and URL.

## 1. Incorporating Conviva Android SDK
Conviva Exoplayer Library is built upon Conviva Android SDK. Please download the latest Android SDK and Exoplayer module and include it in your application. Please download the latest Android SDK and ExoPlayer module and include it in your application.

☞ NOTE: Add jcenter() repository in your root file to automatically install via Gradle.
Add Conviva core SDK as shown below.

	implementation 'com.conviva.sdk:conviva--sdk:4.0+'
	implementation 'com.conviva.sdk:conviva-exoplayer-sdk:4.0+'
	

## 2. Configuration
Initialize ConvivaAnalytics and use init() method to configure the settings.
	
	public static void init(Context applicationContext, String customerKey);
   	public static void init(Context context, String customerKey, Map<string, object> settings);
|Parameter|Description|
|---------|-----------|
|Context |Android application context|
|CustomerKey|String to identify specific customer account. This is different for development and production environments|
|Settings|Map of all Conviva settings. These are the allowed values.<br/><br/>		```ConvivaSdkConstants.GATEWAY_URL (Touchstone URL)```<br/>		```ConvivaSdkConstants.LOG_LEVEL. This enables debug logs.```<br/><br/>Replace settings object with null in production.|

	if(BuildConfig.DEBUG){
	Map<String, Object&t; settings = new HashMap<String, Object>();
	String gatewayUrl = "Touchstone Service URL";
	settings.put(ConvivaSdkConstants.GATEWAY_URL, gatewayUrl);
	settings.put(ConvivaSdkConstants.LOG_LEVEL, ConvivaSdkConstants.LogLevel.DEBUG);
	ConvivaAnalytics.init (getApplicationContext(), TEST_CUSTOMER_KEY, settings);
	}else{
	       //production release
	ConvivaAnalytics.init (getApplicationContext(), PRODUCTION_CUSTOMER_KEY);
	}

☞ IMPORTANT: Please remove the Touchstone gatewayUrl and LogLevel settings for your production release.


## 3. Initializing Conviva Video Analytics




