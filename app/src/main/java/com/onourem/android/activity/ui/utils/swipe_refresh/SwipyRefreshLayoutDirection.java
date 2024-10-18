package com.onourem.android.activity.ui.utils.swipe_refresh;

public enum SwipyRefreshLayoutDirection {

    TOP(0),
    BOTTOM(1),
    BOTH(2);

    private final int mValue;

    SwipyRefreshLayoutDirection(int value) {
        this.mValue = value;
    }

    public static SwipyRefreshLayoutDirection getFromInt(int value) {
        for (SwipyRefreshLayoutDirection direction : SwipyRefreshLayoutDirection.values()) {
            if (direction.mValue == value) {
                return direction;
            }
        }
        return BOTH;
    }

}