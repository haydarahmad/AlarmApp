package com.Haydar.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.Haydar.smartalarm.data.Alarm
import com.Haydar.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.Haydar.smartalarm.helper.timeFormatter
import com.Haydar.smartalarm.local.AlarmDB
import kotlinx.android.synthetic.main.activity_repeating_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Month
import java.time.Year
import java.util.*
import kotlin.math.log

class OneTimeAlarmActivity : AppCompatActivity(), DateDialogFragment.DialogDateSetListener,
    TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding


    private val db by lazy { AlarmDB(this) }

    private var alarmService: AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater) // servant
        setContentView(binding.root) // antum

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {

            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DateDialogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerdialog")
            }
            btnAdd.setOnClickListener {
                val date = tvOnceDate.text.toString()
                val time = tvOnceTime.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (date != "date" && time != "time") {
                    alarmService?.setOneTimeAlarm(applicationContext, 0, date, time,message )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                message,
                                AlarmReceiver.TYPE_ONE_TIME
                            )
                        )
                        Log.i("AddAlarm", "alarm set on :$date $time with message $message")
                        finish()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_add_alarm),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

        }
    }
    override fun onDialogDateSet(view: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        // untuk mengubah tanggal calendar sekarang menjadi tanggal yang telah dipilih di
        calendar.set(year, month, dayOfMonth)
        val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOnceDate.text = dateFormatted.format(calendar.time)
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
    }
}