package com.example.alfred.data.Aliados

import androidx.room.*
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.CurrentService

@Dao
interface AllyRoomDao {

    @Query("SELECT * FROM Ally")
    fun getAll(): List<Ally>

    @Query("SELECT * FROM Ally WHERE id IN (:allyId)")
    fun loadAllyById(allyId: String): List<Ally>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlly(aliado: Ally)

    @Delete
    fun deleteAlly(ally: Ally)

    @Query("DELETE FROM Ally")
    fun deleteAll()
}