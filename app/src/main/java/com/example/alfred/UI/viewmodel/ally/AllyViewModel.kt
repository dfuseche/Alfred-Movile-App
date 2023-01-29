package com.example.alfred.UI.viewmodel.ally

import androidx.lifecycle.ViewModel
import com.example.alfred.data.Aliados.AllyRepository
import com.example.alfred.data.model.Ally

class AllyViewModel (private val AllyRepository: AllyRepository) : ViewModel(){
    suspend fun getAllies() = AllyRepository.getAllies()
    suspend fun getAlliesById(allyId:String) = AllyRepository.getAlliesById(allyId)
    suspend fun createAlly(ally: Ally) = AllyRepository.createAlly(ally)
    suspend fun addToFavorites(ally: Ally) = AllyRepository.addToFavorites(ally)
    suspend fun getFavorites()= AllyRepository.getFavorites()
    suspend fun updateAlly(ally: Ally) = AllyRepository.updateAlly(ally)
    suspend fun removeFromFavorites(ally: Ally) = AllyRepository.removeFromFavorites(ally)
}