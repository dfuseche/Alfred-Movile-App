package com.example.trapp.data
import android.content.Context
import androidx.room.*
import com.example.alfred.data.Aliados.AllyRoomDao
import com.example.alfred.data.car.CarLocalDAO
import com.example.alfred.data.car.CarNotifLocalDAO
import com.example.alfred.data.current_service.CurrentServiceRoomDao
import com.example.alfred.data.model.Car
import com.example.alfred.data.model.CarNotif
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.CurrentService
import com.example.alfred.utilities.Converters

@Database(entities = arrayOf( Car:: class, CurrentService::class, Ally::class, CarNotif::class), version = 1)
@TypeConverters(Converters:: class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carDAO(): CarLocalDAO
    abstract fun carNotifDAO(): CarNotifLocalDAO
    abstract fun currentServiceDAO(): CurrentServiceRoomDao
    abstract fun allyDAO(): AllyRoomDao


    companion object {
        val dbName = "room_database"
        @Volatile
        private var instance: AppDatabase? = null
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: create(context).also { instance = it }
            }

        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                dbName
            ).build()
        }
    }
}
