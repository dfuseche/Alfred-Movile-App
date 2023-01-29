package com.example.alfred.UI.viewmodel.ServiceClient

import androidx.lifecycle.ViewModel
import com.example.alfred.data.model.ServiceClient
import com.example.alfred.data.service_client.ServiceClientRepository

class ServiceClientViewModel (private val ServiceClientRepository: ServiceClientRepository) : ViewModel() {

    suspend fun getServiceClient()=ServiceClientRepository.getServiceClients()
    suspend fun getServiceClientByUserId(userId: String)= ServiceClientRepository.getAllServiceClients()
    suspend fun createServiceClient(car: ServiceClient)=ServiceClientRepository.createServiceClient(car)

}