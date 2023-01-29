package com.example.alfred.UI.viewmodel.service

import androidx.lifecycle.ViewModel
import com.example.alfred.data.model.User
import com.example.alfred.data.service.ServiceRepository


class ServicesViewModel (private val serviceRepository: ServiceRepository) : ViewModel(){

    suspend fun getServices()=serviceRepository.getServices()
    suspend fun getAllServicesAsync()=serviceRepository.getAllServicesAsync()
    suspend fun getServiceById(ServiceId: String)=serviceRepository.getServicerById(ServiceId)
    suspend fun getServicesByAllyId(allyId: String) = serviceRepository.getServicesByAllyId(allyId)

}