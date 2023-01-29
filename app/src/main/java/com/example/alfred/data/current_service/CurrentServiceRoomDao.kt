package com.example.alfred.data.current_service

import androidx.room.*
import com.example.alfred.data.model.CurrentService


@Dao
interface CurrentServiceRoomDao {

    @Query("SELECT * FROM currentService")
    fun getAll(): List<CurrentService>

    @Query("SELECT * FROM currentService WHERE idUser IN (:userId)")
    fun loadAllByUserId(userId: String): List<CurrentService>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg trip: CurrentService)

    @Delete
    fun delete(currentService: CurrentService)

    @Query("DELETE FROM currentService")
    fun deleteAll()

}