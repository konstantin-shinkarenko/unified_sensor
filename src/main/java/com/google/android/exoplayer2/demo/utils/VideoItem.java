package com.google.android.exoplayer2.demo.utils;

/**
 * Information about a video playlist item that the user will select in a playlist.
 */
public final class VideoItem {

    private final int mThumbnailResourceId;
    private final String mTitle;

    public VideoItem(String title, int thumbnailResourceId) {
        super();
        mThumbnailResourceId = thumbnailResourceId;
        mTitle = title;
    }

    /**
     * Returns the video thumbnail image resource.
     */
    public int getImageResource() {
        return mThumbnailResourceId;
    }

    /**
     * Returns the title of the video item.
     */
    public String getTitle() {
        return mTitle;
    }

}
