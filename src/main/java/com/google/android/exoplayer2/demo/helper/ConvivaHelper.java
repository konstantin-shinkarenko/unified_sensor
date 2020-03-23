package com.google.android.exoplayer2.demo.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.conviva.api.Client;
import com.conviva.api.SystemSettings;
import com.conviva.api.player.PlayerStateManager;
import com.conviva.sdk.ConvivaAdAnalytics;
import com.conviva.sdk.ConvivaAnalytics;
import com.conviva.sdk.ConvivaVideoAnalytics;
import com.conviva.sdk.ConvivaSdkConstants;
import com.google.ads.interactivemedia.v3.api.Ad;
import com.google.ads.interactivemedia.v3.api.AdError;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdPodInfo;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;

import java.util.HashMap;
import java.util.Map;

import static com.conviva.sdk.ConvivaSdkConstants.GATEWAY_URL;
import static com.conviva.sdk.ConvivaSdkConstants.LOG_LEVEL;

public class ConvivaHelper {

    private static final String TAG = ConvivaHelper.class.getSimpleName();
    private static final String PLAYER_APPLICATION_NAME = "ExoPlayerSample";
    private static final String PLAYER = "ConvivaHelper";
    private static  Context context;
    private static ConvivaImaAdsManagerListener imaAdsLoaderListener;
    private static ConvivaVideoAnalytics videoAnalytics;
    private static ConvivaAdAnalytics adAnalytics;

    public static void init(Context _context) {
        context = _context;

        HashMap<String, Object> settings = new HashMap<>();

        settings.put(GATEWAY_URL, "GATEWAY_URL");

        // In Production the log level need not be set and will be taken as NONE.
        settings.put(LOG_LEVEL, ConvivaSdkConstants.LogLevel.INFO);

        ConvivaAnalytics.init(_context, "CUSTOMER_KEY", settings, null);
    }

    public static ConvivaVideoAnalytics getInstance(Context context){
        return ConvivaAnalytics.buildVideoAnalytics(context);
    }

    public static void onContentPlaybackStart(ConvivaVideoAnalytics client, Object player, Intent intent, String asset) {

        HashMap<String, Object> contentInfo = new HashMap<>();

        contentInfo.put(ConvivaSdkConstants.STREAM_URL, intent.getData());

        contentInfo.put(ConvivaSdkConstants.ASSET_NAME, asset);

        // Based on the content stream type, isLive can be configured here.
        contentInfo.put(ConvivaSdkConstants.IS_LIVE, false);

        // player application name is not likely to change.
        contentInfo.put(ConvivaSdkConstants.PLAYER_NAME, PLAYER_APPLICATION_NAME);
        contentInfo.put(ConvivaSdkConstants.VIEWER_ID, "Sample App for Simplified SDK");

        //setplayer detects the exoplayer module if it is part of the app and auto collects the exoplayer events.
        client.setPlayer(player);
        client.reportPlaybackRequested(contentInfo);
    }

    public static void initAdSession(AdsLoader adsLoader, ConvivaVideoAnalytics client, String url) {

         videoAnalytics = client;
        adsLoader.addAdsLoadedListener(adsLoadedListener);
        adsLoader.addAdErrorListener(imaAdsLoaderListener);
        adAnalytics = ConvivaAnalytics.buildAdAnalytics(context, videoAnalytics);



    }

    private static AdsLoader.AdsLoadedListener adsLoadedListener = new AdsLoader.AdsLoadedListener() {
        @Override
        public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
            Log.d(TAG, "onAdsManagerLoaded: ");
            imaAdsLoaderListener = new ConvivaHelper.ConvivaImaAdsManagerListener(videoAnalytics, adsManagerLoadedEvent.getAdsManager());
            adsManagerLoadedEvent.getAdsManager().addAdErrorListener(imaAdsLoaderListener);
            adsManagerLoadedEvent.getAdsManager().addAdEventListener(imaAdsLoaderListener);
        }
    };

    public static void releaseAdSession(AdsLoader adsLoader, AdsManager adsManager){
        adsLoader.removeAdsLoadedListener(adsLoadedListener);
        adsManager.removeAdErrorListener(imaAdsLoaderListener);
        adsManager.removeAdEventListener(imaAdsLoaderListener);
        adAnalytics.release();
        adsLoader = null;


    }

    public static void onContentPlaybackEnded(ConvivaVideoAnalytics client){
        client.reportPlaybackEnded();
    }
    public static void release(ConvivaVideoAnalytics client, Object player){
        client.release();
    }

    public static Map<String, Object> getConvivaAdMetadata(Ad ad) {
        AdPodInfo podInfo = ad.getAdPodInfo();
        String podPosition = podInfo.getPodIndex() == 0 ? ConvivaSdkConstants.AdPosition.PREROLL.toString() :
                            (podInfo.getPodIndex() == -1 ? ConvivaSdkConstants.AdPosition.POSTROLL.toString() :
                                    ConvivaSdkConstants.AdPosition.MIDROLL.toString());
        Map<String, Object> tags = new HashMap<>();
        tags.put("c3.ad.technology", "Client Side");
        tags.put("c3.ad.id", ad.getAdId());
        tags.put("c3.ad.system", ad.getAdSystem());
        tags.put("c3.ad.advertiser", ad.getAdvertiserName());
        tags.put("c3.ad.creativeId", ad.getCreativeId());
        tags.put("c3.ad.description", ad.getDescription());
        tags.put("c3.ad.sequence", Integer.toString(ad.getAdPodInfo().getAdPosition()));
        tags.put("c3.ad.position", podPosition);
        tags.put("c3.ad.mediaApiFramework", "NA");
        tags.put("c3.ad.adManagerName", "Google IMA SDK");
        tags.put("c3.ad.adManagerVersion", "3.11.2");

        // First wrapper info
        String firstAdSystem;
        String firstAdId;
        String firstCreativeId;
        if (ad.getAdWrapperIds().length != 0) {
            int len = ad.getAdWrapperIds().length;
            firstAdSystem = ad.getAdWrapperSystems()[len - 1];
            firstAdId = ad.getAdWrapperIds()[len - 1];
            firstCreativeId = ad.getAdWrapperCreativeIds()[len - 1];
        } else {
            firstAdSystem = ad.getAdSystem();
            firstAdId = ad.getAdId();
            firstCreativeId = ad.getCreativeId();
        }
        tags.put("c3.ad.firstAdSystem", firstAdSystem);
        tags.put("c3.ad.firstAdId", firstAdId);
        tags.put("c3.ad.firstCreativeId", firstCreativeId);

        tags.put(ConvivaSdkConstants.ASSET_NAME, ad.getTitle());
        tags.put(ConvivaSdkConstants.STREAM_URL, "adtag_url");
        tags.put(ConvivaSdkConstants.IS_LIVE, "false");
        tags.put(ConvivaSdkConstants.VIEWER_ID, "viewer_id");
        tags.put(ConvivaSdkConstants.PLAYER_NAME, "app name");
        tags.put(ConvivaSdkConstants.ENCODED_FRAMERATE, "30");
        tags.put(ConvivaSdkConstants.DURATION, String.valueOf(ad.getDuration()));


        return tags;
    }

    static class ConvivaImaAdsManagerListener implements AdErrorEvent.AdErrorListener,
            AdEvent.AdEventListener {

        private ConvivaVideoAnalytics videoAnalytics;
        String mPodBreakPostion;
        int mPodIndex;
        AdsManager mAdsManager;

        public ConvivaImaAdsManagerListener(ConvivaVideoAnalytics videoAnalytics, AdsManager adsManager){
            this.videoAnalytics = videoAnalytics;
            mAdsManager = adsManager;

        }
        @Override
        public void onAdEvent(AdEvent adEvent) {
            Ad ad = adEvent.getAd();
            Log.d(TAG, "onAdEvent: "+adEvent.getType());
            switch (adEvent.getType()) {
                case LOADED:
                    Map<String, Object> metadata = ConvivaHelper.getConvivaAdMetadata(ad);
                    Map<String, Object> playerinfo = new HashMap<>();
                    playerinfo.put(ConvivaSdkConstants.FRAMEWORK_NAME, "Google IMA SDK");
                    playerinfo.put(ConvivaSdkConstants.FRAMEWORK_VERSION, "3.11.2");

                    adAnalytics.reportAdLoaded(metadata);
                    adAnalytics.setAdPlayerInfo(playerinfo);

                    break;
                case STARTED:
                    Map<String, Object> metadata1 = ConvivaHelper.getConvivaAdMetadata(ad);
                    adAnalytics.reportAdStarted(metadata1);
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.PLAYER_STATE,PlayerStateManager.PlayerState.PLAYING);
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.BITRATE, ad.getVastMediaBitrate());
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.RESOLUTION, ad.getVastMediaWidth(), ad.getVastMediaHeight());
                    Log.d(TAG, "onAdEvent: STARTED");
                    break;
                case SKIPPED:
                   // ConvivaSessionManager.this.cleanupAdSession();
                    adAnalytics.reportAdSkipped();
                    break;
                case AD_PROGRESS:
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.BITRATE, ad.getVastMediaBitrate());
                    break;
                case PAUSED:
                   // ConvivaSessionManager.this.setAdPlayerState(PlayerStateManager.PlayerState.PAUSED);
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.PLAYER_STATE, PlayerStateManager.PlayerState.PAUSED);
                    break;
                case AD_BUFFERING:
                    //ConvivaSessionManager.this.setAdPlayerState(PlayerStateManager.PlayerState.BUFFERING);
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.PLAYER_STATE,PlayerStateManager.PlayerState.BUFFERING);
                    break;
                case RESUMED:
                    //ConvivaSessionManager.this.setAdPlayerState(PlayerStateManager.PlayerState.PLAYING);
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.PLAYER_STATE,PlayerStateManager.PlayerState.PLAYING);
                    adAnalytics.reportAdMetric(ConvivaSdkConstants.PLAYBACK.BITRATE, ad.getVastMediaBitrate());
                    break;
                case ALL_ADS_COMPLETED:
                    //ConvivaSessionManager.this.mPodIndex = 0;
                    break;
                case COMPLETED:
                    //ConvivaSessionManager.this.cleanupAdSession();
                    adAnalytics.reportAdEnded();
                    break;
                case CONTENT_PAUSE_REQUESTED:
                    Log.d(TAG, "onAdEvent: CONTENT_PAUSE_REQUESTED");
                    AdPodInfo podInfo = ad.getAdPodInfo();
                    mPodBreakPostion = podInfo.getPodIndex() == 0 ? "PREROLL" : (podInfo.getPodIndex() == -1 ? "POSTROLL": "MIDROLL");
                    mPodIndex++;
                    Map<String, Object> podStartAttributes = new HashMap <>();
                    podStartAttributes.put("podDuration", String.valueOf(podInfo.getMaxDuration()));
                    podStartAttributes.put("podPosition", mPodBreakPostion);
                    podStartAttributes.put("podIndex", String.valueOf(mPodIndex));
                    videoAnalytics.reportAdBreakStarted(ConvivaSdkConstants.AdPlayer.CONTENT, ConvivaSdkConstants.AdType.CLIENT_SIDE, podStartAttributes);
                    break;
                case CONTENT_RESUME_REQUESTED:
                    videoAnalytics.reportAdBreakEnded();
                    break;
                case LOG:
                    Log.i(PLAYER, "Start Warning message from LOG ");
                    Map<String, String> adData = adEvent.getAdData();
                    String adErrorType = adData.get("type");
                    String errorMessage = "";
                    for (Map.Entry<String, String> entry : adData.entrySet()) {
                        errorMessage += entry.getKey() + ": " + entry.getValue() + "; ";
                    }
                    if (adErrorType.equals("adPlayError") /*&& ConvivaSessionManager.this.mAdSessionKey != -1*/) {
                        //ConvivaSessionManager.this.reportAdError(errorMessage, true);
                        //ConvivaSessionManager.this.cleanupAdSession();
                    } else {
                        Map<String, Object> errorAdMetadata = null;
                        if (ad != null) {
                            errorAdMetadata = getConvivaAdMetadata(ad);
                        } else {
                            // no ad object available so no ad metadata
                           // errorAdMetadata = new ContentMetadata();
                            //errorAdMetadata.assetName = "Ad Failed";
                        }
                        handleAdError(errorAdMetadata, errorMessage);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAdError(AdErrorEvent adErrorEvent) {
            Log.d(TAG, "onAdError: ");
            AdError error = adErrorEvent.getError();
            AdError.AdErrorType type = error.getErrorType();
            AdError.AdErrorCode code = error.getErrorCode();
            String errorMessage = "Code: " + code.toString() + "; Type: " + type.toString() + "; Message: " + error.getMessage();
            if (type == AdError.AdErrorType.PLAY /*&& ConvivaSessionManager.this.mAdSessionKey != -1*/) {
                //ConvivaSessionManager.this.reportAdError(errorMessage, true);
                //ConvivaSessionManager.this.cleanupAdSession();
                adAnalytics.reportAdError(errorMessage, ConvivaSdkConstants.ErrorSeverity.FATAL);
                adAnalytics.reportAdEnded();
            } else {
                // no ad object available so no ad metadata
                Map<String, Object> errorAdMetadata = new HashMap<>();
                errorAdMetadata.put(ConvivaSdkConstants.ASSET_NAME ,"Ad Failed");
                handleAdError(errorAdMetadata, errorMessage);
            }
        }



        private void handleAdError(Map<String, Object> errorAdMetadata, String errorMessage) {
            /*if (ConvivaSessionManager.this.mClient != null) {
                try {
                    int errorAdSessionKey = mClient.createAdSession(ConvivaSessionManager.this.mSessionKey, errorAdMetadata);
                    ConvivaSessionManager.this.mClient.reportPlaybackError(errorAdSessionKey, errorMessage, Client.ErrorSeverity.FATAL);
                    ConvivaSessionManager.this.mClient.cleanupSession(errorAdSessionKey);
                } catch (ConvivaException e) {
                    e.printStackTrace();
                }
            }*/

            adAnalytics.reportAdError(errorMessage, ConvivaSdkConstants.ErrorSeverity.FATAL);
            adAnalytics.reportAdEnded();
        }
    }

}
