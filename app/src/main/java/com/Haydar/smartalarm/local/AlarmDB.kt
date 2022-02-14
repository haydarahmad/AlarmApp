package com.Haydar.smartalarm.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.Haydar.smartalarm.data.Alarm
import java.util.concurrent.locks.Lock


@Database(entities = [Alarm::class], version = 2)
abstract class AlarmDB: RoomDatabase() {
    abstract fun alarmDao() : AlarmDao

    companion object{
        @Volatile
        var instance: AlarmDB? = null
        private val  LOCK = Any()

        operator fun invoke(context: Context)= instance?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        fun buildDatabase(context: Context)=
            Room.databaseBuilder(context,AlarmDB::class.java, "smart_alarm.db")
                .fallbackToDestructiveMigration()
                .build()

    }
}