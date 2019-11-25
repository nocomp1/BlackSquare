package com.example.nextsoundz.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.nextsoundz.Config.Config
import com.example.nextsoundz.R
import com.example.nextsoundz.SettingsDialogActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class FirebaseMessaging :  FirebaseMessagingService() {

    val notificationId = 0
    val CHANNEL_ID = "SHOES_PUSH_NOTIFICATION"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
         // super.onMessageReceived(remoteMessage)



// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage!!.getFrom())

// Check if message contains a data payload.
        if (remoteMessage.getData().size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData())

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.

                          scheduleJob(remoteMessage)

            } else {
                // Handle message within 10 seconds


                        // handleNow()
            }

        }

// Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification()!!.getBody())
        }

// Also if you intend on generating your own notifications as a result of a received FCM
// message, here is where that should be initiated. See sendNotification method below.

    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {

        Log.d(TAG, "Refreshed token: " + token)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.


       // sendRegistrationToServer(token)

        super.onNewToken(token)
    }






    private fun scheduleJob(remoteMessage: RemoteMessage) {

        val data = remoteMessage.getData()
        Config.title = data.get("title")
        Config.content = data.get("content")
        Config.imageUrl = data.get("imageUrl")
        Config.gameUrl = data.get("gameUrl")

        if (remoteMessage.data != null){

            Observable.just(pushNotification())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { builder ->
                    with(NotificationManagerCompat.from(this)) {
                        // notificationId is a unique int for each notification that you must define

                        notify(notificationId, builder!!.build())
                    }
                }


        }

    }




    fun pushNotification(): NotificationCompat.Builder? {


        //Create a notification channel
        createNotificationChannel()


        // Create an explicit intent for an Activity in your app
        //set the notification tap action
        val intent = Intent(this, SettingsDialogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)




        //Build your notification
        var notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_search)
            .setContentTitle("Search")
            .setContentText("What are you searching for")
            .setLargeIcon(getBitmapFromURL("https://cdn.jackrabbit.com/media/catalog/product/cache/1/small_image/397x196/9df78eab33525d08d6e5fb8d27136e95/w/o/womens-brooks-adrenaline-gts-20-running-shoe-color-greypale-peach-regular-width-size-9-609465416429-01.2839_1.jpg"))
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(getBitmapFromURL("https://cdn.jackrabbit.com/media/catalog/product/cache/1/small_image/397x196/9df78eab33525d08d6e5fb8d27136e95/w/o/womens-brooks-adrenaline-gts-20-running-shoe-color-greypale-peach-regular-width-size-9-609465416429-01.2839_1.jpg"))
                    .bigLargeIcon(null)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return notification
    }


    private fun createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.atmospheric_tap)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    fun getBitmapFromURL(src: String): Bitmap? {

        //val url = URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464")
//val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())


        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            return null
        }

    }




}