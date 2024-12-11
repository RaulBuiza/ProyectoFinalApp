package com.rbr.proyectofinal.model

data class Photo(
    val photoId: String?,
    val link: String?,
    var likes: Int?
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "photoId" to this.photoId,
            "link" to this.link,
            "likes" to this.likes
        )
    }
}