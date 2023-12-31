package com.dicoding.todoapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.M_NOTIF_ID
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {

        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent

        val appContext: Context = applicationContext
        val taskRepository: TaskRepository = TaskRepository.getInstance(appContext)
        var nearestTask: Task = taskRepository.getNearestActiveTask()
        var pendingIntent: PendingIntent = getPendingIntent(nearestTask)!!

        val mNotificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(appContext,
            NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pendingIntent)
            .setContentTitle(nearestTask.title)
            .setContentText(DateConverter.convertMillisToString(nearestTask.dueDateMillis))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* Create or update. */
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = channelName.toString()
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager?.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()

        mNotificationManager?.notify(NOTIFICATION_ID, notification)

        return Result.success()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }

}
