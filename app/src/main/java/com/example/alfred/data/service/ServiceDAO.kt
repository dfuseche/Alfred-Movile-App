package com.example.alfred.data.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.Ally
import com.example.alfred.data.model.Service
import com.example.alfred.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ServiceDAO {

    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "servicios"
    private val serviceList = mutableListOf<Service>()
    private val services = MutableLiveData<List<Service>>()
    private val allyServices = MutableLiveData<List<Service>>()
    private val allyServicesList = mutableListOf<Service>()
    private var service = Service("", "","","",0,"")
    private val serviceLive = MutableLiveData<Service>()

    init {
        services.value = serviceList
        serviceLive.value = service
        allyServices.value= allyServicesList
    }
    companion object {
        fun newInstance() = ServiceDAO()
    }

    suspend fun getAllServicesAsync(): MutableList<Service> = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = db.collection(collectionName)
                .get()
                .await()

            for (document in data.documents) {

                serviceList.add(Service(
                    document.id,
                    document.data!!.getValue("category") as String,
                    document.data!!.getValue("description") as String,
                    document.data!!.getValue("name") as String,
                    document.data!!.getValue("price") as Int,
                    document.data!!.getValue("idAlly") as String,
                    document.data!!.getValue("duration") as String

                    ))

            }
            serviceList
        } catch (e: Exception) {
            serviceList
        }

    }
    suspend fun updateService(service: Service) = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = db.collection(collectionName).whereEqualTo("id",service.id).get().await()
            Log.d("DATA",data.documents.toString())

            for (document in data.documents) {
                Log.d("doc",document.data.toString())
                db.collection(collectionName).document(document.id).set(service)
            }
        } catch (e: Exception) {
            Log.d("ServiceUpdateCatch",e.stackTraceToString())
        }
    }
    suspend fun getServiceByIdAsync(serviceId: String): Service = withContext(Dispatchers.IO)
    {
        return@withContext try {
            val document = db.collection(collectionName).document(serviceId)
                .get().await()


            service = document.toObject(Service::class.java)!!
            println("EL SERVICIO QUE SE ENCONTRO ES"+ service.name)
            service
        } catch (e: Exception) {
            println("Exception ocurred:" + e)
            service
        }
    }
    suspend fun getServiceAsyncByUserId(allyId: String): MutableList<Service> = withContext(Dispatchers.IO) {
        return@withContext try {

            val data = db.collection(collectionName).whereEqualTo("idAlly", allyId)
                .get()
                .await()
            Log.d("DATA",data.documents.toString())
            println(" "+allyId)
            allyServicesList.clear()
            for (document in data.documents) {
                var service = document.toObject(Service::class.java)!!
                allyServicesList.add(service)


            }
            allyServicesList
        } catch (e: Exception) {
            allyServicesList
        }

    }
    suspend fun getServices(): LiveData<List<Service>> {
        getAllServicesAsync()
        return services
    }
    suspend fun  getServicesByAllyId(allyId: String):LiveData<List<Service>> {
        getServiceAsyncByUserId(allyId)
        println("entroooooooooooo")
        for(i in allyServicesList){
            println(i.name + i.idAlly +"hola")
        }
        return allyServices
    }

    suspend fun getServiceById(serviceId: String): LiveData<Service> {
        getServiceByIdAsync(serviceId)
        serviceLive.value = service

        return serviceLive
    }

    suspend fun createService(service :Service){
        db.collection(collectionName).add(service)
    }


}