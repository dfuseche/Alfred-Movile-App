package com.example.alfred.UI.viewmodel.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alfred.data.service.ServiceRepository


class ServicesViewModelFactory (private val serviceRepository: ServiceRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T: ViewModel > create(modelClass: Class<T>):T{
        return ServicesViewModel(serviceRepository) as T
    }


}