package com.example.alfred.data.cache

import com.example.alfred.data.model.Car
import javax.inject.Singleton

object CarMemCache {

    var carsList: MutableList<Car> = mutableListOf()

}