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
fun List<ParentOrderModel>.toEntityList() = map { it.toEntity() }

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

fun ProductEntity.toModel() = com.tayler.pizzzaapp.entity.ProductModel(
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

fun com.tayler.pizzzaapp.entity.ProductModel.toEntity() = ProductEntity(
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
fun List<com.tayler.pizzzaapp.entity.ProductModel>.toProductEntityList() = map { it.toEntity() }
