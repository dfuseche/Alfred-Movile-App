package com.example.alfred.data.car

import androidx.room.*
import com.example.alfred.data.model.Car

@Dao
interface CarLocalDAO {
    @Query("SELECT * FROM car")
    fun getAll(): List<Car>

    @Query("SELECT * FROM car WHERE (:userStr) == user and idCar IN (:carId) LIMIT 1")
    fun findByIdAndUser(carId: Long, userStr: String): Car

    @Query("SELECT * FROM car WHERE (:userStr) == user")
    fun findByUser(userStr: String): List<Car>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg cars: Car)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(car: Car)


    @Delete
    fun delete(car: Car)
}