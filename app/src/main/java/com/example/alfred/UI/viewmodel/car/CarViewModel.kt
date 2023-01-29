package com.example.alfred.UI.viewmodel.car

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alfred.data.car.CarNotifRepository
import com.example.alfred.data.model.Car
import com.example.alfred.data.car.CarRepository
import com.example.alfred.data.model.CarNotif

class CarViewModel (private val CarRepository: CarRepository, private val CarNotifRepository: CarNotifRepository) : ViewModel(){
    suspend fun getCarsByUserId(userId: String,context: Context)=CarRepository.getCarsByUserId(userId, context)
    suspend fun createCar(car: Car,context: Context)=CarRepository.createCar(car, context)
    suspend fun createCarNotif(carNotif: CarNotif)=CarNotifRepository.createCarNotif(carNotif)
    suspend fun getCarsNotifByUserId(userId: String)=CarNotifRepository.getCarsNotifByUserId(userId)

}
