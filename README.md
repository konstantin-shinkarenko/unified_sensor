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
