package com.example.alfred.UI.viewmodel.ServiceClient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alfred.data.service_client.ServiceClientRepository


class ServiceClientViewModelFactory(private val ServiceClientRepository: ServiceClientRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        return ServiceClientViewModel(ServiceClientRepository) as T
    }
}