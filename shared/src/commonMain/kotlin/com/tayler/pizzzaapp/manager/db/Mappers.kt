package com.tayler.pizzzaapp.manager.db

import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.repository.model.ParentOrderResponse

fun ParentOrderEntity.toModel() = ParentOrderModel(
    uid = uid,
    nameClient = nameClient,
    description = description,
    price = price,
    phone = phone,
    date = date,
    state = state
)

fun ParentOrderModel.toEntity() = ParentOrderEntity(
    uid = uid,
    nameClient = nameClient,
    description = description,
    price = price,
    phone = phone,
    date = date,
    state = state
)

fun List<ParentOrderEntity>.toModelList() = map { it.toModel() }
fun List<ParentOrderModel>.toEntityList() = map { it.toEntity() }

fun List<ParentOrderResponse>.toEntityListFromResponse() = map {
    ParentOrderEntity(
        uid = it.uid ?: "",
        nameClient = it.nameClient ?: "",
        description = it.description ?: "",
        price = it.price ?: "",
        phone = it.phone ?: "",
        date = it.date ?: "",
        state = it.state ?: ""
    )
}
