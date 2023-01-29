package com.example.alfred.data.service_client

import com.example.alfred.data.model.ServiceClient

class ServiceClientRepository private constructor(private val ServiceClientDAO: ServiceClientDAO)  {

    suspend fun getServiceClients() = ServiceClientDAO.getServiceClients()
    suspend fun getAllServiceClients() = ServiceClientDAO.getAllserviceClients()
    suspend fun createServiceClient(ServiceClient: ServiceClient)= ServiceClientDAO.createServiceClient(ServiceClient)
    companion object {
        @Volatile
        private var instance: ServiceClientRepository? = null

        fun getInstance(ServiceClientDAO: ServiceClientDAO) =
            instance ?: synchronized(this) {
                instance ?: ServiceClientRepository(ServiceClientDAO).also { instance = it }
            }
    }

}
