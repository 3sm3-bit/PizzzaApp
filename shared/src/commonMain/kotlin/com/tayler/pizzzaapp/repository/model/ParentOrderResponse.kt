package com.tayler.pizzzaapp.repository.model

import com.tayler.pizzzaapp.entity.ParentOrderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
class ParentOrderResponse(
    @SerialName("uid")
    val uid: String? = "",
    @SerialName("nameClient")
    val nameClient: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("price")
    val price: String? = "",
    @SerialName("phone")
    val phone: String? = "",
    @SerialName("date")
    val date: String? = "",
    @SerialName("state")
    val state: String? = ""
) {
    companion object {
        fun List<ParentOrderResponse>.loadParentOrder() = this.map {
            ParentOrderModel(
                uid = it.uid ?: "",
                nameClient = it.nameClient ?: "",
                description = it.description ?: "",
                price = it.price ?: "",
                phone = it.phone ?: "",
                date = it.date ?: "",
                state = it.state ?: ""
            )
        }
    }
}
