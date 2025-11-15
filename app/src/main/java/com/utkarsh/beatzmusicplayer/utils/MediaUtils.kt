package com.utkarsh.beatzmusicplayer.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.ByteArrayInputStream

fun getAlbumArt(filePath: String): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(filePath)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()
        art?.let { BitmapFactory.decodeStream(ByteArrayInputStream(it)) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}