package com.example.notifications.home.view

import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.notifications.R
import com.example.notifications.databinding.ActivityMainBinding
import com.example.notifications.extensions.notifyObserver
import com.example.notifications.home.model.Notification
import com.example.notifications.home.repository.NotificationRepository
import com.example.notifications.home.service.AppNotificationService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION =
            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
        private const val TABLE_NAME = "enabled_notification_listeners"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var manager: NotificationManager
    private val adapter = NotificationAdapter()
    private var onlyActive = false
    private val repository = NotificationRepository.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        manager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        binding.apply {
            recyclerView.adapter = adapter
        }
        observeList()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu);
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.list_toggle -> {
                onlyActive = !onlyActive
                repository.notificationList.notifyObserver()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if (!checkNotificationAccess()) {
            askForPermission {
                startActivity(Intent(NOTIFICATION_PERMISSION))
            }
        }
    }

    private fun askForPermission(onAccept: () -> Unit) {
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.permission_rationale, getString(R.string.app_name)))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                onAccept()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                finish()
            }
            .show()
    }

    private fun checkNotificationAccess(): Boolean {
        val componentService = ComponentName(this, AppNotificationService::class.java)

        // Gets applications using notification settings access
        // getString returns format : [package name]/[Component name]
        val apps = Settings.Secure.getString(contentResolver, TABLE_NAME)

        return apps.contains(componentService.flattenToString())
    }

    private fun submitList(list: MutableList<Notification>) {
        try {
            val filteredList = if (onlyActive)
                list.filter { notification ->
                    !notification.removed
                } else list

            val sorted = filteredList.sortedByDescending {
                it.postTime
            }
            val top20 = sorted.subList(0, min(20, sorted.size))
            adapter.submitList(top20.toMutableList())
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun observeList() {
        repository.notificationList.value?.let {
            submitList(it)
        }
        repository.notificationList.observe(this) {
            it?.let { list ->
                submitList(list)
            }
        }
    }
}