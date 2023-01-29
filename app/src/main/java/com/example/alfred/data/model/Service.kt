package com.example.alfred.data.model
import androidx.room.*
import com.google.firebase.Timestamp

@Entity
data class Service (
    @PrimaryKey var id: String?= null,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "price") val price: Int? = null,
    @ColumnInfo(name = "idAlly") val idAlly: String? = null,
    @ColumnInfo(name = "duration") val duration: String? = null
)
