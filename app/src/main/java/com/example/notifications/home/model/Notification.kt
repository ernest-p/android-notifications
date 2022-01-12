package com.example.notifications.home.model

import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification

data class Notification(
    var id: Int,
    var title: CharSequence?,
    var text: CharSequence?,
    var postTime: Long,
    var removed: Boolean = false,
    var smallIcon: Icon? = null,
    var iconRes: Int? = null
) {
    constructor(sbn: StatusBarNotification) : this(
        sbn.id,
        sbn.notification.extras
            .getCharSequence("android.title"),
        sbn.notification.extras
            .getCharSequence("android.text"),
        sbn.postTime
    )

    fun setIcon(icon: Icon?) {
        smallIcon = icon
    }

    fun setIcon(icon: Int?) {
        iconRes = icon
    }
}