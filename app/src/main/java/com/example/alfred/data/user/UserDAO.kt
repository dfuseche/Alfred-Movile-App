package com.example.alfred.data.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await


class UserDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "users"
    private val userList = mutableListOf<User>()
    private var user = User("nullId")

    private val users = MutableLiveData<List<User>>()
    private val userLive = MutableLiveData<User>()

    init {
        users.value = userList
        userLive.value = user
    }

    companion object {
        fun newInstance() = UserDAO()
    }


    suspend fun getAllusers(): MutableList<User> = withContext(Dispatchers.IO)
    {
        return@withContext try {
            val data = db.collection(collectionName)
                .get()
                .await()
            for (document in data.documents) {
                Log.d("doc",document.data.toString())
                userList.add(
                    User(
                        document.id,
                        document.data!!.getValue("cellphone") as String,
                        document.data!!.getValue("email") as String,
                        document.data!!.getValue("name") as String,

                    )
                )
            }
            userList
        } catch (e: Exception)
        {
            userList
        }

    }
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = db.collection(collectionName).whereEqualTo("id",user.uid).get().await()
            Log.d("DATA",data.documents.toString())

            for (document in data.documents) {
                Log.d("doc",document.data.toString())
                db.collection(collectionName).document(document.id).set(user)
            }
        } catch (e: Exception) {
            Log.d("UserUpdateCatch",e.stackTraceToString())
        }
    }


    suspend fun getuserByIdAsync(userId: String): User = withContext(Dispatchers.IO)
    {
        return@withContext try {


            val document = db.collection(collectionName).document(userId)
                .get().await()

            user = document.toObject(User::class.java)!!
            user.uid = document.id
            user
        } catch (e: Exception) {
            println("excepcion ----------------" + e)
            user
        }
    }

    suspend fun getUsers(): LiveData<List<User>> {
        getAllusers()
        return users
    }
    suspend fun getUserById(userId: String): LiveData<User> {
        getuserByIdAsync(userId)
        println("-------------------------------el mega user" + user)
        userLive.value = user
        return userLive
    }


    suspend fun createUser(user:User){
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val documentReference = db.collection("users").document(userId)
        documentReference.set(user)
    }

}