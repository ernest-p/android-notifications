package com.example.notifications.home.repository

import androidx.lifecycle.MutableLiveData
import com.example.notifications.extensions.notifyObserver
import com.example.notifications.home.model.Notification
import java.util.*

class NotificationRepository {
    val notificationList = MutableLiveData<MutableList<Notification>>()

    fun submitList(list: ArrayList<Notification>) {
        notificationList.value = list
    }

    fun add(notification: Notification) {
        val found = notificationList.value?.find {
            it.id == notification.id
        }
        found?.apply {
            title = notification.title
            text = notification.text
            postTime = notification.postTime
        }
        if(found == null) {
            notificationList.value?.add(notification)
        }
        notificationList.notifyObserver()
    }

    fun remove(notification: Notification) {
        val found = notificationList.value?.find {
            it.id == notification.id
        }
        found?.removed = true
        notificationList.notifyObserver()
    }

    companion object {
        //TODO: use Dagger
        var instance = NotificationRepository()
            private set
    }
}