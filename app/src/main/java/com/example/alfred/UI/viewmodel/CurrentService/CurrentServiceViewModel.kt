package com.example.alfred.UI.viewmodel.CurrentService

import androidx.lifecycle.ViewModel
import com.example.alfred.data.current_service.CurrentServiceRepository
import com.example.alfred.data.model.CurrentService
import com.example.alfred.data.service.ServiceRepository

class CurrentServiceViewModel  (private val currentServiceRepository: CurrentServiceRepository) : ViewModel(){

    suspend fun getServices()=currentServiceRepository.getCurrentServices()
    suspend fun getAllServicesAsync()=currentServiceRepository.getAllCurrentServicesAsync()
    suspend fun getServiceById(ServiceId: String)=currentServiceRepository.getCurrentServicerById(ServiceId)
    suspend fun createCurrentService(currentService: CurrentService) = currentServiceRepository.createCurrentService(currentService)
    suspend fun getCurrentServiceAsyncByUserId(userId: String) = currentServiceRepository.getCurrentServiceAsyncByUserId(userId)
}