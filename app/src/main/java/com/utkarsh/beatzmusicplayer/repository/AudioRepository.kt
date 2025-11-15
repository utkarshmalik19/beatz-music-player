package com.utkarsh.beatzmusicplayer.repository

import android.content.Context
import android.provider.MediaStore
import com.utkarsh.beatzmusicplayer.model.AudioFile

class AudioRepository(private val context: Context) {

    fun getAllAudioFiles(): List<AudioFile> {
        val audioList = mutableListOf<AudioFile>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                audioList.add(
                    AudioFile(
                        id = cursor.getLong(idCol),
                        title = cursor.getString(titleCol),
                        artist = cursor.getString(artistCol),
                        duration = cursor.getLong(durationCol),
                        data = cursor.getString(dataCol)
                    )
                )
            }
        }

        return audioList
    }
}