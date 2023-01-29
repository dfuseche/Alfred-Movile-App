package com.example.alfred.data.service

import com.example.alfred.data.model.User
import com.example.alfred.data.user.UserDAO
import com.example.alfred.data.user.UserRepository

class ServiceRepository constructor(private val ServiceDAO: ServiceDAO) {

    suspend fun getServices() = ServiceDAO.getServices()
    suspend fun getAllServicesAsync() = ServiceDAO.getAllServicesAsync()
    suspend fun getServicerById(ServiceId: String) = ServiceDAO.getServiceById(ServiceId)
    suspend fun getServicesByAllyId(allyId: String) = ServiceDAO.getServicesByAllyId(allyId)


    companion object {
        @Volatile
        private var instance: ServiceRepository? = null

        fun getInstance(ServiceDAO: ServiceDAO) =
            instance ?: synchronized(this) {
                instance ?: ServiceRepository(ServiceDAO()).also { instance = it }
            }
    }
}