package com.onourem.android.activity.ui.audio.playback

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.onourem.android.activity.ui.audio.models.AudioResponse
import java.util.concurrent.TimeUnit


object SongProvider {
    private const val TITLE = 0
    private const val TRACK = 1
    private const val YEAR = 2
    private const val DURATION = 3
    private const val PATH = 4
    private const val ALBUM = 5
    private const val ARTIST_ID = 6
    private const val ARTIST = 7
    private const val MIME_TYPE = 8

    private val BASE_PROJECTION = arrayOf(
        MediaStore.Audio.AudioColumns.TITLE, // 0
        MediaStore.Audio.AudioColumns.TRACK, // 1
        MediaStore.Audio.AudioColumns.YEAR, // 2
        MediaStore.Audio.AudioColumns.DURATION, // 3
        MediaStore.Audio.AudioColumns.DATA, // 4
        MediaStore.Audio.AudioColumns.ALBUM, // 5
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 6
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.MIME_TYPE
    )

    private val mAllDeviceSongs = ArrayList<Song>()

    fun getAllDeviceSongs(context: Context): MutableList<Song> {
        val cursor = makeSongCursor(context)
        return getSongs(cursor)
    }

    //
    fun getAllServerSongs(audioResponse: ArrayList<AudioResponse>): MutableList<Song> {
        return getSongsFromServer(audioResponse)
    }

    //
    private fun getSongs(cursor: Cursor?): MutableList<Song> {
        val songs = ArrayList<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val song = getSongFromCursorImpl(cursor)
                if (song.duration >= 2000) {
                    songs.add(song)
                    mAllDeviceSongs.add(song)
                }
            } while (cursor.moveToNext())
        }

        cursor?.close()

        return songs
    }

    //
    private fun getSongsFromServer(audioResponse: ArrayList<AudioResponse>): MutableList<Song> {
        val songs = ArrayList<Song>()
        audioResponse.forEach { item ->
            val song = getSongFromServerImpl(item)
            songs.add(song)
            mAllDeviceSongs.add(song)
        }
        return songs
    }


    private fun getSongFromCursorImpl(cursor: Cursor): Song {

        val title = cursor.getString(TITLE)
        val trackNumber = 0
        val year = 0
        val duration = cursor.getInt(DURATION)
        val uri = cursor.getString(PATH)
        val albumName = "Onourem"
        val artistId = 0
        val artistName = "Artist"
        val extension = "mp3"

        return Song(
            "",
            "",
            "",
            "",
            "",
            uri,
            "",
            "",
            "",
            title,
            trackNumber,
            year,
            duration,
            albumName,
            artistId,
            artistName,
            false,
            false,
            "",
            "",
            "",
            "",
            "",
            extension,
            "",
            "","0"
        )
    }

    private fun getSongFromServerImpl(audioResponse: AudioResponse): Song {

        val title = audioResponse.audioTitle!!
        val trackNumber = 1
        val year = 2021
        val duration = audioResponse.audioDuration
        val albumName = ""
        val artistId = 100
        val artistName = ""
        val isAudioLiked = audioResponse.isAudioLiked
        val userName = audioResponse.userName
        val extension = "mp3"

        val status = if (audioResponse.audioStatus != null){
            audioResponse.audioStatus!!
        }else{
            ""
        }

        val userFollowingCreator = if (audioResponse.userFollowingCreator != null){
            audioResponse.userFollowingCreator!!
        }else{
            ""
        }

        val commentCount = if (audioResponse.commentCount != null){
            audioResponse.commentCount!!
        }else{
            ""
        }

        val audioRating = if (audioResponse.audioRating != null){
            audioResponse.audioRating!!
        }else{
            "0"
        }



        return Song(
            audioResponse.audioId!!,
            audioResponse.creatorId!!,
            audioResponse.createdDate!!,
            audioResponse.numberOfLike!!,
            audioResponse.audioDuration!!,
            audioResponse.audioUrl!!,
            audioResponse.privacyId!!,
            audioResponse.profilePictureUrl!!,
            audioResponse.categoryName!!,
            title,
            trackNumber,
            year,
            duration!!.toInt(),
            albumName,
            artistId,
            artistName,
            false,
            false,
            isAudioLiked!!,
            userName!!,
            audioResponse.numberOfViews!!,
            status,
            userFollowingCreator,
            extension,
            audioResponse.userType,
            commentCount,
            audioRating
        )
    }

    @SuppressLint("Recycle")
    private fun makeSongCursor(context: Context): Cursor? {

        val selection = "${MediaStore.Audio.Media.DURATION} <= ?"
        val selectionArgs = arrayOf(
            TimeUnit.MILLISECONDS.convert(200, TimeUnit.SECONDS).toString()
        )

        return try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                BASE_PROJECTION, null, null, null
            )


        } catch (e: SecurityException) {
            null
        }

    }
}
