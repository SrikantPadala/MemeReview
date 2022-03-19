package com.ztute.memereview.ui

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ztute.memereview.R
import com.ztute.memereview.domain.model.Meme

@BindingAdapter("memeData")
fun bindImage(imgView: ImageView, meme: Meme) {
    meme.url.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .centerCrop()
            .override(300, 300)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
}