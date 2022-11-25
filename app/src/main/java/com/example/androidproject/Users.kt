package com.example.androidproject

data class Users(
    var uid : String ?= null,
    var name :String ?= null,
    var profileimageUrl : String ?= "https://firebasestorage.googleapis.com/v0/b/anroid-project.appspot.com/o/profileimages%2FIMAGE%2020221122_132338_.png?alt=media&token=d2460870-9383-49c0-ba37-52d2b291bcb1",
    var friends : ArrayList<String> ?= null
)
