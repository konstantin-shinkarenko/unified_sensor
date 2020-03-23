package com.google.android.exoplayer2.demo.utils;

import com.google.android.exoplayer2.demo.R;

import java.util.Arrays;
import java.util.List;

/**
 * An enumeration of video metadata.
 */
public enum VideoMetadata {

    PRE_ROLL_NO_SKIP(
            "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8",
            "Pre-roll, Single Inline Linear - not skippable",
            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/" +
                    "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast" +
                    "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct" +
                    "%3Dlinear&correlator=",
            R.drawable.thumbnail1,
            false),
    PRE_ROLL_SKIP(
            "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
            "Pre-roll, Single Skippable Inline",
            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/" +
                    "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast" +
                    "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct" +
                    "%3Dskippablelinear&correlator=",
            R.drawable.thumbnail1,
            false),

    Single_Redirect_Linear(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, Single Redirect Linear",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/"+
        "single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"+
        "%3Dredirectlinear&correlator=",
        R.drawable.thumbnail1,
        false),

    Single_Redirect_Error(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, Single Redirect Error",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"+
        "%3Dredirecterror&nofb=1&correlator=",
        R.drawable.thumbnail1,
        false),

    Single_Redirect_Broken_Fallback(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, Single Redirect Broken (Fallback)",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"+
        "%3Dredirecterror&correlator=",
        R.drawable.thumbnail1,
        false),

    Single_VPAID_2_0_Linear(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, Single VPAID_2_0_Linear",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"+
        "%3Dlinearvpaid2js&correlator=",
        R.drawable.thumbnail1,
        false),

    Single_VPAID_2_0_Non_Linear(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, Single VPAID 2.0 Non-Linear",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"+
        "%3Dnonlinearvpaid2js&correlator=",
        R.drawable.thumbnail1,
            false),

    Single_Non_linear_Inline(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, Single Nonlinear Inline",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=480x70&iu=/124319096/external"+
        "/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct"+
        "%3Dnonlinear&correlator=",
        R.drawable.thumbnail1,
        false),


    VMAP_Pre_roll(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, VMAP Pre-roll",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpreonly&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),


    VMAP_Pre_roll_Bumper(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Pre-roll, VMAP Pre-roll + Bumper",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpreonlybumper&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Post_roll(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Post-roll, VMAP Post-roll",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpostonly&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Post_roll_Bumper(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "Post-roll, VMAP Post-roll + Bumper",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpostonlybumper&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Pre_Mid_Post_Single_Ads(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "VMAP Pre-, Mid-, and Post-rolls, Single Ads",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Pre_Single_Mid_Standard_Pod_3_ads_Post_Single_Ad(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "VMAP - Pre-roll Single Ad, Mid-roll Standard Pod with 3 ads, Post-roll Single Ad",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Pre_Single_Ad_Mid_Optimized_Pod_3_Ads_Post_Single_Ad(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "VMAP - Pre-roll Single Ad, Mid-roll Optimized Pod with 3 Ads, Post-roll Single Ad",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpremidpostoptimizedpod&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Pre_Single_Mid_Standard_Pod_3_ads_Post_Single_Ad_with_bumpers(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "VMAP - Pre-roll Single Ad, Mid-roll Standard Pod with 3 ads, Post-roll Single Ad With Bumpers",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Pre_Single_Ad_Mid_Optimized_Pod_3_Ads_Post_Single_Ad_with_bumpers(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "VMAP - Pre-roll Single Ad, Mid-roll Optimized Pod with 3 Ads, Post-roll Single Ad with Bumpers",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpremidpostoptimizedpodbumper&cmsid=496&vid=short_onecue&correlator=",
        R.drawable.thumbnail1,
        true),

    VMAP_Pre_Single_Ad_Mid_Standard_Pods_5_Ads_Every_10_Seconds_Post_Single_Ad(
        "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8",
        "VMAP- Pre-roll Single Ad, Mid-roll Standard Pods with 5 Ads Every 10 Seconds for 1:40, Post-roll Single Ad",
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external"+
        "/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap"+
        "&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar"+
        "%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator=",
        R.drawable.thumbnail1,
        true);

    public static final List<VideoMetadata> APP_VIDEOS =
        Arrays.asList(PRE_ROLL_NO_SKIP, PRE_ROLL_SKIP,
                    Single_Redirect_Linear,
                    Single_Redirect_Error,
                    Single_Redirect_Broken_Fallback,
                    Single_VPAID_2_0_Linear,
                    Single_VPAID_2_0_Non_Linear,
                    Single_Non_linear_Inline,
                    VMAP_Pre_roll,
                    VMAP_Pre_roll_Bumper,
                    VMAP_Post_roll,
                    VMAP_Post_roll_Bumper,
                    VMAP_Pre_Mid_Post_Single_Ads,
                    VMAP_Pre_Single_Mid_Standard_Pod_3_ads_Post_Single_Ad,
                    VMAP_Pre_Single_Ad_Mid_Optimized_Pod_3_Ads_Post_Single_Ad,
                    VMAP_Pre_Single_Mid_Standard_Pod_3_ads_Post_Single_Ad_with_bumpers,
                    VMAP_Pre_Single_Ad_Mid_Optimized_Pod_3_Ads_Post_Single_Ad_with_bumpers,
                    VMAP_Pre_Single_Ad_Mid_Standard_Pods_5_Ads_Every_10_Seconds_Post_Single_Ad);

    /**
     * The thumbnail image for the video.
     **/
    public final int thumbnail;

    /**
     * The title of the video.
     **/
    public final String title;

    /**
     * The URL for the video.
     **/
    public final String videoUrl;

    /**
     * The ad tag for the video
     **/
    public final String adTagUrl;

    /**
     * If the ad is VMAP.
     **/
    public final boolean isVmap;

    private VideoMetadata(String videoUrl, String title, String adTagUrl, int thumbnail,
                          boolean isVmap) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.adTagUrl = adTagUrl;
        this.thumbnail = thumbnail;
        this.isVmap = isVmap;
    }
}
