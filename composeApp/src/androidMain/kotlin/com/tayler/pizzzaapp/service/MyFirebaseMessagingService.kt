package com.tayler.pizzzaapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tayler.pizzzaapp.ui.MainActivity
import com.tayler.pizzzaapp.utils.NotificationHelper
import java.util.Locale

class MyFirebaseMessagingService : FirebaseMessagingService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("es", "ES")
            val result = tts?.setLanguage(locale)
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("FCM", "Language not supported for TTS")
            } else {
                // Intentar buscar una voz de mujer en español
                try {
                    val femaleVoice = tts?.voices?.find { 
                        it.locale.language == locale.language && 
                        (it.name.lowercase().contains("female") || it.name.lowercase().contains("mujer"))
                    } ?: tts?.voices?.find { 
                        it.locale.language == locale.language 
                    }
                    
                    femaleVoice?.let { tts?.voice = it }
                } catch (e: Exception) {
                    Log.w("FCM", "Could not set specific female voice, using default")
                }
                
                isTtsReady = true
            }
        } else {
            Log.e("FCM", "TTS Initialization failed")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        // Extraer datos con validaciones solicitadas
        val tipoEntrega = message.data["receiver"]?.uppercase() ?: "DELIVERY"
        val cliente = message.data["client"] ?: "Tayler"

        val voiceMessage = "Llegó un pedido de $tipoEntrega para $cliente"

        message.notification?.let {
            showNotification(it.title ?: "PizzzaApp", it.body ?: "")
        }

        speakMessage(voiceMessage)
    }

    private fun speakMessage(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "OrderArrival")
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(com.tayler.pizzzaapp.R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
