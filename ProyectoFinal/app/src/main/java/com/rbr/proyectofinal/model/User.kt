package com.rbr.proyectofinal.model

import com.google.firebase.firestore.DocumentReference

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profilePhoto: String = "",
    val photos: List<DocumentReference> = emptyList(),
) {
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "userId" to this.userId,
            "name" to this.name,
            "email" to this.email,
            "photos" to photos.map { it.path }
        )
    }
}