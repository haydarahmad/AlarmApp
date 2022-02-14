package com.Haydar.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.Haydar.smartalarm.data.Alarm
import com.Haydar.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.Haydar.smartalarm.helper.timeFormatter
import com.Haydar.smartalarm.local.AlarmDB
import kotlinx.android.synthetic.main.activity_repeating_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get () = _binding as ActivityRepeatingAlarmBinding

    private val db by lazy { AlarmDB(this) }

    private var alarmService : AlarmReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repeating_alarm)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeating.setOnClickListener {
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "timePickerDialog")
            }
        }
        btn_add.setOnClickListener {
            val time = tv_once_time.text.toString()
            val message = edt_note_reapeating_alarm.text.toString()

            if (time == "Time") {
                Toast.makeText(
                    applicationContext, getString(R.string.txt_toast_add_repeating),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                alarmService?.setRepeatingAlarm(
                    applicationContext,
                    AlarmReceiver.TYPE_REPEATING,
                    time,
                    message,
                )
                alarmService?.setRepeatingAlarm(applicationContext,AlarmReceiver.TYPE_REPEATING,time,message)
                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().addAlarm(
                        Alarm(
                            0,
                            "Repeating Alarm",
                            time,
                            message,
                            AlarmReceiver.TYPE_REPEATING
                        )
                    )
                    Log.i("AddAlarm", "alarm set on: $time with message $message")
                    finish()
                }
            }

            btn_cancel.setOnClickListener {
                finish()
            }
        }
    }


    override fun onTimeSetListener(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hourOfDay, minute)
    }
}
