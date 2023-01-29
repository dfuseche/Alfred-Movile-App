package com.example.alfred.data.model

import androidx.room.ColumnInfo

class ServiceClient (

    @ColumnInfo(name = "amount")var amount: String? = null,
    @ColumnInfo(name = "idUser")var idUser: String? = null,
    @ColumnInfo(name = "servicetype")var servicetype: String? = null
    ) : Comparable<ServiceClient> {
    override fun compareTo(other: ServiceClient): Int {
        if(this.servicetype!! > other.servicetype.toString()) return 1
        if(this.servicetype!! < other.servicetype.toString()) return -1
        return 0
    }
}
