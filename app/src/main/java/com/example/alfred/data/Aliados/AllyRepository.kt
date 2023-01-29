package com.example.alfred.data.Aliados

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.current_service.CurrentServiceRoomDao
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.CurrentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AllyRepository private constructor(private val AllyDAO: AllyDAO, private val allyRoomDao: AllyRoomDao) {
    suspend fun getAllies():LiveData<List<Ally>> {

        val allies = AllyDAO.getAllies()

        for (ally in allies.value!!) {
            if(ally.favorite)
            withContext(Dispatchers.IO) {allyRoomDao.insertAlly(ally) }
        }

        return allies

    }
    suspend fun getAlliesById(allyId:String) = AllyDAO.getAllyByIdAsync(allyId)
    suspend fun createAlly(ally: Ally) = AllyDAO.createAlly(ally)
    suspend fun updateAlly(ally: Ally) = AllyDAO.updateAlly(ally)
    suspend fun addToFavorites(ally: Ally): Boolean {


        val allyRoom = withContext(Dispatchers.IO) { allyRoomDao.getAll() }


        if(allyRoom.size < 2)
        {
            withContext(Dispatchers.IO) { allyRoomDao.insertAlly(ally) }
            println("Aliado con id: " + ally.id +" ha diso agregado")
            return true;

        }
        else {
            return false;
        }
    }
    suspend fun removeFromFavorites(ally: Ally) {


        val allyRoom = withContext(Dispatchers.IO) { allyRoomDao.deleteAlly(ally) }



    }


    suspend fun getFavorites(): LiveData<List<Ally>> {
        val alliesRoomLive = MutableLiveData<List<Ally>>()
        val allyRoom = withContext(Dispatchers.IO) { allyRoomDao.getAll() }

        alliesRoomLive.value = allyRoom

        return alliesRoomLive
    }


    companion object {
        @Volatile
        private var instance: AllyRepository?  = null

        fun getInstance(AllyDAO: AllyDAO, allyRoomDao: AllyRoomDao) =
            instance ?: synchronized(this) {
                instance ?: AllyRepository(AllyDAO, allyRoomDao).also { instance = it }
            }
    }
}