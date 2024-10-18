package com.onourem.android.activity.ui.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.onourem.android.activity.models.NotificationInfoList;

import java.util.List;

public class NotificationData implements Parcelable {

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };
    private boolean isReadyToMove = true;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private List<Integer> notificationIdList;
    private List<NotificationInfoList> list;

    public NotificationData() {

    }

    public NotificationData(boolean isReadyToMove, boolean isLastPage, boolean isLoading, List<Integer> notificationIdList, List<NotificationInfoList> list) {
        this.isReadyToMove = isReadyToMove;
        this.isLastPage = isLastPage;
        this.isLoading = isLoading;
        this.notificationIdList = notificationIdList;
        this.list = list;
    }

    protected NotificationData(Parcel in) {
        isReadyToMove = in.readByte() != 0;
        isLastPage = in.readByte() != 0;
        isLoading = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isReadyToMove ? 1 : 0));
        dest.writeByte((byte) (isLastPage ? 1 : 0));
        dest.writeByte((byte) (isLoading ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isReadyToMove() {
        return isReadyToMove;
    }

    public void setReadyToMove(boolean readyToMove) {
        isReadyToMove = readyToMove;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public List<Integer> getNotificationIdList() {
        return notificationIdList;
    }

    public void setNotificationIdList(List<Integer> notificationIdList) {
        this.notificationIdList = notificationIdList;
    }

    public List<NotificationInfoList> getList() {
        return list;
    }

    public void setList(List<NotificationInfoList> list) {
        this.list = list;
    }
}
