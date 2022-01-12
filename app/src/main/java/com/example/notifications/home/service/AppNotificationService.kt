package com.example.notifications.home.service

import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.notifications.home.model.Notification
import com.example.notifications.home.repository.NotificationRepository

class AppNotificationService : NotificationListenerService() {

    private val repository = NotificationRepository.instance

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onListenerConnected() {
        super.onListenerConnected()

        val list = ArrayList<Notification>()
        for (sbn in activeNotifications) {
            val notification = Notification(sbn)
            setIcon(notification, sbn)
            list.add(notification)
        }
        repository.submitList(list)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.also {
            val notification = Notification(sbn)
            setIcon(notification, sbn)
            repository.add(notification)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.also {
            repository.remove(Notification(it))
        }
    }

    private fun setIcon(notification: Notification, sbn: StatusBarNotification?) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notification.setIcon(sbn?.notification?.smallIcon)
        }
        // else {
            notification.setIcon(sbn?.notification?.icon)
        //}
    }
}