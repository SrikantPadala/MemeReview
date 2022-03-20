package com.ztute.memereview.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ztute.memereview.R
import timber.log.Timber

@BindingAdapter("ImageUrl")
fun bindImage(imgView: ImageView, url: String) {
    url.let {
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

@BindingAdapter("android:text")
fun setTextResource(view: TextView, @StringRes resource: Int) {
    Timber.d("resource id: $resource")
    if (resource == 0) {
        view.text = ""
    } else {
        view.setText(resource)
    }
}