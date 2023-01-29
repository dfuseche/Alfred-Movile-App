package com.example.alfred.UI.viewmodel.car

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alfred.data.car.CarNotifRepository
import com.example.alfred.data.car.CarRepository

class CarViewModelFactory (private val carRepository: CarRepository, private val carNotifRepository: CarNotifRepository): ViewModelProvider.NewInstanceFactory() {


    override fun <T: ViewModel > create(modelClass: Class<T>):T{
        return CarViewModel(carRepository, carNotifRepository) as T
    }

}
