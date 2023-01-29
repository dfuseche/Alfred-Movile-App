package com.example.alfred.data.current_service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.CurrentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CurrentServiceRepository constructor(private val CurrentServiceDAO: CurrentServiceDAO, private val currentServiceRoomDao: CurrentServiceRoomDao) {
    suspend fun getCurrentServices() = CurrentServiceDAO.getCurrentServices()
    suspend fun getAllCurrentServicesAsync() = CurrentServiceDAO.getAllCurrentServicesAsync()
    suspend fun getCurrentServicerById(ServiceId: String) = CurrentServiceDAO.getCurrentServiceById(ServiceId)

    suspend fun getCurrentServiceAsyncByUserId(userId:String): LiveData<List<CurrentService>>{

        val currentServicesRoomLive = MutableLiveData<List<CurrentService>>()

        val currentServiceRoom = withContext(Dispatchers.IO) { currentServiceRoomDao.loadAllByUserId(userId) }


        if(currentServiceRoom.isNotEmpty())
        {

            currentServicesRoomLive.value =currentServiceRoom

            println("Valores del ROOM")

            return currentServicesRoomLive
        }

        val currentServicesUser = CurrentServiceDAO.getCurrentServicesByUserId(userId)

        withContext(Dispatchers.IO) { currentServiceRoomDao.deleteAll() }

        for (service in currentServicesUser.value!!) {
            withContext(Dispatchers.IO) { currentServiceRoomDao.insertAll(service) }
        }


        return currentServicesUser
    }
    suspend fun createCurrentService(currentService: CurrentService) {
        CurrentServiceDAO.createCurrentService(currentService)
        withContext(Dispatchers.IO) { currentServiceRoomDao.deleteAll() }

    }


    companion object {
        @Volatile
        private var instance: CurrentServiceRepository? = null

        fun getInstance(currentServiceDAO: CurrentServiceDAO, currentServiceRoomDao: CurrentServiceRoomDao) =
            instance ?: synchronized(this) {
                instance ?: CurrentServiceRepository(currentServiceDAO, currentServiceRoomDao).also { instance = it }
            }
    }

}