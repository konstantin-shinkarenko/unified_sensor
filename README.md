[![Alternate Text]({image-url})]({https://www.youtube.com/watch?v=9kRgVxULbag} "Link Title")

Welcome to the Conviva ExoPlayer Unified Sensor Integration!

This document explains the Conviva ExoPlayer module integration within a Video Player which uses ExoPlayer. The integration is considered complete when all the asset metadata and player events are monitored accurately by CONVIVA ExoPlayer module.

	NOTE: This documentation is for Android SDK 4.0.2 and above. If you are using the legacy SDK, we highly recommend you upgrade to the new SDK below. For reference, the deprecated legacy SDK documentation can be found here.

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

CONVIVA SDK relies on an instance of ConvivaVideoAnalytics to monitor Video. Initialize instance of ConvivaVideoAnalytics as shown below:

	// videoAnalytics would be used throughout the integration.
	ConvivaVideoAnalytics  videoAnalytics = ConvivaAnalytics.buildVideoAnalytics(getApplicationContext());

☞ NOTE: In an application with multiple videos, each video should have separate instance of ConvivaVideoAnalytics.

## 4. Creating ExoPlayer Instances

Use the ExoPlayerFactory class newSimpleInstance() API to create and ExoPlayer instance.

	SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance (Context context, RenderersFactory renderersFactory, TrackSelector trackSelector, DrmSessionManager<frameworkmediacrypto> drmSessionManager);

# Monitoring Main Content

The main purpose of Conviva Integration is to measure quality of experience by monitoring videos from the moment a user initiates playback until the video stops.

Use reportPlaybackRequested method to start monitoring video. Please note that each video should be monitored separately.

Similarly, reportPlaybackEnded method should be called when video is not playing anymore. Refer to below table to understand the difference scenarios:

|Invoke ReportPlaybackRequested() As Soon As|Invoke ReportPlaybackEnded() As Soons As|
|-------------------------------------------|----------------------------------------|
|A user clicks on play button of the video. |A user stops the video.|
|A new video starts in autoplay.	    |An autoplay video ends.|
|An audio new item starts in playlist.	    |An item ends in playlist.|

When an actual content player object is available and initialized, invoke the setPlayer() API and pass player object as an argument to allow Conviva SDK to monitor the playback metrics.

	// Once player is available
	videoAnalytics.setPlayer(contentPlayer);// for ex: instance of simpleExoplayer

	// Syntax of the API to report the request of playback initiating the viewers experience.
	void reportPlaybackRequested(Map<String, Object> contentInfo);

	// So when user clicks on play, inside the play() function of the video player.
	videoAnalytics.reportPlaybackRequested(contentInfo);

## 1. Reporting Metadata

Metadata of the video should be reported as tags via contentInfo map object.

Create a dictionary Map with the video metadata that will be collected when you associate it with a video playback using setContentInfo() API.

	// Syntax of the API to set the Content Metadata
	void setContentInfo (Map<String, Object> contentInfo);
	
Conviva categorizes metadata tags into three buckets:

* Pre-defined Tags that must be reported as part of contentInfo for all videos.
* Device related tags that automatically detected by Conviva backend.
* Custom Tags per business requirement, specific to each customer.

### Pre-Defined Tags

All the mandatory tags are defined below along with their explanations. A code sample is shown after the table.

|Description|Key|Type|Required|Notes|
|-----------|---|----|--------|-----|
|Stream URL|STREAM_URL|string|Y|The URL from which the video is initially loaded.|
|Asset Name|ASSET_NAME|string|Y|Unique name for each stream/video asset. Values are your choice, but a human-readable text prefixed with the unique video ID works best in most Conviva SDK's.<br/>This provides for clarity in reports and makes most popular content easily identifiable.Pattern: [videoID] Video Title<br/>The following are typical patterns for VOD (movies and episodic content) and Live streams:<br/>```Movie Pattern: [{contentId}] {Movie Title}```<br/>```Sample Value: [12345] The ABC Movie```<br/>```Episode Pattern: [{contentId}] {Show Title} - S:{Season Number}:E{Episode Number} - {Episode Title}```<br/>```Sample Value: [67890] The XYZ Show - S3:E1 - The Pilot Episode```<br/>```Live Stream Pattern: [{channelNumber}] {Chanel Name}```<br/>```Sample Value: [10] PQRS Bay Area```|
|Stream Type|IS_LIVE|string|Y|Denotes whether the content is video on-demand or a live stream. Affects the computation and availability of the Conviva metrics.<br/><br/>```{Enum – ConvivaSdkConstants.StreamType.UNKNOWN/LIVE/VOD}```|
|Player Name|PLAYER_NAME|string|Y|A string value used to distinguish individual apps, players, locations, platforms, and/or deployments. Simple values that are unique across all of your integrated platforms work best here. Do not include the build or version number in this property. The intention is to have a higher-level value to easily configure a filter to isolate the metrics of this Player|
|Viewer ID|VIEWER_ID|string|Y for Viewers Module.|A unique identifier to distinguish individual viewers or devices through Conviva's Viewers Module (purchase required). The identifier may be a user's unique username, an email address, a unique device identifier, or a generated GUID.|
|Default Resource|DEFAULT_RESOURCE|string|N|Default video server resource to report for the content. Use when the video server resource cannot be directly inferred from streamUrl.<br/>Examples: EDGECONVIVA SDKT-1, AKAMAI-FREE, LEVEL3-PREMIUM...|
|Duration|DURATION|integer|N|Duration of the video content, in seconds.|
|Encoded Frame Rate|ENCODED_FRAMERATE|integer|N|Encoded frame rate of the video in frames per second.|

Conviva defines few recommended metadata, refer to the ConvivaSdkConstants class for more content metadata parameters.

### Custom Tags

☞ HIGHLY RECOMMENDED: A dictionary defining custom string key/value pairs. You can filter and categorize metrics in Experience Insights (Pulse) based on your specific metadata use-case.


See Integrate: Tags for further details. To add custom tags, add key value pair of String and any Object to the Map.

	// Sample code snippet
	// Dictionary for Content Metadata
	Map<String, Object> contentInfo = new HashMap<String, Object>();
	contentInfo.put("key", "value");

	videoAnalytics.setContentInfo(contentInfo);
	
The following is a code example that creates a Dictionary Map for ContentMetadata along with custom tags:

	// Sample code snippet
	// Dictionary for Content Metadata
	Map<String, Object> contentInfo = new HashMap<String, Object>();
	contentInfo.put(ConvivaSdkConstants.ASSET_NAME, "ASSET_NAME");
	contentInfo.put(ConvivaSdkConstants.PLAYER_NAME, "PLAYER_NAME");
	contentInfo.put(ConvivaSdkConstants.ENCODED_FRAMERATE, 20);
	contentInfo.put(ConvivaSdkConstants.DURATION, 30);
	contentInfo.put(ConvivaSdkConstants.DEFAULT_RESOURCE, "DEFAULT_RESOURCE");
	contentInfo.put(ConvivaSdkConstants.STREAM_URL, "STREAM_URL");
	contentInfo.put(ConvivaSdkConstants.IS_LIVE, false);
	contentInfo.put(ConvivaSdkConstants.VIEWER_ID, "VIEWER_ID");
	contentInfo.put("key", "value");

	videoAnalytics.setContentInfo(contentInfo);
	
### Update Content Metadata

Not all metadata values can be updated at any time. In certain situations, your application may not have all the content metadata initially when reportPlaybackRequested is called. For example, when metadata is fetched from a content management system or metadata requires authentication for encrypted content.

Updating metadata or setting metadata late; after start monitoring of content is subject to certain limitations. Some metadata can be set or updated:

* Before the first video frame is rendered.
* Anytime during video playback; but only once.

The following table indicates the various update scenarios that can be seen in Pulse (Experience Insights), for each metadata item:

|Metadata|Set Only Once|Update Before First Video Frame|Multiple Updates During Session|
|--------|-------------|-------------------------------|-------------------------------|
|Asset Name|✔|||
|Viewer Id||✔|||
|Stream Type||✔|||
|Player Name||✔|||
|Content Length||✔|||
|Custom Tags||✔|||
|Resource|||✔|
|Stream URL|||✔|
|Bitrate|||✔|
|Encoded Frame Rate|||✔|

You cannot currently update any other metadata items.

To update the above metadata values, use the setContentInfo() API. For specific APIs to update Bitrate, refer to Set Bitrate section in the table below.

	// Sample code snippet
	Map<String, Object> newContentInfo = new HashMap<String, Object>();
	newContentInfo.put(ConvivaSdkConstants.ASSET_NAME, "New assetName");
	newContentInfo.put(ConvivaSdkConstants.STREAM_URL, "http://newstreamurl.conviva.com/");
	newContentInfo.put("newTag1", "newVal1");

	// Update the current video monitoring experience with metadata changes:
	videoAnalytics.setContentInfo(newContentInfo);

### Report Metric API Usage

Use the void reportPlaybackMetric(String key, Object… val); API to report player/playback related events. It accepts multiple/zero values for the second argument.

Refer to this table for the usage of this API with various events:

|Reporting Event Name|Description|API Usage|
|--------------------|-----------|---------|
|Errors|Player errors are auto collected by the Conviva module. All additional video related errors which stall playback should be reported as fatal errors to Conviva via the CONVIVA SDK.<br/>☞ NOTE: If there are warnings from player, please do not report it to Conviva. Report only those errors which stops the playback. Usually all video players have a method which can be used.|```// setting of Content Info is optional```<br/>```reportPlaybackFailed(errorMessage, contentInfo);```|
|Player States|All player states such as Playing, buffering etc. should be reported to CONVIVA SDK.||
|Seek Events|Seeking or scrubbing should be reported to CONVIVA SDK when user starts seeking and stops seeking.||
|Play Head Time|Report PlayHead Time.||
|Buffer Length|Report Buffer length of the player.||
|Rendered Framerate|Rendered framerate in fps.|```reportPlaybackMetric(ConvivaSdkConstants.PLAYBACK.RENDERED_FRAMERATE, 100);```|
|CDN IP|CDN IP address in string format.<br/>☞ NOTE: The Conviva ExoPlayer module can also auto collect the CDN IP. Please contact Conviva Support to enable auto collection.|```reportPlaybackMetric (ConvivaSdkConstants.PLAYBACK.CDN_IP, ipaddr);```|

# Handle Additional Events

## 1. Handling Application Background and Foreground

Backgrounding states occur when:

* The home button is pushed.
* The power button is pushed.
* A phone call is received and answered.
* Any event that puts the application in the background.

Background and Foreground are considered to be application’s global events.

### User Initiated Backgrounding

When a video is playing and the app goes to the background due to a user action (for example; pressing, lock or answering a phone call), the video pauses. Follow these steps to handle backgrounding:

* When the app pushed to background, invoke ConvivaAnalytics.reportAppBackgrounded().
* When the app resumes, invoke ConvivaAnalytics.reportAppForegrounded().

If your app does not run in the background, the Conviva heartbeat will not be sent.

### Split View/Mult-Window on Android Devices

The primary app and the secondary app will run in the foreground. Ensure that the application creates two instances of Conviva client, sets up metadata twice, creates two sessions, sends playback event for each session in its corresponding session and then closes both the sessions. With this implementation, the metadata and metrics displayed are known to work in Conviva pulse dashboard.

### Picture-in-Picture (PiP)

The SDK continues to report heartbeats when the video plays in Picture-in-Picture mode.

## 2. Reporting Network Metrics

To enable Conviva library to collect the network metrics, the application must follow the Android permission instructions provided below. If the necessary permissions are not added in the application's manifest file, the Conviva library will report the default values, after performing the necessary security checks.

	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

* In Android M and later versions (API 23 and above), please add a runtime permission request for android.permission.ACCESS_COARSE_LOCATION.
* In Android Q and later versions (API 29 and above), please add a runtime permission request for android.permission.ACCESS_FINE_LOCATION.

For more details, see Android permissions.

	if (Build.VERSION.SDK_INT >= 23) {
	   if (!(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
	     requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
	     }
	 }

	if (Build.VERSION.SDK_INT >= 29) {
	   if ( !(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
	     requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
	     }
	 }
	 
List of metrics that require permissions and collection:

|Metric|Permission Required|Automatically Collected|
|------|-------------------|-----------------------|
|Data Saver|```android.permission.ACCESS_NETWORK_STATE```|No|
|Connection Type|```android.permission.ACCESS_NETWORK_STATE```|Yes<br/>☞ NOTE: The Conviva SDK will report raw values returned by the Connection Type. Connection type can be updated after session creation, before the first video frame is rendered.|
|Link Encryption|```android.permission.ACCESS_WIFI_STATE```|Yes|
|Wi-Fi Signal Strength|```android.permission.ACCESS_WIFI_STATE```|Yes<br/>☞ NOTE:Android SDK reports signal strength in decibel-milliwatts (dBm). If the information is not available, the default value is 1000.|
|Cellular Signal Strength|```android.permission.ACCESS_COARSE_LOCATION```<br/>```android.permisson.ACCESS_FINE_LOCATION```|Yes<br/>☞ NOTE: Android SDK reports signal strength in decibel-milliwatts (dBm). If the information is not available, the default value is 1000.|

### Cellular Signal Strength for WCDMA

Android SDK provides support for WCDMA signal strength from Android API 18 onwards, therefore Conviva SDK will not be able to report it below API level 18.

* Android L devices: Signal strength information for cellular networks in not available in Android L devices therefore Conviva SDK will not be able to report it. This is also captured as Google Android Issue: 60430.
* Android Q and above devices: With the release of Android 29, the functionality of the getAllCellInfo() API used for fetching the cellular networks signal strength information has changed. Your applications must add a new permission ACCESS_FINE_LOCATION for all devices using Android Q and above.

## 3.Data Saver

The Conviva SDKis designed in accordance with the Data Saver capabilities of Android N. When a user enables Data Saver in Settings and the device is on a metered network, the system blocks background data usage and signals apps to use less data in the foreground wherever possible.

If Data Saver setting is enabled and the app is white-listed, the Conviva SDK will continue to send heartbeats as the application is allowed to use data even on metered network. But if the app is not white-listed or is blocked, no heartbeat will be sent in the foreground as well as the background.

☞ NOTE: Conviva does monitor the change in the Data Saver settings during a running instance of the application and acts accordingly.

## 4.Implementing Player Insight

Player Insight is an advanced feature which allows you to track custom events that are not related to video rendering, but rather specific to your player's functionality. These events and their attributes are then tabulated in the Player Insight dashboard at Conviva Experience Insights (Pulse). Contact Conviva SC to enable Player Insights in Pulse.

You may send a custom Player Insight event that can be associated with a video playback using the following method:

	// Syntax of the API to report the player insight event
	void reportPlaybackEvent(String eventType, Map<String, Object> eventDetail);

	// Sample code snippet of event TestEvent with two attributes
	Map<string, object> attr = new HashMap<>();
	Attr.put(“attr1”, “sample value”);
	videoAnalytics.reportPlaybackEvent(eventType, attr);
	
Use this method to send a custom Player Insight event specific to player’s functionality but not associated with a video playback:

	// Sample code snippet
	String eventType = "share-click";
	Map<String, Object> attr = new HashMap<> ();
	attr.put("location", "Toolbar");
	attr.put("assetName", "Sample Video");
	attr.put("shareService", "Facebook");
	ConvivaAnalytics.reportAppEvent(eventType, attr);
	
## 5.Handling Video Changes

In some Conviva SDK’s the video playback may lose the focus due to any of these reasons:

* Login/Passcode dialog pop up, when a user initiates video playback and the application prompts, the user for login/password.
* Bumper Video use cases.

In such Conviva SDK's, use the below provided API to notify the SDK about the focus change and to pause monitoring the main content, and still continue to capture the main video content metrics other than Video Restart Time (VRT) and Video Startup Time (VST).

	// Syntax of the API to report the user actions using Player Insight Event
	reportPlaybackEvent(/*string */ eventType);

	// To report start of login dialog pop up or bumper video
	videoAnalytics.reportPlaybackEvent(ConvivaSdkConstants.USER_WAIT_STARTED);
	videoAnalytics.reportPlaybackEvent(ConvivaSdkConstants.BUMPER_VIDEO_STARTED);

	// To report exit/completion of login dialog pop up or bumper video
	videoAnalytics.reportPlaybackEvent(ConvivaSdkConstants.USER_WAIT_ENDED);
	videoAnalytics.reportPlaybackEvent(ConvivaSdkConstants.BUMPER_VIDEO_ENDED);
	
## 6.Validating Full Integration

Please refer to the Integration Validation: Experience Insights page.

### Metrics and Metadata Tables

|Experience Insights|Metrics|Supported (Y/N)|Automatically Collected (Y/N)|Notes/Limitations|
|-------------------|-------|---------------|-----------------------------|-----------------|
|Quality-Video Startup|Attempts|Y|Y||
||Video Start Failure (VFS)|Y|Y|
||Exits Before Video Starts (EBVS)|Y|Y|
||Plays|Y|Y|
||Video Startup Time (VST)|Y|Y|
|Quality-Video Playback|Concurrent Plays|Y|Y|
||Video Playback Failures (VPF)|Y|Y|
||Rebuffering Ratio|Y|Y|
||Average Bitrate|Y|Y|Bitrate is automatically detected by Conviva for com.google.android.exoplayer2.ExoPlayer. Refer to the Average Bitrate limitation.|
||Ended Plays|Y|Y|
||Video Restart Time|Y|Y|
||Connection Induces Rebuffering Ratio|Y|Y|
||Average Frame Rate|Y|Y|
|Audience|Unique Devices|Y|Y|
||Total Minutes|Y|Y|
||Ended Plays/Unique Device|Y|Y|
||Min/Ended Play|Y|Y|
||Average % Complete|Y|Y|

|Ad Breaks (Part of Ad Insights)|Metrics|Supported (Y/N)|Automatically Collected (Y/N)|Notes/Limitations|
|-------------------------------|-------|---------------|-----------------------------|-----------------|
|Ad Breaks Analysis|Ad Breaks Started|Y|N|For implementation, please refer to Ad Breaks for Android SDK.|
||Ad Breaks Ended|Y|N|For implementation, please refer to Ad Breaks for Android SDK.|
||Ad Breaks Completed|Y|N|Depends on Ad Breaks integration. For more details, please refer to Conviva Ad Insights Integration.|
||Ad Breaks Abandoned|Y|N|Depends on Ad Breaks integration. For more details, please refer to Conviva Ad Insights Integration.|
||Ad Breaks Actual Duration|Y|N|Depends on Ad Breaks integration. For more details, please refer to Conviva Ad Insights Integration.|
||Ad Breaks / Ended Attempts|Y|N|Depends on Ad Breaks integration. For more details, please refer to Conviva Ad Insights Integration.|

## 2.Metadata for Exoplayer

|Experience Insights Dimension|Device Metadata|Required (Y/N)|Automatically Collected (Y/N)|Notes/Limitations|
|-----------------------------|---------------|--------------|-----------------------------|-----------------|
|Asset Name/Content Name|Asset Name|Y|N|Please refer to Android SDK: Configuring Metadata.|
|CDN Name|CDN Vendor|Y|Y|Auto collected only if enabled from the customer portal.|
|Stream URL|Stream URL|Y|N|Please refer to Android SDK: Configuring Metadata.|
|Live or VOD|Live or VOD|Y|N|Please refer to Android SDK: Configuring Metadata.|
|Players|Player Name|Y|N|Please refer to Android SDK: Configuring Metadata.|
|Player Framework Name|Player Framework Name or SDK|Y|Y||
|Player Framework Version|Player Framework Version|Y|Y||
|Device Type|User Agent String|N|Y||
|Viewer ID|Viewer ID|N|Y|Only required if customer has Viewer Module access. Please refer to Android SDK: Configuring Metadata.|
|Browser Name|User Agent String|Y|Y||
|Browser Version|User Agent String|Y|N/A||
|Cities|IP Address|Y|Y||
|Continents|IP Address|Y|Y||
|Conviva Library Version|Conviva Library Version|Y|Y||
|Countries|IP Address|Y|Y||
|Device Manufacturer|User Agent String|Y|Y||
|Device Name|User Agent String|Y|Y||
|Device Marketing Name|User Agent String|Y|Y||
|Device Hardware Type|User Agent String|Y|Y||
|ISP Name|IP Address|Y|Y||
|ASN|IP Address|Y|Y||
|ASN Name|IP Address|N|Y||
|DMA|IP Address|N|Y||
|Device Operating System|User Agent String|Y|Y||
|Device OS Version|User Agent String|Y|Y||

|Ad Insights Dimension|Device Metadata|Required (Y/N)|Automatically Collected (Y/N)|Notes/Limitations|
|---------------------|---------------|--------------|-----------------------------|-----------------|
|Pod Planned Duration|podDuration|Y|N|Please refer to Android SDK: Configuring Ad Breaks.|
|Pod Position|podPosition|Y|N|Please refer to Android SDK: Configuring Ad Breaks.|
|Pod Index Relative|podIndex|Y|N|Please refer to Android SDK: Configuring Ad Breaks.|
|Pod|Pod Index Absolute|N|N|Please refer to Android SDK: Configuring Ad Breaks.|

# Limitations

Please refer to the Platform specific page for limitations.

|Metadata/Metric Name|Impact|Conditions (Y/N)|Issue|
|--------------------|------|----------------|-----|
|Average Bitrate|Under reported|HLS Demuxed Streams|Player reports video bitrate but reports 0 for audio.|
||Over reported|AVERAGE-BANDWIDTH unavailable|Player reports peak bitrate if average bitrate information is unavailable in manifest file.|

☞ NOTE: ExoPlayer does not support DASH Live on Android M.





