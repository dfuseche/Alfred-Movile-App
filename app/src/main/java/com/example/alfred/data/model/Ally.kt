package com.example.alfred.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Ally (
    @PrimaryKey val id: String,
    @ColumnInfo(name="name") val name: String? = null,
    @ColumnInfo(name="city")val city: String? = null,
    @ColumnInfo(name="address")val address: String? = null,
    @ColumnInfo(name="phoneNumber")val phoneNumber: String? = null,
    @ColumnInfo(name="favorite") var favorite: Boolean
)