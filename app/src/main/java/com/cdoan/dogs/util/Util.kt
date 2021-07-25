package com.cdoan.dogs.util

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cdoan.dogs.R

fun getProcessDrawable(context: Context) = CircularProgressDrawable(context).apply {
    strokeWidth = 10f
    centerRadius = 50f
    start()
}

fun ImageView.loadImage(
    uri: String?,
    processDrawable: CircularProgressDrawable = getProcessDrawable(this.context)
) {
    val options = RequestOptions()
                    .placeholder(processDrawable)
                    .error(R.mipmap.ic_launcher_dogs)
    Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    imageView.loadImage(url)
}