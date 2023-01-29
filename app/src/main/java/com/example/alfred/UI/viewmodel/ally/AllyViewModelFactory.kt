package com.example.alfred.UI.viewmodel.ally

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alfred.data.Aliados.AllyRepository

class AllyViewModelFactory (private val allyRepository: AllyRepository): ViewModelProvider.NewInstanceFactory(){

    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        return AllyViewModel(allyRepository) as T
    }
}