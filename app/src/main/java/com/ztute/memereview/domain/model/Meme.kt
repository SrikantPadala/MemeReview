package com.ztute.memereview.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Meme(
    val boxCount: Int,
    val height: Int,
    val id: String,
    val name: String,
    val url: String,
    val width: Int
) : Parcelable