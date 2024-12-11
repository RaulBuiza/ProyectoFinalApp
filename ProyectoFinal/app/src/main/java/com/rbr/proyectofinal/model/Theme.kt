package com.rbr.proyectofinal.model

data class Theme(
    val themeId: String?,
    val theme: String?
){
    fun toMap(): Map<String, Any?>{
        return mapOf(
            "themeId" to this.themeId,
            "theme" to this.theme
        )
    }
}