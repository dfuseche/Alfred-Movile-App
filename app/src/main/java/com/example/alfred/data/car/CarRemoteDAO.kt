package com.example.alfred.data.car


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.alfred.data.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import android.util.Log

class CarRemoteDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "cars"
    private val carList = mutableListOf<Car>()
    private val cars = MutableLiveData<List<Car>>()
    private val userCars = MutableLiveData<List<Car>>()
    private val userCarList = mutableListOf<Car>()
    private var car = Car( -1,"","",null,"","","","","")
    private val carLive = MutableLiveData<Car>()

    init {
        cars.value = carList
        carLive.value = car
        userCars.value= userCarList
    }
    companion object {
        fun newInstance() = CarRemoteDAO()
    }
    suspend fun getCarsAsyncByUserId(userId: String): MutableList<Car> = withContext(Dispatchers.IO) {
        return@withContext try {
            userCarList.removeAll(userCarList)
            val data = db.collection(collectionName).whereEqualTo("user", userId)
                .get()
                .await()
            Log.d("DATA",data.documents.indices.toString())
            var range= data.documents.indices
            var documents = data.documents
            for(i in range){
                userCarList.add(
                    Car(
                        documents[i].data!!.getValue("idCar") as Long,
                        documents[i].data!!.getValue("name") as String,
                        documents[i].data!!.getValue("plate") as String,
                        documents[i].data!!.getValue("soat_date") as Timestamp,
                        documents[i].data!!.getValue("model") as String,
                        documents[i].data!!.getValue("country") as String,
                        documents[i].data!!.getValue("city") as String,
                        documents[i].data!!.getValue("description") as String,
                        documents[i].data!!.getValue("user") as String
                    )
                )
            }
            Log.d("CarListDAO", carList.toString())
            userCarList
        } catch (e: Exception) {
            val stack=e.stackTraceToString()
            println(stack)
            userCarList
        }

    }
    suspend fun  getCarsByUserId(userId: String):LiveData<List<Car>> {
        getCarsAsyncByUserId(userId)
        return userCars
    }

    fun createCar(car:Car){
        db.collection(collectionName).add(car)
    }




}



