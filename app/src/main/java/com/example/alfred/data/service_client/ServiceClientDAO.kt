package com.example.alfred.data.service_client

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.alfred.data.model.ServiceClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ServiceClientDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "services_clients"
    private val serviceClientList = mutableListOf<ServiceClient>()
    private var serviceClient = ServiceClient()

    private val serviceClients = MutableLiveData<List<ServiceClient>>()
    private val serviceClientLive = MutableLiveData<ServiceClient>()

    init {
        serviceClients.value = serviceClientList
        serviceClientLive.value = serviceClient
    }

    companion object {
        fun newInstance() = ServiceClientDAO()
    }


    suspend fun getAllserviceClients(): MutableList<ServiceClient> = withContext(Dispatchers.IO)
    {
        return@withContext try {
            val data = db.collection(collectionName)
                .get()
                .await()
            for (document in data.documents) {
                Log.d("doc",document.data.toString())
                serviceClientList.add(
                    ServiceClient(
                        document.data!!.getValue("amount") as String,
                        document.data!!.getValue("idUser") as String,
                        document.data!!.getValue("servicetype") as String,

                        )
                )
            }
            serviceClientList
        } catch (e: Exception)
        {
            serviceClientList
        }

    }



//    suspend fun getserviceClientByIdAsync(serviceClientId: String): ServiceClient = withContext(Dispatchers.IO)
//    {
//        return@withContext try {
//            val document = db.collection(collectionName).document(serviceClientId)
//                .get().await()
//
//            serviceClient = document.toObject(ServiceClient::class.java)!!
//            serviceClient.idUser = document.id
//
//
//            serviceClient
//        } catch (e: Exception) {
//            println("excepcion ----------------" + e)
//            serviceClient
//        }
//    }

    suspend fun getServiceClients(): LiveData<List<ServiceClient>> {
        getAllserviceClients()
        return serviceClients
    }
//    suspend fun getServiceClientById(serviceClientId: String): LiveData<ServiceClient> {
//        getserviceClientByIdAsync(serviceClientId)
//        println("-------------------------------el mega serviceClient" + serviceClient)
//        serviceClientLive.value = serviceClient
//        return serviceClientLive
//    }


    suspend fun createServiceClient(serviceClient: ServiceClient){
        db.collection(collectionName).add(serviceClient)
    }
}