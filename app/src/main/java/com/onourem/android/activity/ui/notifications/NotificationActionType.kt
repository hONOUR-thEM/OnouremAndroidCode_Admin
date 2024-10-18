package com.onourem.android.activity.ui.notifications

interface NotificationActionType {
    companion object {
        const val FRIEND_REQUEST = "FriendRequest"
        const val WATCHLIST_REQUEST = "WatchRequest"
        const val WATCH_REQUEST_ACCEPTED = "WatchRequestAccepted"
        const val FRIEND_SUGGESTION_REQUEST = "FriendSuggestionRequest"
        const val FRIEND_REQUEST_ACCEPTED = "FriendRequestAccepted"
        const val STARTED_FOLLOWING = "StartedFollowing"
        const val POST_REQUEST = "PostRequest"
        const val POST_SEND = "PostSend"
        const val POST_REQUEST_ACCEPTED = "PostRequestAccepted"
        const val LIKED_POST = "LikedPost"
        const val REPLIED_POST = "RepliedPost"
        const val COMMENTED_POST = "CommentedPost"
        const val SHARED_POST = "SharedPost"
        const val ONE_TO_1_GAME_RECEIVED = "1to1GameReceived"
        const val D_TO_1_GAME_RECEIVED = "Dto1GameReceived"
        const val ONE_TO_M_GAME_RECEIVED = "1toMGameReceived"
        const val ONE_TO_1_RESPONSE_SUBMITTED = "1to1ResponseSubmitted"
        const val D_TO_1_RESPONSE_SUBMITTED = "Dto1ResponseSubmitted"
        const val ONE_TO_M_RESPONSE_SUBMITTED = "1toMResponseSubmitted"
        const val QUESTION_ASKED_BY_ADMIN = "QuestionAskedByAdmin"

        //newly added constants for notification type 29th July 2020
        const val ONE_TO_M_SOLO_RESPONSE_SUBMITTED = "1toMSoloResponseSubmitted"
        const val TO_ALL_FRIEND_NOT_ANSWERED_IN_PLAY_GROUP = "ToAllFriendNotansweredInPlayGroup"
        const val NON_FRIEND_ANSWER_IN_PLAY_GROUP = "NonFriendAnswerInPlayGroup"
        const val FRIEND_COMMENTING_ON_FRIEND_ANSWER = "FriendCommentingOnFriendAnswer"
        const val FRIEND_COMMENTING_ON_ANSWER_I_HAVE_COMMENTED =
            "FriendCommentingOnAnswerIhaveCommented"
        const val FRIEND_COMMENTING_ON_STRANGER_ANSWER = "FriendCommentingOnStrangerAnswer"
        const val FRIEND_COMMENTING_ON_FRIEND_ANSWER_IN_PLAYGROUP =
            "FriendCommentingOnFriendAnswerInPlaygroup"
        const val FRIEND_COMMENTING_ON_ANSWER_I_HAVE_COMMENTED_IN_PLAYGROUP =
            "FriendCommentingOnAnswerIhaveCommentedInPlaygroup"
        const val FRIEND_COMMENTING_ON_STRANGER_ANSWER_IN_PLAYGROUP =
            "FriendCommentingOnStrangerAnswerInPlayGroup"
        const val NON_FRIEND_COMMENTING_ON_FRIEND_ANSWER_IN_PLAYGROUP =
            "NonFriendCommentingOnFriendAnswerInPlayGroup"
        const val COMMENTED_GAME = "CommentedGame"
        const val PLAY_GROUP_1_TO_M_RESPONSE_SUBMITTED = "PlayGroup1toMResponseSubmitted"
        const val PLAY_GROUP_1_TO_M_GAME_RECEIVED = "PlayGroup1toMGameReceived"
        const val PLAY_GROUP_COMMENTED_GAME = "PlayGroupCommentedGame"
        const val USER_ADDED_TO_PLAY_GROUP = "UserAddedtoPlayGroup"
        const val ADMIN_ADDED_TO_PLAY_GROUP = "AdminAddedtoPlayGroup"
        const val LINK_USER_ADDED_TO_PLAY_GROUP = "LinkUserAddedtoPlayGroup"
        const val NOTIFY_ADMIN_FOR_NEW_LINK_USER = "NotifyAdminForNewLinkUser"
        const val SURVEY_RECEIVED = "SurveyReceived"
        const val O_CLUB_QUESTION_LIST = "oclubQuestionList"
        const val INVITE_FRIENDS = "InviteFriends"
        const val BOND_GAME = "Bond003Game"
        const val REVIEW_ONOUREM = "ReviewOnourem"
        const val IRRELEVANT_GAME_RESPONSE = "IrrelevantGameResponse"
        const val CARD_BY_ADMIN = "CardByAdmin"
        const val ACTIVATED_COUPON = "ActivatedCoupon"
        const val APPROVE_AUDIO_BY_ADMIN = "ApproveAudioByAdmin"
        const val LIKE_AUDIO = "LikeAudio"
        const val USER_UPLOADED_AUDIO = "UserUploadedAudio"
        const val SEND_USER_CHAT_MESSAGE = "SendUserChatMessage"

        //NewUserExternalAskedByAdmin
        //NewUserSurveyAskedByAdmin
        //NewUserCardByAdmin
        //NewUserQuestionAskedByAdmin
        //NewUserTaskAskedByAdmin
        //NewUserMessageAskedByAdmin

        const val NEW_USER_EXTERNAL_ASKED_BY_ADMIN = "NewUserExternalAskedByAdmin"
        const val NEW_USER_SURVEY_ASKED_BY_ADMIN = "NewUserSurveyAskedByAdmin"
        const val NEW_USER_CARD_BY_ADMIN = "NewUserCardByAdmin"
        const val NEW_USER_QUESTION_ASKED_BY_ADMIN = "NewUserQuestionAskedByAdmin"
        const val NEW_USER_TASK_ASKED_BY_ADMIN = "NewUserTaskAskedByAdmin"
        const val NEW_USER_MESSAGE_ASKED_BY_ADMIN = "NewUserMessageAskedByAdmin"

        const val NEW_OCLUB_EXTERNAL_ASKED_BY_ADMIN = "NewOclubExternalAskedByAdmin"
        const val NEW_OCLUB_SURVEY_ASKED_BY_ADMIN = "NewOclubSurveyAskedByAdmin"
        const val NEW_OCLUB_CARD_BY_ADMIN = "NewOclubCardByAdmin"
        const val NEW_OCLUB_QUESTION_ASKED_BY_ADMIN = "NewOclubQuestionAskedByAdmin"
        const val NEW_OCLUB_TASK_ASKED_BY_ADMIN = "NewOclubTaskAskedByAdmin"
        const val NEW_OCLUB_MESSAGE_ASKED_BY_ADMIN = "NewOclubMessageAskedByAdmin"

        const val TASK_OR_MESSAGE_ASKED_BY_ADMIN = "TaskOrMessageAskedByAdmin"
        const val TASK_ASKED_BY_ADMIN = "TaskAskedByAdmin"
        const val MESSAGE_ASKED_BY_ADMIN = "MessageAskedByAdmin"
        const val EXTERNAL_ASKED_BY_ADMIN = "ExternalAskedByAdmin"
        const val SURVEY_ASKED_BY_ADMIN = "SurveyAskedByAdmin"

        const val SURVEY_ASKED_BY_ADMIN_IN_PLAYGROUP = "SurveyAskedByAdminInPlaygroup"
        const val CARD_ASKED_BY_ADMIN_IN_PLAYGROUP = "CardAskedInPlayGroup"
        const val EXTERNAL_ASKED_BY_ADMIN_IN_PLAYGROUP = "ExternalAskedByAdminInPlaygroup"
        const val TASK_OR_MESSAGE_ASKED_BY_ADMIN_IN_PLAYGROUP = "TaskOrMessageAskedByAdminInPlayGroup"
        const val TASK_ASKED_BY_ADMIN_IN_PLAYGROUP = "TaskAskedByAdminInPlayGroup"
        const val MESSAGE_ASKED_BY_ADMIN_IN_PLAYGROUP = "MessageAskedByAdminInPlayGroup"
        const val PLAY_GROUP_1TOM_GAME_RECEIVED_BY_ADMIN = "PlayGroup1toMGameReceivedByAdmin"
        const val NONE = ""
        val TAKE_TO_FRIEND_KEYS =
            arrayOf("FriendRequest", "StartedFollowing", "FriendRequestAccepted")
        val TAKE_TO_POST_KEYS = arrayOf(
            "PostRequest",
            "PostRequestAccepted",
            "PostSend",
            "LikedPost",
            "CommentedPost",
            "RepliedPost",
            "SharedPost"
        )
        val TAKE_TO_ACTIVITY_KEYS = arrayOf(
            "1to1GameReceived",
            "Dto1GameReceived",
            "1toMGameReceived",
            "1to1ResponseSubmitted",
            "Dto1ResponseSubmitted",
            "1toMResponseSubmitted",
            "CommentedGame",
            "PlayGroup1toMResponseSubmitted",
            "PlayGroup1toMGameReceived",
            "PlayGroupCommentedGame"
        )
        val TAKE_TO_SINGLE_PLAY_GROUP_MEMBER_LIST = arrayOf(
            "UserAddedtoPlayGroup",
            "AdminAddedtoPlayGroup",
            "LinkUserAddedtoPlayGroup",
            "NotifyAdminForNewLinkUser"
        )
    }
}