package com.example.notifications.home.view

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notifications.databinding.ItemNotificationBinding
import com.example.notifications.home.model.Notification

class NotificationAdapter : ListAdapter<Notification,
        NotificationAdapter.NotificationViewHolder>(UserDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {

        return NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class UserDiffCallBack : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
            oldItem.text == newItem.text &&
                    oldItem.title == newItem.title &&
                    oldItem.postTime == oldItem.postTime
    }

    class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                title.text = notification.title
                text.text = notification.text
                icon.setImageDrawable(getIconDrawable(notification))
            }
        }

        private fun getIconDrawable(notification: Notification): Drawable? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notification.smallIcon?.loadDrawable(binding.root.context)
            } else {
                return ResourcesCompat.getDrawable(
                    binding.root.context.resources,
                    notification.iconRes ?: 0, null
                )
            }
        }
    }
}
