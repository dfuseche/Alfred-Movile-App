package com.example.alfred.data.car
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.CarNotif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarNotifRepository private constructor(private val carNotifLocalDAO: CarNotifLocalDAO)  {


    suspend fun getCarsNotifByUserId(userId:String): LiveData<List<CarNotif>> {
            return MutableLiveData(withContext(Dispatchers.IO){carNotifLocalDAO.findByUser(userId)})

    }
    suspend fun createCarNotif(carNotif:CarNotif) {
        withContext(Dispatchers.IO){carNotifLocalDAO.insert(carNotif)}}


    companion object {
        @Volatile
        private var instance: CarNotifRepository? = null

        fun getInstance(carNotifLocalDAO: CarNotifLocalDAO) =
            instance ?: synchronized(this) {
                instance ?: CarNotifRepository(carNotifLocalDAO).also { instance = it }
            }
    }

}
