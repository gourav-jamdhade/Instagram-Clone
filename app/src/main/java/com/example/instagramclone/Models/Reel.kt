package com.example.instagramclone.Models

class Reel {

    var reelUrl: String = ""
    var caption: String = ""
    var profile_link: String? = null
    var likeCount: Int = 0


    constructor()
    constructor(reelUrl: String, caption: String) {
        this.reelUrl = reelUrl
        this.caption = caption
    }

    constructor(reelUrl: String, caption: String, profile_link: String) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.profile_link = profile_link
    }




}