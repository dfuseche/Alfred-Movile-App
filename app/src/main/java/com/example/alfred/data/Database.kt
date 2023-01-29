package com.example.alfred.data
import com.example.alfred.data.Aliados.AllyDAO
import com.example.alfred.data.service.ServiceDAO
import com.example.alfred.data.user.UserDAO
import com.example.alfred.data.car.CarRemoteDAO
import com.example.alfred.data.current_service.CurrentServiceDAO
import com.example.alfred.data.service_client.ServiceClientDAO

class Database private constructor() {
    var serviceDAO = ServiceDAO()
        private set
    var userDAO = UserDAO()
        private set
    var carDAO = CarRemoteDAO()
        private set
    var allyDAO = AllyDAO()
        private set
    var serviceClientDAO = ServiceClientDAO()
        private set
    var currentServiceDAO = CurrentServiceDAO()
        private set

    companion object {
        @Volatile
        private var instance: Database? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: Database().also { instance = it }
            }
    }
}
