package com.example.alfred.UI.viewmodel.user

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alfred.data.model.User
import com.example.alfred.data.user.UserRepository

class UserViewModel (private val UserRepository: UserRepository) : ViewModel() {

    suspend fun getUsers()=UserRepository.getUsers()
    suspend fun getAllUsersAsync()=UserRepository.getAllUsersAsync()
    suspend fun getUserById(UserId: String)=UserRepository.getUserById(UserId)
    suspend fun createUser(user: User)=UserRepository.createUser(user)
    suspend fun updateUser(user:User)=UserRepository.updateUser(user)


}