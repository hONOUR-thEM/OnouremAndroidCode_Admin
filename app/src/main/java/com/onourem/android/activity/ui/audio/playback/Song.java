package com.onourem.android.activity.ui.audio.playback;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
    String audioId;
    String creatorId;
    String createdDate;
    String numberOfLike;
    String audioDuration;
    String audioUrl;
    String privacyId;
    String profilePictureUrl;
    String categoryName;
    String title;
    int trackNumber;
    int year;
    int duration;
    String albumName;
    int artistId;
    String artistName;
    boolean isPlaying;
    boolean isAudioPreparing;
    String isAudioLiked;
    String userName;
    String numberOfViews;
    String audioStatus;
    String userFollowingCreator;
    String extension;
    String userType;
    String commentCount;
    String audioRatings;
    Drawable drawable;


    public Song(String audioId, String creatorId, String createdDate, String numberOfLike, String audioDuration, String audioUrl, String privacyId, String profilePictureUrl, String categoryName, String title, int trackNumber, int year, int duration, String albumName, int artistId, String artistName, boolean isPlaying, boolean isAudioPreparing, String isAudioLiked, String userName, String numberOfViews, String audioStatus, String userFollowingCreator, String extension, String userType, String commentCount, String audioRatings) {
        this.audioId = audioId;
        this.creatorId = creatorId;
        this.createdDate = createdDate;
        this.numberOfLike = numberOfLike;
        this.audioDuration = audioDuration;
        this.audioUrl = audioUrl;
        this.privacyId = privacyId;
        this.profilePictureUrl = profilePictureUrl;
        this.categoryName = categoryName;
        this.title = title;
        this.trackNumber = trackNumber;
        this.year = year;
        this.duration = duration;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
        this.isPlaying = isPlaying;
        this.isAudioPreparing = isAudioPreparing;
        this.isAudioLiked = isAudioLiked;
        this.userName = userName;
        this.numberOfViews = numberOfViews;
        this.audioStatus = audioStatus;
        this.userFollowingCreator = userFollowingCreator;
        this.extension = extension;
        this.userType = userType;
        this.commentCount = commentCount;
        this.audioRatings = audioRatings;
    }

    public Song(String audioId, String creatorId, String createdDate, String numberOfLike, String audioDuration, String audioUrl, String privacyId, String profilePictureUrl, String categoryName, String title, int trackNumber, int year, int duration, String albumName, int artistId, String artistName, boolean isPlaying, boolean isAudioPreparing, String isAudioLiked, String userName, String numberOfViews, String audioStatus, String userFollowingCreator, String extension, String userType, String commentCount, Drawable drawable) {
        this.audioId = audioId;
        this.creatorId = creatorId;
        this.createdDate = createdDate;
        this.numberOfLike = numberOfLike;
        this.audioDuration = audioDuration;
        this.audioUrl = audioUrl;
        this.privacyId = privacyId;
        this.profilePictureUrl = profilePictureUrl;
        this.categoryName = categoryName;
        this.title = title;
        this.trackNumber = trackNumber;
        this.year = year;
        this.duration = duration;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
        this.isPlaying = isPlaying;
        this.isAudioPreparing = isAudioPreparing;
        this.isAudioLiked = isAudioLiked;
        this.userName = userName;
        this.numberOfViews = numberOfViews;
        this.audioStatus = audioStatus;
        this.userFollowingCreator = userFollowingCreator;
        this.extension = extension;
        this.userType = userType;
        this.commentCount = commentCount;
        this.drawable = drawable;
    }

    public Song() {
    }

    protected Song(Parcel in) {
        audioId = in.readString();
        creatorId = in.readString();
        createdDate = in.readString();
        numberOfLike = in.readString();
        audioDuration = in.readString();
        audioUrl = in.readString();
        privacyId = in.readString();
        profilePictureUrl = in.readString();
        categoryName = in.readString();
        title = in.readString();
        trackNumber = in.readInt();
        year = in.readInt();
        duration = in.readInt();
        albumName = in.readString();
        artistId = in.readInt();
        artistName = in.readString();
        isPlaying = in.readByte() != 0;
        isAudioPreparing = in.readByte() != 0;
        isAudioLiked = in.readString();
        userName = in.readString();
        numberOfViews = in.readString();
        audioStatus = in.readString();
        userFollowingCreator = in.readString();
        extension = in.readString();
        userType = in.readString();
        commentCount = in.readString();
        audioRatings = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(audioId);
        dest.writeString(creatorId);
        dest.writeString(createdDate);
        dest.writeString(numberOfLike);
        dest.writeString(audioDuration);
        dest.writeString(audioUrl);
        dest.writeString(privacyId);
        dest.writeString(profilePictureUrl);
        dest.writeString(categoryName);
        dest.writeString(title);
        dest.writeInt(trackNumber);
        dest.writeInt(year);
        dest.writeInt(duration);
        dest.writeString(albumName);
        dest.writeInt(artistId);
        dest.writeString(artistName);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeByte((byte) (isAudioPreparing ? 1 : 0));
        dest.writeString(isAudioLiked);
        dest.writeString(userName);
        dest.writeString(numberOfViews);
        dest.writeString(audioStatus);
        dest.writeString(userFollowingCreator);
        dest.writeString(extension);
        dest.writeString(userType);
        dest.writeString(commentCount);
        dest.writeString(audioRatings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getNumberOfLike() {
        return numberOfLike;
    }

    public void setNumberOfLike(String numberOfLike) {
        this.numberOfLike = numberOfLike;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getPrivacyId() {
        return privacyId;
    }

    public void setPrivacyId(String privacyId) {
        this.privacyId = privacyId;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isAudioPreparing() {
        return isAudioPreparing;
    }

    public void setAudioPreparing(boolean audioPreparing) {
        isAudioPreparing = audioPreparing;
    }

    public String getIsAudioLiked() {
        return isAudioLiked;
    }

    public void setIsAudioLiked(String isAudioLiked) {
        this.isAudioLiked = isAudioLiked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(String numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public String getAudioStatus() {
        return audioStatus;
    }

    public void setAudioStatus(String audioStatus) {
        this.audioStatus = audioStatus;
    }

    public String getUserFollowingCreator() {
        return userFollowingCreator;
    }

    public void setUserFollowingCreator(String userFollowingCreator) {
        this.userFollowingCreator = userFollowingCreator;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getAudioRatings() {
        return audioRatings;
    }

    public void setAudioRatings(String audioRatings) {
        this.audioRatings = audioRatings;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
