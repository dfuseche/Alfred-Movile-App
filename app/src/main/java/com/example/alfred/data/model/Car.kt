package com.example.alfred.data.model
import androidx.room.*
import com.google.firebase.Timestamp

@Entity(primaryKeys = ["idCar","user"])
data class Car(
    @ColumnInfo(name="idCar") val idCar: Long,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="plate") val plate: String,
    @ColumnInfo(name="soat_date") val soat_date: Timestamp?,
    @ColumnInfo(name="model") val model: String,
    @ColumnInfo(name="country") val country: String,
    @ColumnInfo(name="city") val city: String,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="user") val user: String

)