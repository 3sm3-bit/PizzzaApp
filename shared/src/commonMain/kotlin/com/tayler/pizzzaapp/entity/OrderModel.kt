package com.tayler.pizzzaapp.entity

data class OrderModel (
    val ui : String,
    val nameClient : String,
    val quantity : String,
    val nameProduct : String,
    val tamanio : String,
    val typeDough : String,
    val cheeseFilledCrust : String,
    val note : String,
    val phone : String,
    val price : String,
    val priceTotal : String,
    val state : String,
    val date : String,
    val idOrder: String
)