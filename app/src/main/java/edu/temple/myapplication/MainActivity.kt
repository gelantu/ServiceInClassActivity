package edu.temple.myapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var timerTextView: TextView
    private var timerService: TimerService.TimerBinder? = null
    private var isBound = false

    private val timeHandler = Handler(Looper.getMainLooper()) { msg ->
        timerTextView.text = msg.what.toString()
        true
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            timerService = service as TimerService.TimerBinder
            isBound = true
            timerService?.setHandler(timeHandler)

        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        timerTextView = findViewById(R.id.textView)

        startButton.setOnClickListener {
            if (isBound) {
                if (timerService?.isRunning == true) {
                    timerService?.pause()
                    startButton.text = "Start"
                } else {
                    timerService?.start(60)
                    startButton.text = "Pause"
                }
            }
        }

        stopButton.setOnClickListener {
            if (isBound) {
                timerService?.stop()
                timerTextView.text = "0"
                startButton.text = "Start"
            }
        }
    }
    override fun onStart() {
        super.onStart()
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}
