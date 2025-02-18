package com.example.instagramclone.Models

class User {

    var image: String? = null
    var name:String?=null
    var email:String?=null
    var password:String?=null
    var likedReels:ArrayList<String>? = null

    constructor()
    constructor(image: String?, name: String?, email: String?, password: String?) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
    }

    constructor(image: String?, name: String?, email: String?) {
        this.image = image
        this.name = name
        this.email = email
    }

    constructor(
        image: String?,
        name: String?,
        email: String?,
        password: String?,
        likedReels: ArrayList<String>?
    ) {
        this.image = image
        this.name = name
        this.email = email
        this.password = password
        this.likedReels = likedReels
    }


}