package com.example.androidproject

import org.w3c.dom.Comment
data class ContentDTO (var explain : String?=null, var imageUrl : String ?= null,
                       var uid : String ?= null, var userId : String? = null,
                       var timestamp : Long? = null, var favoriteCount : Int? = null,
                       var favorites : MutableMap<String, Boolean> = HashMap() ) {
    data class Comment(var uid : String ?= null, var userId: String ?= null,
                       var comment: String ?= null, var timestamp: Long? = null)

}