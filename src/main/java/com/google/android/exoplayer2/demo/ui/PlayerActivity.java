/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.demo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conviva.api.ContentMetadata;
import com.conviva.api.player.PlayerStateManager;
import com.conviva.sdk.ConvivaAdAnalytics;
import com.conviva.sdk.ConvivaAnalytics;
import com.conviva.sdk.ConvivaSdkConstants;
import com.conviva.sdk.ConvivaVideoAnalytics;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.demo.helper.ConvivaHelper;
import com.google.android.exoplayer2.demo.utils.DemoApplication;
import com.google.android.exoplayer2.demo.R;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity that plays media using {@link SimpleExoPlayer}.
 */
public class PlayerActivity extends Activity
        implements OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener, AdEvent.AdEventListener {
    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";

    public static final String ACTION_VIEW_LIST =
            "com.google.android.exoplayer.demo.action.VIEW_LIST";
    public static final String URI_LIST_EXTRA = "uri_list";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";

    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    public static final String AD_TAG_URI = "ad_tag";

    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
    private static final String ABR_ALGORITHM_DEFAULT = "default";

    public static final String SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode";

    // Saved instance state keys.
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private  DefaultBandwidthMeter BANDWIDTH_METER;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    private PlayerView playerView;
    private LinearLayout debugRootView;
    private TextView debugTextView;

    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private DebugTextViewHelper debugViewHelper;
    private TrackGroupArray lastSeenTrackGroupArray;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    // Fields used only for ad playback. The ads loader is loaded via reflection.

    private AdsLoader adsLoader;

    //IMA integration
    private ImaSdkFactory mSdkFactory;
    private AdsLoader mAdsLoader;
    private AdsManager mAdsManager;
    private ViewGroup mAdUiContainer;
    private Button mAd_Playpause;
    private boolean isAdpause = false;
    private boolean isAllAdscompleted = false;
    //private ConvivaSessionManager mConvivaSessionManager = null;
    private ConvivaVideoAnalytics mConvivaVideoAnalytics;
    private ConvivaAdAnalytics mConvivaAdAnalytics;
    // Activity lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String sphericalStereoMode = getIntent().getStringExtra(SPHERICAL_STEREO_MODE_EXTRA);
        if (sphericalStereoMode != null) {
            setTheme(R.style.PlayerTheme_Spherical);
        }
        super.onCreate(savedInstanceState);
        mediaDataSourceFactory = buildDataSourceFactory(true);

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        setContentView(R.layout.player_activity);
        View rootView = findViewById(R.id.root);

        rootView.setOnClickListener(this);
        debugRootView = findViewById(R.id.controls_root);
        debugTextView = findViewById(R.id.debug_text_view);

        playerView = findViewById(R.id.player_view);
        mAd_Playpause = findViewById(R.id.ad_pause);
        mAdUiContainer = playerView;
        playerView.setControllerVisibilityListener(this);
        playerView.requestFocus();


        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        //mConvivaSessionManager = ConvivaSessionManager.getInstance();
        mConvivaVideoAnalytics = ConvivaHelper.getInstance(getApplicationContext());

        mSdkFactory = ImaSdkFactory.getInstance();
        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(mAdUiContainer);
        mAdsLoader = mSdkFactory.createAdsLoader(getApplicationContext(), mSdkFactory.createImaSdkSettings(), adDisplayContainer);

        //mAdsLoader.addAdErrorListener(this);
        final AdsRenderingSettings ars = mSdkFactory.createAdsRenderingSettings();
        mAdsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                Log.d("PlayerActivity", "onAdsManagerLoaded: ");
                mAdsManager = adsManagerLoadedEvent.getAdsManager();
                // Attach event and error event listeners.
                //mAdsManager.addAdErrorListener(PlayerActivity.this);
                mAdsManager.addAdEventListener(PlayerActivity.this);
                // mAdsManager.init();
                // For enabling preload setting.
                mAdsManager.init(ars);
            }
        });

        mAd_Playpause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdsManager != null) {
                    if (!isAdpause) {
                        mAdsManager.pause();
                        mAd_Playpause.setText(R.string.play);
                        isAdpause = true;
                    } else {
                        mAdsManager.resume();
                        mAd_Playpause.setText(R.string.pause);
                        isAdpause = false;
                    }
                }
            }
        });

        String ad_url = getIntent().getStringExtra(PlayerActivity.AD_TAG_URI);
        requestAds(ad_url);
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        clearStartPosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAdsLoader();
        /*if(mConvivaSessionManager != null) {
            mConvivaSessionManager.cleanupConvivaSession();
        }*/

        ConvivaHelper.release(mConvivaVideoAnalytics, player);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    // Activity input

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        if (view.getParent() == debugRootView) {
            MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                CharSequence title = ((Button) view).getText();
                int rendererIndex = (int) view.getTag();
                int rendererType = mappedTrackInfo.getRendererType(rendererIndex);
                boolean allowAdaptiveSelections =
                        rendererType == C.TRACK_TYPE_VIDEO
                                || (rendererType == C.TRACK_TYPE_AUDIO
                                && mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                                == MappedTrackInfo.RENDERER_SUPPORT_NO_TRACKS);
                Pair<AlertDialog, TrackSelectionView> dialogPair =
                        TrackSelectionView.getDialog(this, title, trackSelector, rendererIndex);
                dialogPair.second.setShowDisableOption(true);
                dialogPair.second.setAllowAdaptiveSelections(allowAdaptiveSelections);
                dialogPair.first.show();
            }
        }
    }

    @Override
    public void preparePlayback() {
        initializePlayer();
    }

    @Override
    public void onVisibilityChange(int visibility) {
        debugRootView.setVisibility(visibility);
    }

    private void initializePlayer() {
        if (player == null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            Uri[] uris;
            String[] extensions;
            if (ACTION_VIEW.equals(action)) {
                uris = new Uri[]{intent.getData()};
                extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};
            } else if (ACTION_VIEW_LIST.equals(action)) {
                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
                uris = new Uri[uriStrings.length];
                for (int i = 0; i < uriStrings.length; i++) {
                    uris[i] = Uri.parse(uriStrings[i]);
                }
                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
                if (extensions == null) {
                    extensions = new String[uriStrings.length];
                }
            } else {
                finish();
                return;
            }
            if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {
                return;
            }

            DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

            TrackSelection.Factory trackSelectionFactory = null;
            String abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_EXTRA);
            if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
                trackSelectionFactory = new AdaptiveTrackSelection.Factory();
            }

            boolean preferExtensionDecoders =
                    intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                    ((DemoApplication) getApplication()).useExtensionRenderers()
                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory =
                    new DefaultRenderersFactory(this, extensionRendererMode);

            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);
            lastSeenTrackGroupArray = null;

            player =
                    ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector, drmSessionManager);

            //Conviva-Integration
            ContentMetadata metadata = new ContentMetadata();
            metadata.assetName = "GoogleIma_maincontent";
            /*if(mConvivaSessionManager == null){
                mConvivaSessionManager = ConvivaSessionManager.getInstance();
            }
            mConvivaSessionManager.createConvivaSession(metadata);*/

            //mConvivaVideoAnalytics = ConvivaHelper.init(getApplicationContext());
            Map<String, Object> info = new HashMap<>();
            info.put(ConvivaSdkConstants.ASSET_NAME, "GoogleIma_maincontent");
            //mConvivaVideoAnalytics.reportAdLoaded(info);
            ConvivaHelper.initAdSession(mAdsLoader, mConvivaVideoAnalytics, getIntent().getStringExtra(PlayerActivity.AD_TAG_URI));
            ConvivaHelper.onContentPlaybackStart(mConvivaVideoAnalytics, player, getIntent(), "GoogleIma_maincontent");

            /*try {
                mPlayerInterface = new CVExoPlayerInterface(mConvivaSessionManager.getPlayerStateManager(), player);
            } catch (Exception e) {
                Log.d("PlayerActivity", "DemmoPlayerAnalyticsInterface instance failed");
            }*/

            player.addListener(new PlayerEventListener());
            player.setPlayWhenReady(startAutoPlay);
            player.addAnalyticsListener(new EventLogger(trackSelector));
            playerView.setPlayer(player);
            playerView.setPlaybackPreparer(this);
            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugViewHelper.start();

            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
            }
            mediaSource =
                    mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);

        }
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        player.prepare(mediaSource, !haveStartPosition, false);
        updateButtonVisibilities();
    }

    private void requestAds(String adTagUrl) {



        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setContentProgressProvider(new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if(player != null && player.getContentPosition() >= 0) {
                    return new VideoProgressUpdate(player.getContentPosition(),
                            player.getContentPosition());
                }else{
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
            }
        });

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);
    }

    @SuppressWarnings("unchecked")
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(mediaDataSourceFactory)
                    .setManifestParser(
                            new FilteringManifestParser<>(new DashManifestParser(), getOfflineStreamKeys(uri)))
                    .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .setManifestParser(
                                new FilteringManifestParser<>(
                                        new SsManifestParser(), getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setPlaylistParserFactory(
                                new DefaultHlsPlaylistParserFactory(getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private List<StreamKey> getOfflineStreamKeys(Uri uri) {
        return ((DemoApplication) getApplication()).getDownloadTracker().getOfflineStreamKeys(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            ConvivaHelper.release(mConvivaVideoAnalytics, player);
            updateTrackSelectorParameters();
            updateStartPosition();
            debugViewHelper.stop();
            debugViewHelper = null;
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
        /*if(mConvivaSessionManager != null){
            mConvivaSessionManager.cleanupConvivaSession();
            mConvivaSessionManager = null;
        }*/

    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((DemoApplication) getApplication())
                .buildDataSourceFactory();
    }

    private void releaseAdsLoader() {
        if (mAdsLoader != null) {
            Log.d(PlayerActivity.class.getName(), "releaseAdsLoader: ");
            mAdsManager.destroy();
            //mAdsLoader.removeAdErrorListener(this);
            mAdsManager.removeAdEventListener(this);
            //mAdsManager.removeAdErrorListener(this);
            ConvivaHelper.releaseAdSession(mAdsLoader, mAdsManager);
            mAdsLoader = null;
            //mConvivaAdAnalytics.release();
            playerView.getOverlayFrameLayout().removeAllViews();
        }
    }

    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();
        if (player == null) {
            return;
        }

        MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }

        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                Button button = new Button(this);
                int label;
                switch (player.getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:
                        label = R.string.exo_track_selection_title_audio;
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        label = R.string.exo_track_selection_title_video;
                        break;
                    case C.TRACK_TYPE_TEXT:
                        label = R.string.exo_track_selection_title_text;
                        break;
                    default:
                        continue;
                }
                button.setText(label);
                button.setTag(i);
                button.setOnClickListener(this);
                debugRootView.addView(button);
            }
        }
    }

    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        Log.d(PlayerActivity.class.getName(), "onBackPressed: ");
        releaseAdsLoader();
        releasePlayer();
        super.onBackPressed();
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                showControls();
                ConvivaHelper.onContentPlaybackEnded(mConvivaVideoAnalytics);
                if (mAdsLoader != null) {
                    mAdsLoader.contentComplete();
                }
                if(isAllAdscompleted){
                    isAllAdscompleted = false;
                }
            }
            updateButtonVisibilities();
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (player.getPlaybackError() != null) {
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer();
            } else {
                updateStartPosition();
                updateButtonVisibilities();
                showControls();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            updateButtonVisibilities();
            if (trackGroups != lastSeenTrackGroupArray) {
                MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video);
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio);
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {

        switch (adEvent.getType()) {
            case LOADED:
                mAdsManager.start();
                break;

            case STARTED:
                break;

            case SKIPPED:
                if (player != null) {
                    player.setPlayWhenReady(true);
                    // CONTENT_RESUME_REQUESTED is called after SKIPPED
                    // attachContentPlayer is called again.
                    // This removes log warning:
                    //"Monitor.attachPlayer(): detach current PlayerStateManager first"
                    //mConvivaSessionManager.attachContentPlayer();
                }
                break;

            case PAUSED:
                break;
            case RESUMED:
                break;

            case CONTENT_PAUSE_REQUESTED:
                if (player != null) {
                    Log.d("PlayerActivity", "onAdEvent: CONTENT_PAUSE_REQUESTED");
                    player.setPlayWhenReady(false);
                }
                break;
            case CONTENT_RESUME_REQUESTED:
                if (player != null) {
                    player.setPlayWhenReady(true);
                }
                break;
            case ALL_ADS_COMPLETED:
                isAllAdscompleted = true;
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    //mAdsManager = null;
                }
                break;
            case COMPLETED:
                if (adEvent.getAd().getAdPodInfo().getPodIndex() == -1) {
                }
                break;
            default:
                break;
        }
    }

}
