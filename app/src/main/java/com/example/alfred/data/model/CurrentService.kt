package com.example.alfred.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime;

@Entity()
data class CurrentService (
    @PrimaryKey val id: String,
    @ColumnInfo(name="idService") val idService: String,
    @ColumnInfo(name="idUser") val idUser: String,
    @ColumnInfo(name="idCar") val idCar: String,
    @ColumnInfo(name="pickupAddress") val pickupAddress: String,
    @ColumnInfo(name="deliveryAddress") val deliveryAddress: String,
    @ColumnInfo(name="driver") val driver: Boolean,
    @ColumnInfo(name="state") val state: String,
    @ColumnInfo(name="date") val date: String,

    )
