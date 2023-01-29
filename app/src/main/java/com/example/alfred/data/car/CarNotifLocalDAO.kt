package com.example.alfred.data.car

import androidx.room.*
import com.example.alfred.data.model.CarNotif

@Dao
interface CarNotifLocalDAO {
    @Query("SELECT * FROM carNotif")
    fun getAll(): List<CarNotif>

    @Query("SELECT * FROM carNotif WHERE (:userStr) == user and idCar IN (:carId) LIMIT 1")
    fun findByIdAndUser(carId: Long, userStr: String): CarNotif

    @Query("SELECT * FROM carNotif WHERE (:userStr) == user")
    fun findByUser(userStr: String): List<CarNotif>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cars: CarNotif)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(car: CarNotif)


    @Delete
    fun delete(user: CarNotif)
}