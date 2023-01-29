package com.example.alfred.data.current_service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.CurrentService
import com.example.alfred.data.model.Service
import com.example.alfred.data.service.ServiceDAO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class CurrentServiceDAO {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "current_service"
    private val currentServiceList = mutableListOf<CurrentService>()
    private val currentServices = MutableLiveData<List<CurrentService>>()
    private val allyCurrentServices = MutableLiveData<List<CurrentService>>()
    private val allyCurrentServicesList = mutableListOf<CurrentService>()

    private var currentService = CurrentService("", "","","","","",false,"","")
    private val currentServiceLive = MutableLiveData<CurrentService>()

    init {
        currentServices.value = currentServiceList
        currentServiceLive.value = currentService
        allyCurrentServices.value= allyCurrentServicesList
    }
    companion object {
        fun newInstance() = ServiceDAO()
    }

    suspend fun getAllCurrentServicesAsync(): MutableList<CurrentService> = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = db.collection(collectionName)
                .get()
                .await()
            currentServiceList.clear()
            for (document in data.documents) {
                currentServiceList.add(
                    CurrentService(
                        document.id,
                        document.data!!.getValue("idService") as String,
                        document.data!!.getValue("idUser") as String,
                        document.data!!.getValue("idCar") as String,
                        document.data!!.getValue("pickupAddress") as String,
                        document.data!!.getValue("deliveryAddress") as String,
                        document.data!!.getValue("driver") as Boolean,
                        document.data!!.getValue("state") as String,
                        document.data!!.getValue("date") as String,
                    )
                )


            }
            currentServiceList
        } catch (e: Exception) {
            currentServiceList
        }

    }
    suspend fun updateCurrentService(currentService: CurrentService) = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = db.collection(collectionName).whereEqualTo("id",currentService.id).get().await()
            Log.d("DATA",data.documents.toString())

            for (document in data.documents) {
                Log.d("doc",document.data.toString())
                db.collection(collectionName).document(document.id).set(currentService)
            }
        } catch (e: Exception) {
            Log.d("ServiceUpdateCatch",e.stackTraceToString())
        }
    }
    suspend fun getCurrentServiceByIdAsync(currentServiceId: String): CurrentService = withContext(Dispatchers.IO)
    {
        return@withContext try {
            val document = db.collection(collectionName).document(currentServiceId)
                .get().await()


            currentService = document.toObject(CurrentService::class.java)!!
            println("EL SERVICIO QUE SE ENCONTRO ES"+ currentService.id)
            currentService
        } catch (e: Exception) {
            println("Exception ocurred:" + e)
            currentService
        }
    }
    suspend fun getCurrentServiceAsyncByUserId(userId: String): MutableList<CurrentService> = withContext(
        Dispatchers.IO) {
        return@withContext try {

            val data = db.collection(collectionName).whereEqualTo("idUser", userId)
                .get()
                .await()
            Log.d("DATA",data.documents.toString())
            allyCurrentServicesList.clear()

            for (document in data.documents) {
                allyCurrentServicesList.add(
                    CurrentService(
                        document.id,
                        document.data!!.getValue("idService") as String,
                        document.data!!.getValue("idUser") as String,
                        document.data!!.getValue("idCar") as String,
                        document.data!!.getValue("pickupAddress") as String,
                        document.data!!.getValue("deliveryAddress") as String,
                        document.data!!.getValue("driver") as Boolean,
                        document.data!!.getValue("state") as String,
                        document.data!!.getValue("date") as String,
                    )
                )
            }
            allyCurrentServicesList
        } catch (e: Exception) {
            allyCurrentServicesList
        }

    }


    suspend fun getCurrentServices(): LiveData<List<CurrentService>> {
        getAllCurrentServicesAsync()
        for(i in currentServiceList){
            println(i.deliveryAddress+i.idService)
        }
        return currentServices
    }
    suspend fun  getCurrentServicesByUserId(allyId: String): LiveData<List<CurrentService>> {
        getCurrentServiceAsyncByUserId(allyId)

        return allyCurrentServices
    }

    suspend fun getCurrentServiceById(currentServiceId: String): LiveData<CurrentService> {
        getCurrentServiceByIdAsync(currentServiceId)
        currentServiceLive.value = currentService

        return currentServiceLive
    }

    suspend fun createCurrentService(currentService : CurrentService){
        db.collection(collectionName).add(currentService)
    }


}