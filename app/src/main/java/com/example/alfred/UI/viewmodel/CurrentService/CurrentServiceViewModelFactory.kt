package com.example.alfred.UI.viewmodel.CurrentService

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alfred.UI.viewmodel.service.ServicesViewModel
import com.example.alfred.data.current_service.CurrentServiceRepository
import com.example.alfred.data.service.ServiceRepository

class CurrentServiceViewModelFactory (private val currentServiceRepository: CurrentServiceRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        return CurrentServiceViewModel(currentServiceRepository) as T
    }

}