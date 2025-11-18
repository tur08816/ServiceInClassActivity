package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.Service
import java.security.Provider
import java.util.logging.Handler
import java.util.logging.LogRecord
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {


//create handler and show countdown in activity step 2
    lateinit var TimeBinder: TimerService.TimerBinder
    var isConnected = false

    lateinit var Menu: Menu

//    val timerHandler = Handler(Looper.getMainLooper()){
//
//        true
//    }

    private val handler = object : android.os.Handler() {


        override fun handleMessage(msg: Message) {
            findViewById<TextView>(R.id.textView).text = msg.what.toString()
        }

    }


    val serviceConnection = object : ServiceConnection{
        override
        fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            TimeBinder = p1 as TimerService.TimerBinder
            TimeBinder.setHandler(handler)
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    //menu call back -> menu item determines what button is clicked


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        if (menu != null) {
            Menu = menu
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.action_start -> {
                onTimerStart()
                true
            }
            R.id.action_stop -> {
                onTimerStop()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onTimerStop(){
        var item = Menu.findItem(R.id.action_start)

        val text = findViewById<TextView>(R.id.textView)
        item.icon = ContextCompat.getDrawable(this, R.drawable.timer_24px)
        text.text = "0"
        TimeBinder.stop()
    }

    fun onTimerStart(){
        var item = Menu.findItem(R.id.action_start)
        if(!TimeBinder.isRunning && isConnected){
            item.icon = ContextCompat.getDrawable(this, R.drawable.timer_pause_24px)
            TimeBinder.start(1000)
        }else if(TimeBinder.paused && isConnected){
            item.icon = ContextCompat.getDrawable(this, R.drawable.timer_pause_24px)
            TimeBinder.pause()
        }else if(!TimeBinder.paused && isConnected){
            item.icon = ContextCompat.getDrawable(this, R.drawable.play_arrow_24px)
            TimeBinder.pause()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = findViewById<TextView>(R.id.textView)

        //bind
        var intent = Intent(this, TimerService::class.java)
        bindService(intent,serviceConnection,BIND_AUTO_CREATE)


    }

    override fun onDestroy() {
        //unbind
        unbindService(serviceConnection)

        super.onDestroy()
    }
}