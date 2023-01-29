package com.example.alfred.data.model
import androidx.room.*
import com.google.firebase.Timestamp

@Entity(primaryKeys = ["idCar","user"])
data class CarNotif(
    @ColumnInfo(name="idCar") val idCar: Long,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="plate") val plate: String,
    @ColumnInfo(name="soat_date") val soat_date: Timestamp?,
    @ColumnInfo(name="user") val user: String
)