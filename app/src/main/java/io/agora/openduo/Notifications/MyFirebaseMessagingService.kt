package io.agora.openduo.Notifications

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.agora.openduo.Constants
import io.agora.openduo.DataLayer.AppPreference
import io.agora.openduo.OpenDuoApplication
import io.agora.openduo.R
import io.agora.openduo.activities.CallActivity
import kotlin.random.Random


/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT"></action>
</intent-filter> *
 */
@ExperimentalUnsignedTypes
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"

    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(
            TAG,
            "From: " + remoteMessage.from
        )

        Log.d("FCM NOTIFICATION",remoteMessage.notification.toString())

        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra(Constants.KEY_CALLING_CHANNEL, getString(R.string.default_notification_channel_id))
        intent.putExtra(Constants.KEY_CALLING_PEER, "Ashwani")
        intent.putExtra(Constants.KEY_CALLING_ROLE, Constants.ROLE_CALLEE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(this, 113, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val notificationBuilder =
                NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id))
                        .setSmallIcon(R.drawable.btn_startcall)
                        .setContentTitle("Incoming call")
                        .setContentText("(919) 555-1234")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)

                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        //.setFullScreenIntent(pendingIntent, true)


        val incomingCallNotification = notificationBuilder.build()
        val a = Random(100).nextInt(1000)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        //notificationManager.notify(a, notificationBuilder.build())


        startForeground(a, incomingCallNotification)
        // Check if message co6 5   \
        //]=k-0j9h8gf765dc4321  `?ntains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(
                TAG,
                "Message data payload: " + remoteMessage.data
            )

            remoteMessage.data?.let {
                val calleeRTMToken = it["calleeRTMToken"].toString()
                val channelID = it["channelID"].toString()
                val callerRTMToken = it["callerRTMToken"].toString()
                val calleeName = it["calleeName"].toString()
                val callerName = it["callerName"].toString()

                //val user = AppPreference.getUsers()?.first { calleeName.equals(it.user_id) }
                OpenDuoApplication.instance.initConfig()

                print(calleeRTMToken)
                print(channelID)
                print(callerRTMToken)
                print(calleeName)
                print(callerName)


//                val intent = Intent(this, CallActivity::class.java)
//                intent.putExtra(Constants.KEY_CALLING_CHANNEL, channelID)
//                intent.putExtra(Constants.KEY_CALLING_PEER, "Ashwani")
//                intent.putExtra(Constants.KEY_CALLING_ROLE, Constants.ROLE_CALLEE)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)





            } ?: kotlin.run {
                return
            }

        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        AppPreference.saveFCMDeviceID(token)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
//    private fun showCommentAddedNotification(messageBody: JsonObject) {
//        val ticket = Gson().fromJson<TicketNotification>(
//            Gson().toJson(messageBody),
//            object : TypeToken<TicketNotification>() {}.type
//        )
//        /* val intent = Intent(this, SplashActivity::class.java)
//         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//         intent.putExtra(
//             notificationActionKey,
//             NotificationActionsEnum.NT_OPEN_TICKET_L3.value.toInt()
//         )
//         intent.putExtra("id", messageBody.get("ticket_id").asInt)
//
//         val pendingIntent = PendingIntent.getActivity(
//             this, 0, intent,
//             PendingIntent.FLAG_ONE_SHOT
//         )
//         val title = messageBody.get("title").asString
//         val content = messageBody.get("ticket_description").asString
//         val bigText = messageBody.get("body").asString
//
//         NotificationHelper.sendBigTextNotification(
//             this,
//             title,
//             content,
//             bigText,
//             pendingIntent
//         )*/
//
//        initNotification(
//            NotificationActionsEnum.NT_OPEN_TICKET_L3,
//            messageBody,
//            ticket.title,
//            ticket.ticket_description,
//            ticket.body
//        )
//    }

    /**
     * Create and show a New Notice Notification
     *
     * @param messageBody FCM message body received.
     */
//    private fun showNewNotice(messageBody: JsonObject) {
//        val notice = Gson().fromJson<NoticeNotification>(
//            messageBody,
//            object : TypeToken<NoticeNotification>() {}.type
//        )
//
//        initNotification(
//            NotificationActionsEnum.NT_OPEN_NOTICE_L3,
//            messageBody,
//            notice.title,
//            notice.body
//        )
//
//    }


    /**
     * Create and show a General Notification
     *
     * @param messageBody FCM message body received.
     */
//    private fun showGeneralNotification(messageBody: JsonObject) {
//        val notification = Gson().fromJson<GeneralNotification>(
//            messageBody,
//            object : TypeToken<GeneralNotification>() {}.type
//        )
//
//        NotificationHelper.sendNormalNotification(this,notification.title,notification.body)
//
//    }


    /**
     * Create and show Notification for Delivery
     * @param messageBody
     */
//    private fun showDeliveryEntry(messageBody: JsonObject) {
//        val response = Gson().toJson(messageBody)
//        val deliveryNotification = Gson().fromJson<DeliveryNotification>(
//            response,
//            object : TypeToken<DeliveryNotification>() {}.type
//        )
//
//        val map = HashMap<String, String>()
//        map[NestapNotificationConstant.declineDelivery] = "Decline"
//        map[NestapNotificationConstant.approveDelivery] = "Approve"
//
//        initNotificationWithCustomApproveDecline(
//            NotificationActionsEnum.NT_OPEN_DELIVERY_L3,
//            messageBody,
//            deliveryNotification.title,
//            "${deliveryNotification.person_name} from ${deliveryNotification.company_name}",
//            NestapNotificationConstant.approveDelivery,
//            NestapNotificationConstant.declineDelivery
//        )
//
//    }


    /**
     * Create and show Notification for Guest
     * @param messageBody
     */
//    private fun showGuestEntry(messageBody: JsonObject) {
//        val response = Gson().toJson(messageBody)
//        val guestNotification = Gson().fromJson<GuestNotification>(
//            response,
//            object : TypeToken<GuestNotification>() {}.type
//        )
//
//        /*val map = HashMap<String, String>()
//        map[NestapNotificationConstant.declineVisitor] = "Decline"
//        map[NestapNotificationConstant.approveVisitor] = "Approve"*/
//
//
//        initNotificationWithCustomApproveDecline(
//            NotificationActionsEnum.NT_OPEN_GUEST_L3,
//            messageBody,
//            guestNotification.title,
//            guestNotification.person_name,
//            NestapNotificationConstant.approveVisitor,
//            NestapNotificationConstant.declineVisitor
//        )
//
//    }


//    private fun initNotification(
//        notificationActionsEnum: NotificationActionsEnum,
//        messageBody: JsonObject,
//        title: String,
//        content: String,
//        bigText: String? = null,
//        actions: HashMap<String, String>? = null
//    ) {
//        val intent = Intent(this, SplashActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        intent.putExtra(notificationActionKey, notificationActionsEnum.value.toInt())
//        intent.putExtra(
//            NestapNotificationConstant.notificationResponseBody,
//            Gson().toJson(messageBody)
//        )
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
//
//        val bigTexts: String = bigText ?: ""
//
//        NotificationHelper.sendNotificationWithAction(
//            this,
//            title,
//            content,
//            bigTexts,
//            pendingIntent,
//            messageBody,
//            actions
//        )
//
//    }


//    private fun initNotificationWithCustomApproveDecline(
//        notificationActionsEnum: NotificationActionsEnum,
//        messageBody: JsonObject,
//        title: String,
//        content: String,
//        approveKey: String,
//        declineKey: String
//    ) {
//        val intent = Intent(this, SplashActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        intent.putExtra(notificationActionKey, notificationActionsEnum.value.toInt())
//        intent.putExtra(
//            NestapNotificationConstant.notificationResponseBody,
//            Gson().toJson(messageBody)
//        )
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
//
//        NotificationHelper.sendNotificationWithApproveDecline(
//            this,
//            title,
//            content,
//            pendingIntent,
//            messageBody,
//            approveKey,
//            declineKey
//        )
//
//    }


}