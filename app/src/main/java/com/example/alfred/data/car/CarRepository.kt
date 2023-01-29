package com.example.alfred.data.car
import android.content.Context
import android.util.SparseArray
import android.util.SparseLongArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.Car
import com.example.trapp.utilities.NetworkTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarRepository private constructor(private val carRemoteDAO: CarRemoteDAO, private val carLocalDAO: CarLocalDAO)  {


    suspend fun getCarsByUserId(userId:String, context: Context): LiveData<List<Car>> {
        val tracker = NetworkTracker.getInstance()
        var remoteCars: LiveData<List<Car>>
        var localUserCars: List<Car>
        if(tracker.getInternet(context)){
        withContext(Dispatchers.IO){
            remoteCars=carRemoteDAO.getCarsByUserId(userId)
            val value=remoteCars.value
            var i =0
            while(i<value!!.size){
                carLocalDAO.insert(value[i])
                i++
            }
            remoteCars}

            return remoteCars
        }else{
            localUserCars= withContext(Dispatchers.IO){carLocalDAO.findByUser(userId)}
            return MutableLiveData(localUserCars)
        }


    }
    suspend fun createCar(car:Car, context: Context) {
        val tracker = NetworkTracker.getInstance()
        if(tracker.getInternet(context)){
            carRemoteDAO.createCar(car)
            carLocalDAO.insert(car)}}


    companion object {
        @Volatile
        private var instance: CarRepository? = null

        fun getInstance(carRemoteDAO: CarRemoteDAO, carLocalDAO: CarLocalDAO) =
            instance ?: synchronized(this) {
                instance ?: CarRepository(carRemoteDAO, carLocalDAO).also { instance = it }
            }
    }

}
