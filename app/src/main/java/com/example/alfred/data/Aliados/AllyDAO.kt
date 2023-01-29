package com.example.alfred.data.Aliados
import android.util.Log
import com.example.alfred.data.model.Ally
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.alfred.data.model.Car
import com.example.alfred.data.model.CurrentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AllyDAO {
    private val db = Firebase.firestore
    private val collectionName = "allies"
    private val allyList = mutableListOf<Ally>()
    private var ally = Ally("", "", "", "", "", false)

    private val allies = MutableLiveData<List<Ally>>()
    private val allyLive = MutableLiveData<Ally>()

    init {
        allies.value = allyList
        allyLive.value = ally
    }

    companion object {
        fun newInstance() = AllyDAO()
    }

    suspend fun getAllAllysAsync(): MutableList<Ally> = withContext(Dispatchers.IO) {
        return@withContext try {
            val data = db.collection(collectionName)
                .get()
                .await()
            Log.d("DATA",data.documents.toString())
            allyList.clear()
            for (document in data.documents) {
                allyList.add(
                    Ally(
                        document.id,
                        document.data!!.getValue("name") as String,
                        document.data!!.getValue("city") as String,
                        document.data!!.getValue("address") as String,
                        document.data!!.getValue("phoneNumber") as String,
                        document.data!!.getValue("favorite") as Boolean

                    )
                )

            }
            allyList
        } catch (e: Exception) {
            allyList
        }
    }
    suspend fun getAllyByIdAsync(allyId: String): Ally = withContext(Dispatchers.IO)
    {
        return@withContext try {
            val document = db.collection(collectionName).document(allyId)
                .get().await()


            ally = Ally(
                document.id,
                document.data!!.getValue("name") as String,
                document.data!!.getValue("city") as String,
                document.data!!.getValue("address") as String,
                document.data!!.getValue("phoneNumber") as String,
                document.data!!.getValue("favorite") as Boolean

                )
            ally
        } catch (e: Exception) {
            println("Exception ocurred:" + e)
            ally
        }
    }

    suspend fun updateAlly(ally: Ally) = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("Id aliado", ally.id)

            db.collection(collectionName).document(ally.id).update("favorite", ally.favorite)

        } catch (e: Exception) {
            Log.d("allyUpdate",e.stackTraceToString())
        }
    }


    suspend fun getAllies(): LiveData<List<Ally>> {
        getAllAllysAsync()
        for(i in allyList)
        {
            println(i.name)
        }
        return allies
    }
    suspend fun createAlly(ally: Ally){
        db.collection(collectionName).add(ally)
    }


}