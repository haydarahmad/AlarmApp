package com.Haydar.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Haydar.smartalarm.adapter.AlarmAdapter
import com.Haydar.smartalarm.data.Alarm
import com.Haydar.smartalarm.databinding.ActivityMainBinding
import com.Haydar.smartalarm.local.AlarmDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding
    private var alarmAdapter: AlarmAdapter? = null

    private var  alarmService : AlarmReceiver ? = null
    private val db by lazy { AlarmDB(this) }

    override fun onResume() {
        super.onResume()

        db.alarmDao().getAlarm().observe(this){
            alarmAdapter?.setData(it)
            Log.i("GetAlarm", "setUpRecyclerView: with this data $it")
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val alarm = db.alarmDao().getAlarm()
//            withContext(Dispatchers.Main){
//                alarmAdapter?.setData(alarm)
//            }
//            Log.i("GetAlarm", "setUpRecyclerView: with this data $alarm")
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        setupRecleyclerView()
    }
    private fun setupRecleyclerView() {
        binding.apply {
        alarmAdapter = AlarmAdapter()
            rvReminderAlarm.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = alarmAdapter
            }
            swipeToDelete(rvReminderAlarm)
        }
    }

    private fun initView() {
        binding.apply {
            cvSetOneTimeAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, OneTimeAlarmActivity::class.java))
            }

            cvSetRepeatingAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }

        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                    deletedItem?.let { db.alarmDao().deleteAlarm(it) }
                }
//                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
                deletedItem?.type?.let { alarmService?.cancelAlarm(applicationContext,it)}
            }
        }
        ).attachToRecyclerView(recyclerView)
    }




//    private fun iniDateToday() {
//        val calendar = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("E,dd MMM yyyy", Locale.getDefault())
//        val formattedDate = dateFormat.format(calendar.time)
//
//        binding.tvDateToday.text  = formattedDate
//    }
//
//    private fun initTimeToday() {
//    // calender buat segala hal yang berhubungan dengan waktu android
//        val calendar = Calendar.getInstance()
//    // menentukan format jam yang akan digunakan
//        val timeFormat = SimpleDateFormat("hh:mm aa",Locale.getDefault())
//        val formattedTime = timeFormat.format(calendar.time)
//
//        binding.tvTimeToday.text = formattedTime
//    }

}