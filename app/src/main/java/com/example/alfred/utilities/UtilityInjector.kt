package com.example.alfred.utilities
import android.content.Context
import com.example.alfred.UI.viewmodel.CurrentService.CurrentServiceViewModelFactory
import com.example.alfred.UI.viewmodel.ServiceClient.ServiceClientViewModelFactory
import com.example.alfred.UI.viewmodel.ally.AllyViewModelFactory
import com.example.alfred.UI.viewmodel.service.ServicesViewModelFactory
import com.example.alfred.UI.viewmodel.user.UserViewModelFactory
import com.example.alfred.data.Database
import com.example.alfred.data.service.ServiceRepository
import com.example.alfred.data.user.UserRepository
import com.example.alfred.UI.viewmodel.car.CarViewModelFactory
import com.example.alfred.data.Aliados.AllyRepository
import com.example.alfred.data.car.CarNotifRepository
import com.example.alfred.data.car.CarRepository
import com.example.alfred.data.current_service.CurrentServiceRepository
import com.example.alfred.data.service_client.ServiceClientRepository
import com.example.trapp.data.AppDatabase

object UtilityInjector {
    fun provideServiceViewModelFactory(context: Context): ServicesViewModelFactory {
        val serviceRepository = ServiceRepository.getInstance(Database.getInstance().serviceDAO)
        return ServicesViewModelFactory(serviceRepository)
    }

    fun provideUserViewModelFactory(context: Context): UserViewModelFactory {
        val userRepository = UserRepository.getInstance(Database.getInstance().userDAO)
        return UserViewModelFactory(userRepository)
    }
    fun provideCarViewModelFactory(context: Context): CarViewModelFactory {
        val carRepository = CarRepository.getInstance(Database.getInstance().carDAO,AppDatabase.getInstance(context).carDAO())
        val carNotifRepository = CarNotifRepository.getInstance(AppDatabase.getInstance(context).carNotifDAO())
        return CarViewModelFactory(carRepository, carNotifRepository)
    }

    fun provideAllyViewModelFactory(context: Context): AllyViewModelFactory {
        val allyRepository = AllyRepository.getInstance(Database.getInstance().allyDAO, AppDatabase.getInstance(context).allyDAO())
        return AllyViewModelFactory(allyRepository)
    }
    fun provideServiceClientViewModelFactory(context: Context): ServiceClientViewModelFactory {
        val serviceClientRepository = ServiceClientRepository.getInstance(Database.getInstance().serviceClientDAO)
        return ServiceClientViewModelFactory(serviceClientRepository)
    }
    fun provideCurrentServiceViewModelFactory(context: Context): CurrentServiceViewModelFactory {
        val currentServiceRepository = CurrentServiceRepository.getInstance(Database.getInstance().currentServiceDAO,AppDatabase.getInstance(context).currentServiceDAO())
        return CurrentServiceViewModelFactory(currentServiceRepository)
    }

}



