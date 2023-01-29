package com.example.alfred.data.user

import com.example.alfred.data.model.User

class UserRepository private constructor(private val UserDAO: UserDAO)  {

    suspend fun getUsers() = UserDAO.getUsers()
    suspend fun getAllUsersAsync() = UserDAO.getAllusers()
    suspend fun getUserById(UserId: String) = UserDAO.getUserById(UserId)
    suspend fun createUser(User: User)= UserDAO.createUser(User)
    suspend fun updateUser(user: User)=UserDAO.updateUser(user)

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(UserDAO: UserDAO) =
            instance ?: synchronized(this) {
                instance ?: UserRepository(UserDAO).also { instance = it }
            }
    }

}