package com.tayler.pizzzaapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
    private var pendingMessage: String? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("FCM", "MyFirebaseMessagingService CREADO (onCreate)")
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setLanguage(Locale("es", "ES"))
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("FCM", "TTS started speaking")
                }
                override fun onDone(utteranceId: String?) {
                    Log.d("FCM", "TTS finished speaking, releasing WakeLock")
                    releaseWakeLock()
                }
                override fun onError(utteranceId: String?) {
                    Log.e("FCM", "TTS error, releasing WakeLock")
                    releaseWakeLock()
                }
            })
            
            isTtsReady = true
            pendingMessage?.let {
                speakMessage(it)
                pendingMessage = null
            }
        } else {
            Log.e("FCM", "TTS Initialization failed")
            releaseWakeLock()
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            Log.e("FCM", "Error releasing WakeLock: ${e.message}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        Log.d("FCM", "======= NUEVO PUSH RECIBIDO =======")
        Log.d("FCM", "De: ${message.from}")
        Log.d("FCM", "Prioridad: ${message.priority}")
        
        // Log de todos los datos en el objeto 'data'
        if (message.data.isNotEmpty()) {
            Log.d("FCM", "Contenido de DATA:")
            message.data.forEach { (key, value) ->
                Log.d("FCM", "   Key: $key | Value: $value")
            }
        } else {
            Log.d("FCM", "El objeto DATA está VACÍO")
        }

        // Log del objeto 'notification' si existe
        message.notification?.let {
            Log.d("FCM", "Contenido de NOTIFICATION:")
            Log.d("FCM", "   Title: ${it.title}")
            Log.d("FCM", "   Body: ${it.body}")
        } ?: Log.d("FCM", "El objeto NOTIFICATION es NULO")
        Log.d("FCM", "===================================")

        // Adquirir WakeLock para mantener la CPU activa mientras hablamos
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PizzzaApp:TTSLock")
        wakeLock?.acquire(30000) // 30 segundos máximo

        // Extraer datos intentando varias llaves comunes (Data-only)
        val titulo = message.data["title"] ?: message.data["titulo"] ?: message.notification?.title ?: "PizzzaApp"
        val cuerpo = message.data["body"] ?: message.data["mensaje"] ?: message.data["message"] ?: message.notification?.body ?: "Nuevo pedido recibido"
        
        val tipoEntrega = message.data["receiver"]?.uppercase() ?: "DELIVERY"
        val cliente = message.data["client"] ?: "Tayler"

        val voiceMessage = "Llegó un pedido de $tipoEntrega para $cliente"

        // Mostrar notificación (necesario manualmente si se usa data-only)
        Log.d("FCM", "Showing notification: $titulo - $cuerpo")
        showNotification(titulo, cuerpo)
        
        // Ejecutar voz
        speakMessage(voiceMessage)
    }

    private fun speakMessage(text: String) {
        Log.d("FCM", "Attempting to speak: $text (Ready: $isTtsReady)")
        if (isTtsReady) {
            val params = HashMap<String, String>()
            params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "OrderArrival"
            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "OrderArrival")
            if (result == TextToSpeech.ERROR) {
                Log.e("FCM", "Error occurred while trying to speak")
                releaseWakeLock()
            }
        } else {
            Log.w("FCM", "TTS not ready yet. Queuing message.")
            pendingMessage = text
            // Si por alguna razón el tts es nulo, re-inicializar
            if (tts == null) {
                tts = TextToSpeech(this, this)
            }
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
            .setPriority(NotificationCompat.PRIORITY_MAX) // Prioridad máxima para Samsung
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        releaseWakeLock()
        super.onDestroy()
    }
}
