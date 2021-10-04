package com.example.myalarmclock

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import com.example.myalarmclock.databinding.ActivityMainBinding
import java.lang.IllegalArgumentException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), TimeAlertDialog.Listener,
        DatePickerFragment.OnDateSelectedListener, TimePickerFragment.OnTimeSelectedListener{
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.getBooleanExtra("onReceive",false) == true){
            when{
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 ->{
                    setShowWhenLocked(true)
                    setTurnScreenOn(true)
                    val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(this, null)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->{
                    window.addFlags(FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED)
                    val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(this, null)
                }
                else -> {
                    window.addFlags(FLAG_TURN_SCREEN_ON or FLAG_SHOW_WHEN_LOCKED or FLAG_DISMISS_KEYGUARD)
                }
            }
            val dialog = TimeAlertDialog()
            dialog.show(supportFragmentManager, "alert_dialog")
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setAlarm.setOnClickListener{
            /*val calender = Calendar.getInstance()
            calender.timeInMillis = System.currentTimeMillis()
            calender.add(Calendar.SECOND, 5)
            setAlarmManager(calender)*/
            val date = "${binding.dateText.text} ${binding.timeText.text}".toDate()
            when {
                date != null ->{
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    setAlarmManager(calendar)
                    Toast.makeText(this, "アラームをセットしました", Toast.LENGTH_SHORT).show()
                }
                else ->{
                    Toast.makeText(this, "日付の形式が正しくありません", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.cancelAlarm.setOnClickListener{
            cancelAlarmManager()
        }

        binding.dateText.setOnClickListener{
            val dialog = DatePickerFragment()
            dialog.show(supportFragmentManager, "date_dialog")
        }

        binding.timeText.setOnClickListener{
            val dialog = TimePickerFragment()
            dialog.show(supportFragmentManager, "time_dialog")
        }
    }
    private fun setAlarmManager(calendar: Calendar){
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP->{//Android5.0以上
                val info = AlarmManager.AlarmClockInfo(calendar.timeInMillis,null)
                am.setAlarmClock(info, pending)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ->{//Android4.4以上
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
            }
            else ->{//それ未満
                am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
            }
        }
    }
    private fun cancelAlarmManager(){
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        val pending = PendingIntent.getBroadcast(this, 0, intent, 0)
        am.cancel(pending)
    }
    override fun getUp(){
        //Toast.makeText(this, "起きるがタップされました", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun snooze() {
        //Toast.makeText(this,"あと5分がタップされました", Toast.LENGTH_SHORT).show()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE,5)
        setAlarmManager(calendar)
        finish()
    }

    override fun onSelected(year: Int, month: Int, date: Int) {
        val c = Calendar.getInstance()
        c.set(year, month, date)
        binding.dateText.text = DateFormat.format("yyyy/MM/dd",c)
    }

    override fun onSelected(hourOfDay: Int, minute: Int) {
        binding.timeText.text = "%1$02d:%2$02d".format(hourOfDay,minute)
    }

    //拡張関数
    private fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"):Date? {
        return try{
            SimpleDateFormat(pattern).parse(this)
        }catch(e: IllegalArgumentException){
            return null
        }catch(e: ParseException){
            return null
        }
    }
}