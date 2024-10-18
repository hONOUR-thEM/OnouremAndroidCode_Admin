package com.onourem.android.activity.ui.utils;

public interface Constants {

    String KEY_AUTH_TOKEN = "key_auth_token";
    String KEY_FCM_TOKEN = "key_fcm_token";
    String KEY_IS_LOGGED_IN = "key_is_logged_in";
    String KEY_LOGGED_IN_USER_ID = "key_logged_in_user_id";
    String KEY_IS_LOGGED_IN_AS_GUEST = "key_is_logged_in_as_guest";
    String KEY_USER_OBJECT = "key_user_object";
    String KEY_USER_EMAIL = "key_user_email";
    String KEY_NEW_USER = "key_new_user";

    String KEY_IS_LINK_VERIFIED = "key_is_link_verified";
    String KEY_LINK_USER_ID = "key_link_user_id";
    String KEY_LINK_TYPE = "key_link_type";
    String KEY_INTRO_VIDEO_ENGLISH = "KEY_INTRO_VIDEO_ENGLISH";
    String KEY_INTRO_VIDEO_HINDI = "KEY_INTRO_VIDEO_HINDI";
    String KEY_SELECTED_FILTER_INDEX = "key_selected_filter_index";
    String KEY_CAN_APP_INSTALLED_DIRECTLY = "key_can_app_installed_directly";
    String KEY_SELECTED_EXPRESSION = "key_selected_expression";
    String KEY_EXPRESSIONS_RESPONSE = "key_expressions_response";
    String KEY_SELECTED_EXPRESSION_MESSAGE = "key_selected_expression_message";
    String KEY_ANDROID_ADVICE_UPGRADE_VERSION_SKIPPED = "KEY_ANDROID_ADVICE_UPGRADE_VERSION_SKIPPED";

    String KEY_DYNAMIC_LINK = "key_dynamic_link";
    String KEY_TIME_MILIES_LAST_MOOD_SYNC = "key_time_stamp";
    String KEY_SELECTED_SURVEY = "key_selected_survey";
    String KEY_BOTTOM_SHEET_ACTIONS = "KEY_BOTTOM_SHEET_ACTIONS";
    String KEY_GAME_POINTS = "KEY_GAME_POINTS";
    String KEY_VIDEO_DURATION = "KEY_VIDEO_DURATION";
    String KEY_APP_VERSION = "KEY_APP_VERSION";
    String KEY_DEVICE_NAME = "KEY_DEVICE_NAME";
    String KEY_DEVICE_MODEL = "KEY_DEVICE_MODEL";
    String KEY_OS_VERSION = "KEY_OS_VERSION";
    String KEY_SELECTED_FILTER_INT = "KEY_SELECTED_FILTER_INT";
    String KEY_SELECTED_FILTER_INT_WHILE_PLAYING = "KEY_SELECTED_FILTER_INT_WHILE_PLAYING";
    String KEY_AUDIO_LIMIT = "KEY_AUDIO_LIMIT";
    String KEY_AUDIO_VIEW = "KEY_AUDIO_VIEW";
    String KEY_SP_LAST_INTERACTION_TIME = "KEY_SP_LAST_INTERACTION_TIME";
    String KEY_GET_APP_UPDATE_RESPONSE = "KEY_GET_APP_UPDATE_RESPONSE";

    String KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY = "KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY";
    String KEY_TOTAL_QUES_ANSWERED_TODAY = "KEY_TOTAL_QUES_ANSWERED_TODAY";
    String KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN = "KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN";
    String KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED = "KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED";
    String KEY_HAS_USER_REVIEWED_ALREADY = "KEY_HAS_USER_REVIEWED_ALREADY";
    int CLICK_WHOLE_ITEM = 1;
    int CLICK_LIKE = 2;
    int CLICK_MORE = 3;
    int CLICK_PROFILE = 4;
    int CLICK_COMMENT = 5;
    int CLICK_COMMENT_LIST = 6;
    int COMMENT_MAX_LENGTH = 250;
    float DIM_AMOUNT = 0.30f;
    float PROGRESS_DIM_AMOUNT = 0.50f;

    double MAX_VIDEO_SIZE_TO_COMPRESS = 2048.0 * 5;  //Mb

    String TRENDING_VOCALS = "getAudioInfo";
    String FRIENDS_VOCALS = "getFriendAudioInfo";
    String MY_VOCALS = "getLoginUserAudioInfo";
    String FAVORITE_VOCALS = "getAudioInfoLikedByLoginUser";
    String OTHERS_VOCALS = "getAudioForOtherUser";
    String GUEST_VOCALS = "getAudioInfoForGuestUser";
    String VOCALS = "vocals";
    String ADMIN_VOCALS = "vocals";

    int CLICK_APPROVE = 5;
    int CLICK_REJECT = 6;
    int CLICK_SCHEDULE = 7;
    int CLICK_RE_SCHEDULE = 8;
    String WATCHLIST = "watchlist";
    String WATCHLIST_POSITION_INDEX = "WATCHLIST_POSITION_INDEX";

    String CHANGED_MOOD = "CHANGED_MOOD";

    String APP_START_TIME = "APP_START_TIME";
    String APP_END_TIME = "APP_END_TIME";
    String READ = "0";
    String UN_READ = "1";
    String SELECTED_INDEX_ADMIN_MENU = "SELECTED_INDEX_ADMIN_MENU";
    String JSON_1 = "JSON_1";
    String JSON_2 = "JSON_2";
    String JSON_3 = "JSON_3";


    String ACTIVITY_EXTERNAL = "External";
    String ACTIVITY_MOOD = "UserMoodCard";
    String ACTIVITY_ONE_TO_MANY = "1toM";
    String ACTIVITY_POST = "Post";
    String ACTIVITY_WATCHLIST = "Watchlist";
    String ACTIVITY_AUDIO = "Audio";
    String ACTIVITY_TASK = "Task";
    String ACTIVITY_MESSAGE = "Message";
    String ACTIVITY_CARD = "Card";
    String ACTIVITY_SURVEY = "Survey";
    String ACTIVITY_ASK_TO_FRIENDS = "ASKTOFRIENDS";
    String ACTIVITY_FRIEND_CIRCLE_GAME = "FriendCircleGame";
    String LOADER = "loader";
    String FOOTER = "footer";

    String KEY_RECYCLERVIEW_LAYOUT_MANAGER_STATE = "stateLayout";

}
