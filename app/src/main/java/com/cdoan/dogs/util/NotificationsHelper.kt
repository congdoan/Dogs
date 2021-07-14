package com.cdoan.dogs.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cdoan.dogs.R
import com.cdoan.dogs.view.MainActivity

class NotificationsHelper(val context: Context) {

    private val channelId = "Dogs channel id"
    private val notificationId = 123

    fun createNotification() {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.dog)
        val notification = NotificationCompat.Builder(context, channelId)
            .setLargeIcon(icon)
            .setSmallIcon(R.drawable.dog_icon)
            .setContentTitle("Dogs retrieved")
            .setContentText("This notification has some content")
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigLargeIcon(null)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                "Dogs channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Dogs channel description" }
        )
    }

}