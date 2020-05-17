package com.example.blacksquare.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.blacksquare.Config.Config
import com.example.blacksquare.R
import com.example.blacksquare.SettingsDialogActivity
import com.example.blacksquare.Helpers.Definitions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso


class FirebaseMessaging : FirebaseMessagingService() {


    val notificationId = 0
    val CHANNEL_ID = "SHOES_PUSH_NOTIFICATION"
    lateinit var notificationManager: NotificationManager

    companion object {

        val CANCEL_NOTIFICATION_KEY: String? = "cancel notification"
        val CANCEL_NOTIFICATION_VALUE = 100
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage!!.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.


            } else {
                // Handle message within 10 seconds

                // handleNow()
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        val data = remoteMessage.data

        scheduleJob(data, Picasso.get().load(data.get("media-attachment-url")).get())


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
        super.onNewToken(token)
    }

    private fun scheduleJob(
        data: MutableMap<String, String>,
        imageBitmap: Bitmap
    ) {

        Config.title = data.get(Definitions.notificationTitleKey)
        Config.content = data.get(Definitions.notificationBodyKey)
        Config.imageUrl = data.get(Definitions.notificationImageKey)
        Config.deepLink = data.get(Definitions.notificationDeepLinkKey)

        Config.cta1Label = data.get(Definitions.notificationCTALabelKey)
        Config.cancelLabel = data.get(Definitions.notificationCancelLabelKey)
        Config.cta1Action = data.get("custom key 3")
        Config.cta2Action = data.get("custom key 4")

        //Create a notification channel
        createNotificationChannel()

        // Create an explicit intent for an Activity in your app
        //set the notification tap action
        ///////Intents///////
        val intent = Intent(this, SettingsDialogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val cancelIntent = Intent(this, CancelNotification::class.java)


        //////////////////CALL TO ACTION //////////////////////////
        // Key for the string that's delivered in the action's intent.
        ///This is for the user being able to respond inside of the
        //notification
        val KEY_TEXT_REPLY = "key_text_reply"
        var cta1Label: String? = Config.cta1Label
        var cancelLabel: String? = Config.cancelLabel
        var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(cta1Label)
            build()
        }

        // Build a PendingIntent for the cta1Label action to trigger.
        //Create the deeplink uri from the string
        val deeplink = Uri.parse(Config.deepLink)
        val ctaIntent = Intent(Intent.ACTION_VIEW, deeplink)


        /////pending intents////
        var cta1LabelPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, ctaIntent, 0)

        ////IF WE WANT TO ADD A CANCEL TO THE NOTIFICATION/////

//        var cancelIntentDestination: PendingIntent = PendingIntent.getActivity(
//            this,
//            CANCEL_NOTIFICATION_VALUE,
//            cancelIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        //add our id so we can dismiss the notification
//        cancelIntent.putExtra(CANCEL_NOTIFICATION_KEY, CANCEL_NOTIFICATION_VALUE)


        // Create the reply action and add the remote input.
        var action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(
                R.drawable.ic_search,
                cta1Label, cta1LabelPendingIntent
            )
                .addRemoteInput(remoteInput)
                .build()

        //Build notification
        var notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_search)
            .setContentTitle(Config.title)
            .setContentText(Config.content)
            .setLargeIcon(imageBitmap)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(imageBitmap)
                    .bigLargeIcon(null)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_search, cta1Label, cta1LabelPendingIntent)

                ////IF WE WANT TO ADD CANCEL BUTTON
               // .addAction(R.drawable.ic_search, cancelLabel, cancelIntentDestination)
            .setAutoCancel(true)

        // Register the channel with the system
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(CANCEL_NOTIFICATION_VALUE, notification.build())
    }

    private fun createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.atmospheric_tap)
            val descriptionText = "BlackSquare"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //////Inner class to dissmis the notification
    class CancelNotification : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val noti_id = intent.getIntExtra(CANCEL_NOTIFICATION_KEY, CANCEL_NOTIFICATION_VALUE)

                if (noti_id > 0) {
                    val notificationManager: NotificationManager? =
                        context!!.getSystemService(NotificationManager::class.java)
                    notificationManager!!.cancel(noti_id)
                }
            }

        }


    }

}