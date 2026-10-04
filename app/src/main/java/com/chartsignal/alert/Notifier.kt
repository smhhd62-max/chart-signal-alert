package com.chartsignal.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import androidx.core.app.NotificationCompat

object Notifier {

    private const val SIGNAL_CHANNEL_ID = "signal_channel"
    private var notifId = 2000

    fun notify(context: Context, signal: SignalType) {
        val (title, text) = when (signal) {
            SignalType.GREEN_TRIANGLE -> "🟢 سیگنال خرید" to "مثلث سبز (AI+) ظاهر شد"
            SignalType.RED_TRIANGLE -> "🔴 سیگنال فروش" to "مثلث قرمز (AI-) ظاهر شد"
            else -> return
        }

        createChannel(context)

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, SIGNAL_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(sound)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notifId++, notification)

        vibrate(context)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SIGNAL_CHANNEL_ID,
                "Chart Signals",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            val nm = context.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun vibrate(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(400)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
