package com.tayler.pizzzaapp.repository.db

import com.tayler.pizzzaapp.model.ParentOrderModel
import com.tayler.pizzzaapp.model.ProductModel
import com.tayler.pizzzaapp.repository.db.entity.ParentOrderEntity
import com.tayler.pizzzaapp.repository.db.entity.ProductEntity
import com.tayler.pizzzaapp.repository.network.model.ParentOrderResponse

fun ParentOrderEntity.toModel() = ParentOrderModel(
    uid = uid,
    nameClient = nameClient,
    description = description,
    price = price,
    phone = phone,
    date = date,
    state = state,
    address = address,
    reception = reception,
    orders = emptyList() // No orders stored in Entity for now
)

fun ParentOrderModel.toEntity() = ParentOrderEntity(
    uid = uid,
    nameClient = nameClient,
    description = description,
    price = price,
    phone = phone,
    date = date,
    state = state,
    address = address,
    reception = reception
)

fun List<ParentOrderEntity>.toModelList() = map { it.toModel() }

fun List<ParentOrderResponse>.toEntityListFromResponse() = map {
    ParentOrderEntity(
        uid = it.uid ?: "",
        nameClient = it.nameClient ?: "",
        description = it.description ?: "",
        price = it.price ?: "",
        phone = it.phone ?: "",
        date = it.date ?: "",
        state = it.state ?: "",
        address = it.address ?: "",
        reception = it.reception ?: ""
    )
}

fun ProductEntity.toModel() = ProductModel(
    uid = uid,
    nameProduct = nameProduct,
    type = type,
    price = price,
    tamanio = tamanio,
    description = description,
    priceChosse = priceChosse,
    currency = currency,
    currencySymbol = currencySymbol,
    state = state
)

fun ProductModel.toEntity() = ProductEntity(
    uid = uid,
    nameProduct = nameProduct,
    type = type,
    price = price,
    tamanio = tamanio,
    description = description,
    priceChosse = priceChosse,
    currency = currency,
    currencySymbol = currencySymbol,
    state = state
)

fun List<ProductEntity>.toProductModelList() = map { it.toModel() }
fun List<ProductModel>.toProductEntityList() = map { it.toEntity() }
