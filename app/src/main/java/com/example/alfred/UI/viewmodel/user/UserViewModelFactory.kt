package com.example.alfred.UI.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alfred.data.user.UserRepository

class UserViewModelFactory (private val UserRepository: UserRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T: ViewModel > create(modelClass: Class<T>):T{
       return UserViewModel(UserRepository) as T
    }


}