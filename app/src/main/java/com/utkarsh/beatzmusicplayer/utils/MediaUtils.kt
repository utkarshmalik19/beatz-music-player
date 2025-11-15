package com.utkarsh.beatzmusicplayer.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.ByteArrayInputStream

fun getAlbumArt(path: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(path)
        val art = retriever.embeddedPicture
        if (art != null) BitmapFactory.decodeByteArray(art, 0, art.size) else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}